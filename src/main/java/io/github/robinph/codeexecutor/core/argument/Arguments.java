package io.github.robinph.codeexecutor.core.argument;

import io.github.robinph.codeexecutor.core.chat.ChatBuilder;
import io.github.robinph.codeexecutor.core.command.AbstractCommand;
import io.github.robinph.codeexecutor.utils.Prefix;
import lombok.Getter;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class Arguments {
    private final List<Argument> args = new ArrayList<>();
    private @Getter boolean lastArgArbitraryLength = false;
    private @Getter final AbstractCommand command;
    public Arguments(AbstractCommand command) {
        this.command = command;
    }

    public void add(Argument argument) {
        args.add(argument);
    }

    public void setLastArgArbitraryLength(boolean b) {
        if (!args.isEmpty()) {
            args.get(args.size() - 1).setNullable(b);
        }

        this.lastArgArbitraryLength = b;
    }

    public boolean validate(CommandSender sender, String ...arguments) {
        TextComponent usage = new ChatBuilder(Prefix.NORMAL + "Usage: §7").append(this.command.usage()).append(this.argumentsUsage()).build();

        if (this.isLastArgArbitraryLength()) {
            if (this.args.size() - 1 > arguments.length) {
                sender.sendMessage(Prefix.ERROR + String.format("Required length: More than %s. Received: %s", args.size() - 2, arguments.length));
                sender.spigot().sendMessage(usage);
                return false;
            }
        } else {
            if (this.args.size() != arguments.length) {
                sender.sendMessage(Prefix.ERROR + String.format("Required length: %s. Received: %s", args.size(), arguments.length));
                sender.spigot().sendMessage(usage);
                return false;
            }
        }

        boolean success = true;

        for (int i = 0; i < Math.max(args.size(), arguments.length); i++) {
            int n = Math.min(i, args.size() - 1);
            String arg = i > arguments.length - 1 ? null : arguments[i];

            if (!args.get(n).validate(arg)) {
                sender.sendMessage(Prefix.ERROR_COLOR + args.get(n).error(arg));
                sender.spigot().sendMessage(usage);
                success = false;
            }
        }

        return success;
    }

    public String argumentsUsage() {
        List<String> usages = new ArrayList<>();

        for (int i = 0; i < this.args.size(); i++) {
            if (i == this.args.size() - 1 && this.lastArgArbitraryLength) {
                usages.add("..." + this.args.get(i).usage());
            } else {
                usages.add(this.args.get(i).usage());
            }
        }

        return String.join(" ", usages);
    }
}
