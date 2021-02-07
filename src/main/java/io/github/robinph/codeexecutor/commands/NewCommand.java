package io.github.robinph.codeexecutor.commands;

import io.github.robinph.codeexecutor.Common;
import io.github.robinph.codeexecutor.core.argument.argument.StringArgument;
import io.github.robinph.codeexecutor.core.command.AbstractCommand;
import io.github.robinph.codeexecutor.codeeditor.CodeEditor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class NewCommand extends AbstractCommand {
    public NewCommand() {
        super("new");

        this.addArgument(new StringArgument("name"));
        this.getArguments().setLastArgArbitraryLength(true);

        this.setDescription("Create a new editor");
    }

    @Override
    public void execute(CommandSender sender, String ...args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            CodeEditor editor = Common.getCodeEditorManager().newEditor(player);

            if (this.getArguments().validate(sender, args)) {
                if (editor != null) {
                    if (args.length > 0) {
                        editor.changeName(String.join(" ", args));
                        editor.getFooterMessage().clear();
                    }

                    editor.render();
                }
            }
        }
    }
}
