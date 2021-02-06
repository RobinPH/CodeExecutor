package io.github.robinph.codeexecutor.database;

import io.github.robinph.codeexecutor.Common;
import io.github.robinph.codeexecutor.codeeditor.CodeEditor;
import io.github.robinph.codeexecutor.config.ConfigManager;
import io.github.robinph.codeexecutor.utils.Prefix;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Database {
    public interface Table {
        String EDITORS = "editors";
    }

    public interface Column {
        String PLAYER_UUID = "player_uuid";
        String EDITOR_NAME = "editor_name";
        String EDITOR_UUID = "editor_uuid";
        String EDITOR = "editor";
    }

    public interface Query {
        String CREATE_EDITORS_TABLE = "CREATE TABLE IF NOT EXISTS " + Table.EDITORS + " (" + Column.PLAYER_UUID + " VARCHAR(36) NOT NULL, " + Column.EDITOR_NAME + " VARCHAR(256) NOT NULL, " + Column.EDITOR_UUID + " VARCHAR(36) NOT NULL, " + Column.EDITOR + " MEDIUMTEXT NOT NULL)";

        String GET_EDITOR_BY_NAME = "SELECT " + Column.EDITOR + " FROM " + Table.EDITORS + " WHERE " + Column.PLAYER_UUID + " = ? AND " + Column.EDITOR_NAME + " = ?";
        String GET_EDITOR_BY_UUID = "SELECT " + Column.EDITOR + " FROM " + Table.EDITORS + " WHERE " + Column.EDITOR_UUID + " = ?";
        String GET_ALL_EDITORS_OF_PLAYER = "SELECT " + Column.EDITOR + " FROM " + Table.EDITORS + " WHERE " + Column.PLAYER_UUID + " = ?";

        String INSERT_EDITOR = "INSERT INTO " + Table.EDITORS + " VALUES(?, ?, ?, ?)";
        String UPDATE_EDITOR = "UPDATE " + Table.EDITORS + " SET " + Column.EDITOR + " = ?, " + Column.EDITOR_NAME + " = ? WHERE " + Column.EDITOR_UUID + " = ?";
        String DELETE_EDITOR = "DELETE FROM " + Table.EDITORS + " WHERE editor_uuid = ?";
    }

    private JavaPlugin plugin;

    private Database() { }
    public @Getter static final Database instance = new Database();

    public Response<CodeEditor> getEditor(Player player, String editorName) {
        String query = Query.GET_EDITOR_BY_NAME;
        try (Connection con = this.getConnection()) {
            PreparedStatement statement = con.prepareStatement(query);
            statement.setString(1, player.getUniqueId().toString());
            statement.setString(2, editorName);

            ResultSet res = statement.executeQuery();

            if (!res.next()) {
                return new Response<>(Response.Code.DOES_NOT_EXIST, null, editorName + " does not exist.");
            }

            CodeEditor editor = Common.getCodeExecutorGson().getGson().fromJson(res.getString(Column.EDITOR), CodeEditor.class);

            res.close();

            return new Response<>(Response.Code.SUCCESS, editor, null);
        } catch (SQLException | ClassNotFoundException e) {
            return new Response<>(Response.Code.UNKNOWN_ERROR, null, e.getMessage());
        }
    }

    public Response<CodeEditor> getEditor(UUID editorUUID) {
        String query = Query.GET_EDITOR_BY_UUID;
        try (Connection con = this.getConnection()) {
            PreparedStatement statement = con.prepareStatement(query);
            statement.setString(1, editorUUID.toString());

            ResultSet res = statement.executeQuery();

            if (!res.next()) {
                return new Response<>(Response.Code.DOES_NOT_EXIST, null, editorUUID.toString() + " does not exist.");
            }

            CodeEditor editor = Common.getCodeExecutorGson().getGson().fromJson(res.getString(Column.EDITOR), CodeEditor.class);
            res.close();

            return new Response<>(Response.Code.SUCCESS, editor, null);
        } catch (SQLException | ClassNotFoundException e) {
            return new Response<>(Response.Code.UNKNOWN_ERROR, null, e.getMessage());
        }
    }

    public Response<List<CodeEditor>> getEditors(Player player) {
        String query = Query.GET_ALL_EDITORS_OF_PLAYER;
        try (Connection con = this.getConnection()) {
            PreparedStatement statement = con.prepareStatement(query);
            statement.setString(1, player.getUniqueId().toString());
            List<CodeEditor> editors = new ArrayList<>();

            ResultSet res = statement.executeQuery();

            while (res.next()) {
                editors.add(Common.getCodeExecutorGson().getGson().fromJson(res.getString(Column.EDITOR), CodeEditor.class));
            }

            return new Response<>(Response.Code.SUCCESS, editors, "");
        } catch (SQLException | ClassNotFoundException e) {
            return new Response<>(Response.Code.UNKNOWN_ERROR, null, e.getMessage());
        }
    }


    public Response<Boolean> newEditor(CodeEditor editor) {
        String query = Query.INSERT_EDITOR;

        try (Connection con = this.getConnection()) {
            String editorJSON = Common.getCodeExecutorGson().getGson().toJson(editor, CodeEditor.class);

            Response<List<CodeEditor>> countEditor = this.getEditors(editor.getPlayer());

            if (countEditor.isSuccess()) {
                int maxFiles = Common.getConfig().get(ConfigManager.Variable.MAX_FILES_PER_PLAYER);
                if (countEditor.getContent().size() > maxFiles) {
                    return new Response<>(Response.Code.UNKNOWN_ERROR, false, "Exceeded the maximum (" + maxFiles + ") number of files per player.");
                }
            }

            Response<CodeEditor> checkIfNameExists = this.getEditor(editor.getPlayer(), editor.getName());
            if (checkIfNameExists.isSuccess()) {
                if (!checkIfNameExists.getContent().getUuid().equals(editor.getUuid())) {
                    return new Response<>(Response.Code.ALREADY_EXISTS, false, "Name is already taken.");
                }
            }

            if (getEditor(editor.getUuid()).isSuccess()) {
                return this.updateEditor(editor);
            }

            PreparedStatement insertStatement = con.prepareStatement(query);
            insertStatement.setString(1, editor.getPlayer().getUniqueId().toString());
            insertStatement.setString(2, editor.getName());
            insertStatement.setString(3, editor.getUuid().toString());
            insertStatement.setString(4, editorJSON);


            insertStatement.execute();

            return new Response<>(Response.Code.SUCCESS, true, Prefix.SUCCESS_COLOR + " " + editor.getName() + " saved!");
        } catch (SQLException | ClassNotFoundException e) {
            return new Response<>(Response.Code.UNKNOWN_ERROR, false, e.getMessage());
        }
    }

    public Response<Boolean> updateEditor(CodeEditor editor) {
        String query = Query.UPDATE_EDITOR;

        try (Connection con = this.getConnection()) {
            String editorJSON = Common.getCodeExecutorGson().getGson().toJson(editor, CodeEditor.class);

            Response<CodeEditor> checkIfNameExists = this.getEditor(editor.getPlayer(), editor.getName());
            if (checkIfNameExists.isSuccess()) {
                if (!checkIfNameExists.getContent().getUuid().equals(editor.getUuid())) {
                    return new Response<>(Response.Code.ALREADY_EXISTS, false, "Name is already taken.");
                }
            }

            if (getEditor(editor.getUuid()).isError()) {
                this.newEditor(editor);
            }

            PreparedStatement overwriteState = con.prepareStatement(query);

            overwriteState.setString(1, editorJSON);
            overwriteState.setString(2, editor.getName());
            overwriteState.setString(3, editor.getUuid().toString());

            overwriteState.execute();

            return new Response<>(Response.Code.SUCCESS, true, Prefix.SUCCESS_COLOR + " " + editor.getName() + " saved!");
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return new Response<>(Response.Code.UNKNOWN_ERROR, false, e.getMessage());
        }
    }

    public Response<Boolean> deleteEditor(CodeEditor editor) {
        String query = Query.DELETE_EDITOR;
        try (Connection con = this.getConnection()) {
            if (getEditor(editor.getUuid()).isError()) {
                return new Response<>(Response.Code.DOES_NOT_EXIST, false, editor.getName() + " does not exist.");
            }

            PreparedStatement deleteStatement = con.prepareStatement(query);
            deleteStatement.setString(1, editor.getUuid().toString());

            deleteStatement.execute();

            return new Response<>(Response.Code.SUCCESS, true, editor.getName() + " has been deleted.");
        } catch (SQLException | ClassNotFoundException e) {
            return new Response<>(Response.Code.UNKNOWN_ERROR, false, e.getMessage());
        }
    }

    public Connection getConnection() throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");

        String path = path(plugin.getDataFolder().toString().replace("./", ""), "database.db");
        return DriverManager.getConnection("jdbc:sqlite:" + path);
    }

    public void init(JavaPlugin plugin) {
        this.plugin = plugin;

        try {
            createTable();
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to connect to database.");
            e.printStackTrace();
        }
    }

    public void createTable() throws SQLException {
        try (Connection con = this.getConnection()) {
            PreparedStatement statement = con.prepareStatement(Query.CREATE_EDITORS_TABLE);

            statement.execute();
        } catch (ClassNotFoundException ignored) { }
    }

    public static String path(String... args) {
        StringBuilder path = new StringBuilder();

        for (String arg : args) {
            path.append(arg).append(File.separator);
        }

        return path.toString();
    }
}
