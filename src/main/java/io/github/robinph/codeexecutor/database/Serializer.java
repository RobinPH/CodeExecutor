package io.github.robinph.codeexecutor.database;

import com.google.gson.*;
import io.github.robinph.codeexecutor.codeeditor.CodeEditor;

import java.lang.reflect.Type;
import java.util.Map;

public interface Serializer {
    JsonSerializer<CodeEditor> codeEditor = new JsonSerializer<CodeEditor>() {
        @Override
        public JsonElement serialize(CodeEditor src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject obj = new JsonObject();

            obj.addProperty("name", src.getName());
            obj.addProperty("player", src.getPlayer().getUniqueId().toString());
            obj.addProperty("language", src.getLanguage() == null ? null : src.getLanguage().getName());
            obj.addProperty("uuid", src.getUuid().toString());

            obj.addProperty("currentLine", src.getCurrentLine());

            JsonObject lines = new JsonObject();
            for (Map.Entry<Integer, String> line : src.getLines().entrySet()) {
                lines.addProperty(String.valueOf(line.getKey()), line.getValue());
            }
            obj.add("lines", lines);

            JsonArray argv = new JsonArray();
            for (String arg : src.getArgv()) {
                argv.add(arg);
            }
            obj.add("argv", argv);

            obj.addProperty("requireStdin", src.isRequiresStdin());

            return obj;
        }
    };
}
