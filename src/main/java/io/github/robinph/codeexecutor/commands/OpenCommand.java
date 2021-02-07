package io.github.robinph.codeexecutor.commands;

import io.github.robinph.codeexecutor.Common;
import io.github.robinph.codeexecutor.codeeditor.CodeEditor;
import io.github.robinph.codeexecutor.core.argument.argument.StringArgument;
import io.github.robinph.codeexecutor.core.command.AbstractCommand;
import io.github.robinph.codeexecutor.database.Response;
import io.github.robinph.codeexecutor.utils.Prefix;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class OpenCommand extends AbstractCommand {
    public OpenCommand() {
        super("open");

        this.addArgument(new StringArgument("editor"));
        this.getArguments().setLastArgArbitraryLength(true);

        this.setDescription("Open an editor.");
    }

    @Override
    public void execute(CommandSender sender, String ...args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (args.length == 0) {
                CodeEditor editor = Common.getCodeEditorManager().getEditor(player);

                if (editor != null) {
                    editor.render();
                }
            } else {
                String editorName = String.join(" ", args);

                new BukkitRunnable() {

                    @Override
                    public void run() {
                        Response<CodeEditor> response = Common.getDatabase().getEditor(player, editorName);

                        if (response.isSuccess()) {
                            boolean success = Common.getCodeEditorManager().openEditor(response.getContent());

                            if (success) {
                                CodeEditor editor = Common.getCodeEditorManager().getEditor(player);

                                if (editor != null) {
                                    editor.setSaved(true);
                                    editor.render();
                                }
                            } else {
                                player.sendMessage(Prefix.ERROR + "Something went wrong while opening " + editorName);
                            }
                        } else {
                            player.sendMessage(Prefix.ERROR + editorName + " does not exist.");
                        }
                    }
                }.runTaskAsynchronously(Common.getPlugin());
            }
        }
    }
}
