package io.github.robinph.codeexecutor.commands;

import io.github.robinph.codeexecutor.Common;
import io.github.robinph.codeexecutor.codeeditor.CodeEditor;
import io.github.robinph.codeexecutor.core.argument.argument.StringArgument;
import io.github.robinph.codeexecutor.core.command.AbstractCommand;
import io.github.robinph.codeexecutor.database.Response;
import io.github.robinph.codeexecutor.utils.Prefix;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DeleteCommand extends AbstractCommand {
    public DeleteCommand() {
        super("delete");

        this.getArguments().setLastArgArbitraryLength(true);
        this.addArgument(new StringArgument().setNullable(false));

    }

    @Override
    public void execute(CommandSender sender, String... args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (this.getArguments().validate(sender, args)) {
                String name = String.join(" ", args);
                Response<CodeEditor> checkIfExists = Common.getDatabase().getEditor(player, name);

                if (checkIfExists.isSuccess()) {
                    Response<Boolean> delete = Common.getDatabase().deleteEditor(checkIfExists.getContent());
                    if (delete.isSuccess()) {
                        player.sendMessage(Prefix.SUCCESS + name + " has been deleted");
                    } else {
                        player.sendMessage(Prefix.ERROR + "Something went wrong while deleting " + name);
                    }

                } else {
                    player.sendMessage(Prefix.ERROR + name + " does not exist.");
                }

            }
        }
    }
}
