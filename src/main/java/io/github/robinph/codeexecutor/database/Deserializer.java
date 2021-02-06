package io.github.robinph.codeexecutor.database;

import com.google.gson.*;
import io.github.robinph.codeexecutor.codeeditor.CodeEditor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.UUID;

public interface Deserializer {
    JsonDeserializer<CodeEditor> codeEditor = new JsonDeserializer<CodeEditor>() {
        @Override
        public CodeEditor deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject obj = json.getAsJsonObject();

            String uuid = obj.get("player").getAsString();
            Player player = Bukkit.getPlayer(UUID.fromString(uuid));

            CodeEditor editor = new CodeEditor(player);
            editor.changeName(obj.get("name").getAsString());

            editor.setLanguage(obj.get("language") == null ? null : obj.get("language").getAsString());
            editor.setUuid(UUID.fromString(obj.get("uuid").getAsString()));

            editor.setCurrentLine(obj.get("currentLine").getAsInt());

            for (Map.Entry<String, JsonElement> line : obj.get("lines").getAsJsonObject().entrySet()) {
                editor.editLine(Integer.parseInt(line.getKey()), line.getValue().getAsString());
            }

            JsonArray argv = obj.get("argv").getAsJsonArray();
            editor.setArgvCount(argv.size());
            for (int i = 0; i < argv.size(); i++) {
                editor.setArgument(i, argv.get(i).getAsString());
            }

            editor.requireStdin(obj.get("requireStdin").getAsBoolean());

            editor.setHighlightedLine(0);
            editor.getFooterMessage().clear();
            editor.addFooterMessage("Editor opened.");

            return editor;
        }
    };
}
