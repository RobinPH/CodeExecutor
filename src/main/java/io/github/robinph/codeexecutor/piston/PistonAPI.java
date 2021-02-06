package io.github.robinph.codeexecutor.piston;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.robinph.codeexecutor.Common;
import io.github.robinph.codeexecutor.codeeditor.CodeEditor;
import io.github.robinph.codeexecutor.core.chat.ChatBuilder;
import io.github.robinph.codeexecutor.utils.FontUtils;
import io.github.robinph.codeexecutor.utils.Prefix;
import lombok.Getter;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class PistonAPI {
    public static @Getter final String EXECUTE_URL = "https://emkc.org/api/v1/piston/execute";
    public static @Getter final String VERSIONS_URL = "https://emkc.org/api/v1/piston/versions";
    public static @Getter final Map<String, Language> languages = new HashMap<>();

    public static void execute(CodeEditor editor, String stdin) {
        if (editor.getLanguage() == null) {
            editor.getPlayer().sendMessage(Prefix.ERROR + "Error language not found. Set language: §7/code language <language>");
            return;
        }

        JsonObject request = PistonAPI.requestJson(editor.getLanguage().getName(), editor.getText(), stdin, editor.getArgv());

        editor.addFooterMessage("§7Executing...");
        editor.render();

        JsonObject responseJson = PistonAPI.post(PistonAPI.EXECUTE_URL, request);
        int code = responseJson.get("code").getAsInt();
        JsonObject response = responseJson.get("response").getAsJsonObject();

        if (code != 200) {
            String message;
            if (response.has("message")) {
                message = response.get("message").getAsString();
                editor.addFooterMessage(Prefix.ERROR_COLOR + "Error Code " + code + ": " + message);

                ChatBuilder tooltip = new ChatBuilder("§a§lPiston§r");

                tooltip.newLine();
                tooltip.newLineAppend("  §bError: §7" + code);
                tooltip.newLineAppend("  §bMessage: §7" + FontUtils.trimString(message.replace("\n", "\\n"), 6 * 36));
                tooltip.newLine();
                tooltip.newLineAppend("§7§ohttps://emkc.org/api/v1/piston");

                editor.getPlayer().spigot().sendMessage(new ChatBuilder("§8§l[§a§lPiston§8§l] §7Finished!").addHoverText(tooltip.build().toPlainText()).build());
            } else {
                editor.addFooterMessage(Prefix.ERROR_COLOR + "Error Code " + code);
            }
        } else {
            boolean ran = response.get("ran").getAsBoolean();
            String lang = response.get("language").getAsString();
            String version = response.get("version").getAsString();
            String output = response.get("output").getAsString();
            String stdout = response.get("stdout").getAsString();
            String stderr = response.get("stderr").getAsString();

            if (output.getBytes().length <= CodeEditor.maxSizePerOutput) {
                String[] outs = output.split("\n");
                List<String> outTrimmed = new ArrayList<>();
                Language language = PistonAPI.getLanguage(lang);

                ChatBuilder builder = new ChatBuilder();

                String prefix = " " + (language != null ? language.getPrefix() : lang);

                if (outs.length > CodeEditor.maxOutputLine) {
                    int half = CodeEditor.maxOutputLine / 2;

                    for (int i = 0; i < half; i++) {
                        outTrimmed.add(prefix + " §8» §7" + outs[i]);
                    }
                    outTrimmed.add(prefix +" §c§oSkipped " + (outs.length - CodeEditor.maxOutputLine) + " lines. Output limit is " + CodeEditor.maxOutputLine + " lines.");

                    for (int i = outs.length - (CodeEditor.maxOutputLine - half); i < outs.length; i++) {
                        outTrimmed.add(prefix + " §8» §7" + outs[i]);
                    }
                } else {
                    for (String out : outs) {
                        outTrimmed.add(prefix + " §8» §7" + out);
                    }
                }

                builder.mergeNewLine(outTrimmed.toArray(new String[0]));

                editor.getPlayer().spigot().sendMessage(builder.build());
            } else {
                editor.getPlayer().sendMessage(Prefix.ERROR + "Failed to print the output. Output too big (" + output.getBytes().length + " bytes, max " + CodeEditor.maxSizePerOutput + ")");
            }

            ChatBuilder tooltip = new ChatBuilder("§a§lPiston§r");

            tooltip.newLine();
            tooltip.newLineAppend("  §bRan: §7" + ran);
            tooltip.newLineAppend("  §bLanguage: §7" + lang);
            tooltip.newLineAppend("  §bVersion: §7" + version);
            tooltip.newLineAppend("  §bOutput: §7" + FontUtils.trimString(output.replace("\n", "\\n"), 6 * 36));
            tooltip.newLineAppend("  §bStdout: §7" + FontUtils.trimString(stdout.replace("\n", "\\n"), 6 * 36));
            tooltip.newLineAppend("  §bStderr: §7" + FontUtils.trimString(stderr.replace("\n", "\\n"), 6 * 36));
            tooltip.newLine();
            tooltip.newLineAppend("§7§ohttps://emkc.org/api/v1/piston");

            editor.getPlayer().spigot().sendMessage(new ChatBuilder("§8§l[§a§lPiston§8§l] §7Finished!").addHoverText(tooltip.build().toPlainText()).build());
        }
    }

    public static void syncLanguages() {
        new BukkitRunnable() {
            @Override
            public void run() {
                JsonObject json = PistonAPI.fetchLanguages();
                int code = json.get("code").getAsInt();

                if (code != 200) {
                    return;
                }

                JsonArray response = json.get("response").getAsJsonArray();

                response.forEach(elm -> {
                    JsonObject obj = elm.getAsJsonObject();
                    String name = obj.get("name").getAsString();

                    if (!PistonAPI.getLanguages().containsKey(name)) {
                        PistonAPI.addLanguage(new Language(name, "§8§o" + name));
                    }

                    Language lang = PistonAPI.getLanguage(name);

                    if (lang != null) {
                        JsonArray aliases = obj.get("aliases").getAsJsonArray();
                        aliases.forEach(a -> {
                            String alias = a.getAsString();
                            lang.addAlias(alias);
                        });

                        String version = obj.get("version").getAsString();
                        lang.setVersion(version);
                    }
                });
            }
        }.runTaskAsynchronously(Common.getPlugin());
    }

    public static JsonObject fetchLanguages() {
        return PistonAPI.get(PistonAPI.VERSIONS_URL);
    }

    public static JsonObject get(String url) {
        try {
            HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
            con.setRequestMethod("GET");

            StringBuilder builder = new StringBuilder();
            try(BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = br.readLine()) != null) {
                    builder.append(line.trim());
                }
            }

            JsonObject response = new JsonObject();

            response.addProperty("code", con.getResponseCode());

            response.add("response", new JsonParser().parse(builder.toString()));

            return response;

        } catch (IOException e) {
            JsonObject response = new JsonObject();

            response.addProperty("code", -1);
            response.add("response", new JsonObject());

            return response;
        }
    }

    public static JsonObject post(String url, JsonObject request) {
        try {
            HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();

            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Accept", "application/json");
            con.setDoOutput(true);

            try(OutputStream os = con.getOutputStream()) {
                byte[] input = request.toString().getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            JsonObject response = new JsonObject();

            response.addProperty("code", con.getResponseCode());
            response.add("response", new JsonObject());
            if (con.getResponseCode() == 400) {
                return response;
            }


            StringBuilder builder = new StringBuilder();
            try(BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = br.readLine()) != null) {
                    builder.append(line.trim());
                }
            }

            response.add("response", new JsonParser().parse(builder.toString()).getAsJsonObject());

            return response;

        } catch (IOException e) {
            JsonObject response = new JsonObject();
            response.addProperty("code", -1);

            return response;
        }
    }

    public static JsonObject requestJson(String language, String source, String stdin, String... args) {
        JsonObject obj = new JsonObject();

        obj.addProperty("language", language);
        obj.addProperty("source", source);
        if (stdin != null) obj.addProperty("stdin", stdin);

        JsonArray arr = new JsonArray();

        for (String arg : args) {
            arr.add(arg);
        }

        if (args.length > 0) obj.add("args", arr);

        return obj;
    }

    public static void registerLanguages() {
        // Incase syncVersions() failed
        PistonAPI.addLanguage("awk", "§8AWK");
        PistonAPI.addLanguage("bash", "§8Bash");
        PistonAPI.addLanguage("brainfuck", "§8Brainf§ku§r§8ck");
        PistonAPI.addLanguage("c", "§9C");
        PistonAPI.addLanguage("cpp", "§9C++");
        PistonAPI.addLanguage("csharp", "§9C#");
        PistonAPI.addLanguage("crystal", "§bC#");
        PistonAPI.addLanguage("d", "§cD");
        PistonAPI.addLanguage("dash", "§8Dash");
        PistonAPI.addLanguage("deno", "§8De§fno");
        PistonAPI.addLanguage("elixir", "§5Elixir");
        PistonAPI.addLanguage("emacs", "§5Emacs");
        PistonAPI.addLanguage("go", "§3Go");
        PistonAPI.addLanguage("haskell", "§5Has§dkell");
        PistonAPI.addLanguage("java", "§6Java");
        PistonAPI.addLanguage("jelly", "§8Jelly");
        PistonAPI.addLanguage("julia", "§3J§0u§cl§ai§da");
        PistonAPI.addLanguage("kotlin", "§6Kot§9lin");
        PistonAPI.addLanguage("lisp", "§8Lisp");
        PistonAPI.addLanguage("lua", "§9Lua");
        PistonAPI.addLanguage("nasm", "§9NASM");
        PistonAPI.addLanguage("nasm64", "§9NASM64");
        PistonAPI.addLanguage("nim", "§enim");
        PistonAPI.addLanguage("node", "§aNode");
        PistonAPI.addLanguage("osabie", "§8O5ab1e");
        PistonAPI.addLanguage("paradoc", "§8Paradoc");
        PistonAPI.addLanguage("perl", "§8Perl");
        PistonAPI.addLanguage("php", "§9PHP");
        PistonAPI.addLanguage("python3", "§3Python§e3");
        PistonAPI.addLanguage("python2", "§3Python§e2");
        PistonAPI.addLanguage("ruby", "§cRuby");
        PistonAPI.addLanguage("rust", "§8Rust");
        PistonAPI.addLanguage("swift", "§6Swift");
        PistonAPI.addLanguage("typescript", "§3Type§bScript");
        PistonAPI.addLanguage("zig", "§6Zig");

        PistonAPI.syncLanguages();
    }

    public static void addLanguage(Language language) {
        PistonAPI.getLanguages().put(language.getName(), language);
    }

    public static void addLanguage(String name, String prefix, String ...aliases) {
        PistonAPI.getLanguages().put(name, new Language(name, prefix, aliases));
    }

    public static Language getLanguage(String language) {
        if (language == null) {
            return null;
        }

        for (Language lang : PistonAPI.getLanguages().values()) {
            if (language.equalsIgnoreCase(lang.getName())) {
                return lang;
            }
            for (String alias : lang.getAliases()) {
                if (language.equalsIgnoreCase(alias)) {
                    return lang;
                }
            }
        }

        return null;
    }

    public static List<String> getLanguageList() {
        List<String> languages = new ArrayList<>();

        for (Language lang : PistonAPI.getLanguages().values()) {
            languages.add(lang.getName());
        }

        return languages;
    }

    public static TextComponent getLanguagesTextComponent() {
        List<String> languages = PistonAPI.getLanguageList();
        ChatBuilder builder = new ChatBuilder(Prefix.NORMAL + "Supported Languages (" + languages.size() + "): §f");

        for (int i = 0; i < languages.size(); i++) {
            String langName = languages.get(i);
            builder.append(new ChatBuilder(langName)
                    .addHoverText("/code language " + langName)
                    .runCommand("code language " + langName)
            );

            if (i < languages.size() - 1) {
                builder.append(", ");
            }
        }

        return builder.build();
    }
}
