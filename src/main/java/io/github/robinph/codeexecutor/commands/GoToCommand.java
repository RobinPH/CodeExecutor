package io.github.robinph.codeexecutor.commands;

import io.github.robinph.codeexecutor.Common;
import io.github.robinph.codeexecutor.core.argument.argument.IntegerArgument;
import io.github.robinph.codeexecutor.core.command.AbstractCommand;
import io.github.robinph.codeexecutor.codeeditor.CodeEditor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GoToCommand extends AbstractCommand {
    public GoToCommand() {
        super("goto");

        this.addArgument(new IntegerArgument("line number"));

        this.setDescription("Go to specific line");
    }

    @Override
    public void execute(CommandSender sender, String ...args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            CodeEditor editor = Common.getCodeEditorManager().getEditor(player);

            if (this.getArguments().validate(sender, args)) {
                if (editor != null) {
                    editor.goTo(IntegerArgument.value(args[0]));
                    editor.render();
                }
            }
        }
    }
}
