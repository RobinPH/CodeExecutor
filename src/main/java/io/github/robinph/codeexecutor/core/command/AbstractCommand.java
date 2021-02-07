package io.github.robinph.codeexecutor.core.command;

import io.github.robinph.codeexecutor.core.argument.Argument;
import io.github.robinph.codeexecutor.core.argument.Arguments;
import io.github.robinph.codeexecutor.core.chat.ChatBuilder;
import io.github.robinph.codeexecutor.utils.Prefix;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.server.TabCompleteEvent;

import java.util.*;

public class AbstractCommand {
    private @Getter final String cmd;
    private @Getter final Map<String, AbstractCommand> childrenMap = new HashMap<>();
    private @Getter @Setter AbstractCommand parent = null;
    private @Getter final Arguments arguments = new Arguments(this);
    private @Getter @Setter String description;

    public AbstractCommand(String cmd) {
        this.cmd = cmd;
    }

    public void addChild(AbstractCommand child) {
        childrenMap.put(child.getCmd(), child);
        child.setParent(this);
    }

    public void execute(CommandSender sender, String ...args) {
        if (args.length > 0) {
            for (AbstractCommand child : this.getChildren()) {
                if (child.getCmd().equalsIgnoreCase(args[0])) {
                    child.execute(sender, Arrays.copyOfRange(args, 1, args.length));
                    return;
                }
            }
        }

        TextComponent usage = new TextComponent(Prefix.NORMAL + "Usage for §7");
        usage.addExtra(this.usage());
        sender.spigot().sendMessage(usage);
    }

    public List<String> getChildrenCommands() {
        List<String> commands = new ArrayList<>();

        for (AbstractCommand child : this.getChildren()) {
            commands.add(child.getCmd());
        }

        return commands;
    }

    public List<String> getTabComplete(Player player, String ...args) {
        List<String> commands = new ArrayList<>();

        if (args.length == 1) {
            if (args[0].endsWith(" ")) {
                return this.getChildrenCommands();
            }
        }

        if (args.length < 2) {
            return commands;
        }

        Optional<AbstractCommand> result = getChildren().stream().filter(child -> child.getCmd().equalsIgnoreCase(args[1].replace(" ", ""))).findAny();

        if (result.isPresent()) {
            return result.get().getTabComplete(player, Arrays.copyOfRange(args, 1, args.length));
        }


        for (String childCMD : this.getChildrenCommands()) {
            if (childCMD.startsWith(args[1])) {
                commands.add(childCMD);
            }
        }

        return commands;
    }

    public void addArgument(Argument argument) {
        this.getArguments().add(argument);
    }

    public BaseComponent usage() {
        List<String> commands = new ArrayList<>();
        AbstractCommand parent = this;

        while (parent != null) {
            commands.add(0, parent.getCmd());
            parent = parent.getParent();
        }

        String cmd = "/" + String.join(" ", commands) + " ";
        ChatBuilder builder = new ChatBuilder();
        builder.append(new ChatBuilder(cmd)
                .addHoverText(cmd)
                .suggestCommand(String.join(" ", commands) + " "));

        if (this.getChildrenMap().size() != 0) {
            for (AbstractCommand subCommand : this.getChildren()) {
                String subCommandCMD = subCommand.getCmd();

                ChatBuilder subCommandBuilder = new ChatBuilder("§3 »§b» §7" + cmd + "§f" + subCommandCMD + " §7" + subCommand.getArguments().argumentsUsage())
                                                .addHoverText(cmd + subCommandCMD);

                if (!subCommand.getChildren().isEmpty()) {
                    subCommandBuilder.runCommand(cmd.substring(1) + subCommandCMD);
                } else {
                    subCommandBuilder.suggestCommand(cmd.substring(1) + subCommandCMD + " ");
                }

                if (subCommand.getDescription() != null) {
                    subCommandBuilder.append(" §7- ").append("§7" + subCommand.getDescription());
                }

                builder.newLineAppend(subCommandBuilder);
            }
        }

        return builder.build();
    }

    public Collection<AbstractCommand> getChildren() {
        return this.childrenMap.values();
    }

    public void onTabComplete(TabCompleteEvent event, String buffer) {
        if (buffer == null) {
            return;
        }

        String[] args = buffer.split(" ", 2);
        String command = args[0], leftOver = null;

        if (args.length > 1) {
            leftOver = args[1];
        }

        if (buffer.endsWith(" ") && args.length == 1) {
            command += " ";
        }

        if (command.equalsIgnoreCase(this.getCmd())) {
            for (AbstractCommand child : this.getChildren()) {
                child.onTabComplete(event, leftOver);
            }
        }
    }
}
