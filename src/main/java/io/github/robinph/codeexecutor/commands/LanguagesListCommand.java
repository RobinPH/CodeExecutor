package io.github.robinph.codeexecutor.commands;

import io.github.robinph.codeexecutor.core.command.AbstractCommand;
import io.github.robinph.codeexecutor.piston.PistonAPI;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LanguagesListCommand extends AbstractCommand {
    public LanguagesListCommand() {
        super("languages");
    }

    @Override
    public void execute(CommandSender sender, String ...args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            player.spigot().sendMessage(PistonAPI.getLanguagesTextComponent());
        }
    }
}
