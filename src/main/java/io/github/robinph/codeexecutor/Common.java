package io.github.robinph.codeexecutor;

import io.github.robinph.codeexecutor.codeeditor.CodeEditorManager;
import io.github.robinph.codeexecutor.config.ConfigManager;
import io.github.robinph.codeexecutor.database.CodeExecutorGson;
import io.github.robinph.codeexecutor.database.Database;
import io.github.robinph.codeexecutor.piston.PistonQueue;

public interface Common {
    static CodeExecutor getPlugin() { return CodeExecutor.getInstance(); }
    static CodeEditorManager getCodeEditorManager() { return getPlugin().getCodeEditorManager(); }
    static ConfigManager getConfig() { return getPlugin().getConfigManager(); }
    static PistonQueue getPistonQueue() { return getPlugin().getPistonQueue(); }
    static CodeExecutorGson getCodeExecutorGson() { return getPlugin().getGson(); }
    static Database getDatabase() { return getPlugin().getDatabase(); }
}
