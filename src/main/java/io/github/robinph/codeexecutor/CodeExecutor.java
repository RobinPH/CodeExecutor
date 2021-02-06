package io.github.robinph.codeexecutor;

import io.github.robinph.codeexecutor.bstats.MetricsLite;
import io.github.robinph.codeexecutor.codeeditor.CodeEditorManager;
import io.github.robinph.codeexecutor.commands.CodeEditorCommand;
import io.github.robinph.codeexecutor.config.ConfigManager;
import io.github.robinph.codeexecutor.database.CodeExecutorGson;
import io.github.robinph.codeexecutor.database.Database;
import io.github.robinph.codeexecutor.piston.PistonAPI;
import io.github.robinph.codeexecutor.piston.PistonQueue;
import io.github.robinph.codeexecutor.utils.FontMetrics;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

public final class CodeExecutor extends JavaPlugin {
    private @Getter static CodeExecutor instance = null;
    private @Getter final CodeEditorManager codeEditorManager = CodeEditorManager.getInstance();
    private @Getter final ConfigManager configManager = new ConfigManager(this.getConfig());
    private @Getter final PistonQueue pistonQueue = PistonQueue.getInstance();
    private @Getter final CodeExecutorGson gson = CodeExecutorGson.getInstance();
    private @Getter final Database database = Database.getInstance();

    @Override
    public void onEnable() {
        instance = this;

        this.getCommand("code").setExecutor(new CodeEditorCommand());

        PistonAPI.registerLanguages();
        FontMetrics.registerDefault();

        this.getPistonQueue().init(this);
        this.getDatabase().init(this);

        this.getConfig().options().copyDefaults(true);
        this.saveConfig();

        int bStatsID = 10230;
        MetricsLite metrics = new MetricsLite(this, bStatsID);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
