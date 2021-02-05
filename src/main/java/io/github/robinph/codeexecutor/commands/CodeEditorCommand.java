package io.github.robinph.codeexecutor.commands;

import io.github.robinph.codeexecutor.Common;
import io.github.robinph.codeexecutor.commands.argv.ArgvCommand;
import io.github.robinph.codeexecutor.commands.line.LineCommand;
import io.github.robinph.codeexecutor.core.command.AbstractCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.TabCompleteEvent;

import java.util.List;

public class CodeEditorCommand extends AbstractCommand implements CommandExecutor, Listener {
    public CodeEditorCommand() {
        super("code");

        this.addChild(new NewCommand());
        this.addChild(new OpenCommand());
        this.addChild(new CloseCommand());
        this.addChild(new RunCommand());
        this.addChild(new SetLanguageCommand());
        this.addChild(new LanguagesListCommand());
        this.addChild(new ScrollCommand());
        this.addChild(new GoToCommand());
        this.addChild(new LineCommand());
        this.addChild(new ArgvCommand());
        this.addChild(new StdinCommand());

        this.addChild(new TestCommand());

        Bukkit.getServer().getPluginManager().registerEvents(this, Common.getPlugin());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        this.execute(sender, args);
        return true;
    }

    @EventHandler
    public void onTabCompleteEvent(TabCompleteEvent event) {
        if (!(event.getSender() instanceof Player)) {
            return;
        }

        String[] args = event.getBuffer().split(" ");

        // /command arg arg = [/command, arg arg]
        String[] tabCompleteArgs = event.getBuffer().split(" ", 2);
        String command = tabCompleteArgs[0], leftOver = null;

        if (tabCompleteArgs.length > 1) {
            leftOver = tabCompleteArgs[1];
        }

        Player player = (Player) event.getSender();

        if (event.getBuffer().endsWith(" ") && tabCompleteArgs.length == 1) {
            command += " ";
        }

        if (event.getBuffer().endsWith(" ") || args.length == 1) {
            args[args.length - 1] += " ";
        }

        if (command.startsWith("/")) {
            PluginCommand editorCommand = Common.getPlugin().getCommand(this.getCmd());
            PluginCommand receivedCommand = Common.getPlugin().getCommand(command.substring(1).replace(" ", ""));

            if (editorCommand != null && receivedCommand != null) {
                if (editorCommand.getExecutor().equals(receivedCommand.getExecutor())) {
                    for (AbstractCommand child : this.getChildren()) {
                        child.onTabComplete(event, leftOver);
                        event.setCompletions(this.getCompletion(player, args));
                    }
                }
            }
        }
    }

    public List<String> getCompletion(Player player, String ...args) {
        if (args.length == 1) {
            if (args[0].endsWith(" ")) {
                return this.getChildrenCommands();
            }
        }

        return this.getTabComplete(player, args);
    }
}
