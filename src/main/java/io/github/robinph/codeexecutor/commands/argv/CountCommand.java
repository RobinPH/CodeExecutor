package io.github.robinph.codeexecutor.commands.argv;

import io.github.robinph.codeexecutor.Common;
import io.github.robinph.codeexecutor.core.argument.argument.IntegerArgument;
import io.github.robinph.codeexecutor.core.command.AbstractCommand;
import io.github.robinph.codeexecutor.codeeditor.CodeEditor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CountCommand extends AbstractCommand {
    public CountCommand() {
        super("count");

        this.addArgument(new IntegerArgument("count"));

        this.setDescription("Set the number of argv");
    }

    @Override
    public void execute(CommandSender sender, String... args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (this.getArguments().validate(sender, args)) {
                CodeEditor editor = Common.getCodeEditorManager().getEditor(player);

                if (editor != null) {
                    editor.setArgvCount(IntegerArgument.value(args[0]));
                    editor.render();
                }
            }
        }
    }
}
