package io.github.robinph.codeexecutor.piston;

import io.github.robinph.codeexecutor.codeeditor.CodeEditor;
import io.github.robinph.codeexecutor.utils.Prefix;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.LinkedList;
import java.util.Queue;

public class PistonQueue extends BukkitRunnable {
    private @Getter final Queue<PistonQueueItem> queue = new LinkedList<>();
    private @Getter int slotsAvailable = 5;
    private @Getter JavaPlugin plugin;

    private static final PistonQueue instance = new PistonQueue();

    public static PistonQueue getInstance() { return instance; }

    public void init(JavaPlugin plugin) {
        this.plugin = plugin;

        this.runTaskTimerAsynchronously(this.plugin, 0,5L);
    }

    public void add(CodeEditor editor, String stdin) {
        this.getQueue().add(new PistonQueueItem(editor.clone(), stdin));
        editor.addFooterMessage(Prefix.SUCCESS_COLOR + "In queue #" + this.getQueue().size());
        editor.render();
    }

    @Override
    public void run() {
        if (!this.getQueue().isEmpty() && this.slotsAvailable > 0) {
            this.slotsAvailable--;

            PistonQueueItem next = this.getQueue().remove();
            next.execute();

            this.slotsAvailable++;
        }
    }

    @RequiredArgsConstructor
    public static class PistonQueueItem {
        private @Getter final CodeEditor editor;
        private @Getter final String stdin;

        public void execute() {
            PistonAPI.execute(this.getEditor(), this.getStdin());
        }
    }
}
