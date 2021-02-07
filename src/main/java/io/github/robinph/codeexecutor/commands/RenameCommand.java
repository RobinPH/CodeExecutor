package io.github.robinph.codeexecutor.commands;

import io.github.robinph.codeexecutor.Common;
import io.github.robinph.codeexecutor.codeeditor.CodeEditor;
import io.github.robinph.codeexecutor.core.argument.argument.StringArgument;
import io.github.robinph.codeexecutor.core.command.AbstractCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RenameCommand extends AbstractCommand {
    public RenameCommand() {
        super("rename");

        this.getArguments().setLastArgArbitraryLength(true);
        this.addArgument(new StringArgument("name").setNullable(false));

        this.setDescription("Rename your editor");
    }

    @Override
    public void execute(CommandSender sender, String ...args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            CodeEditor editor = Common.getCodeEditorManager().getEditor(player);

            if (this.getArguments().validate(sender, args)) {
                if (editor != null) {
                    editor.changeName(String.join(" ", args));
                    editor.render();
                }
            }
        }
    }
}
