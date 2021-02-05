package io.github.robinph.codeexecutor.commands;

import io.github.robinph.codeexecutor.Common;
import io.github.robinph.codeexecutor.core.command.AbstractCommand;
import io.github.robinph.codeexecutor.utils.Prefix;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CloseCommand extends AbstractCommand {
    public CloseCommand() {
        super("close");
    }

    @Override
    public void execute(CommandSender sender, String ...args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            boolean success = Common.getCodeEditorManager().closeEditor(player);

            if (success) {
                player.sendMessage(Prefix.SUCCESS + "Editor closed.");
            } else {
                player.sendMessage(Prefix.WARNING + "You do not have an open editor. To open new one: /code new");
            }
        }
    }
}
