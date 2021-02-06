package io.github.robinph.codeexecutor.database;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.robinph.codeexecutor.codeeditor.CodeEditor;
import lombok.Getter;

public class CodeExecutorGson {
    private final GsonBuilder gsonBuilder = new GsonBuilder();
    private @Getter final Gson gson;

    private @Getter static CodeExecutorGson instance = new CodeExecutorGson();

    private CodeExecutorGson() {
        this.registerSerializer();
        this.registerDeserializer();
        this.gson = gsonBuilder.create();
    }

    public void registerSerializer() {
        gsonBuilder.registerTypeAdapter(CodeEditor.class, Serializer.codeEditor);
    }

    public void registerDeserializer() {
        gsonBuilder.registerTypeAdapter(CodeEditor.class, Deserializer.codeEditor);
    }
}
