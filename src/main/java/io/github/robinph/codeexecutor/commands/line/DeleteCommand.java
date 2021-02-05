package io.github.robinph.codeexecutor.commands.line;

import io.github.robinph.codeexecutor.Common;
import io.github.robinph.codeexecutor.core.argument.argument.IntegerArgument;
import io.github.robinph.codeexecutor.core.command.AbstractCommand;
import io.github.robinph.codeexecutor.codeeditor.CodeEditor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DeleteCommand extends AbstractCommand {
    public DeleteCommand() {
        super("delete");

        this.addArgument(new IntegerArgument("line number"));
        this.addArgument(new IntegerArgument("count"));

        // setLastArgArbitraryLength() must be called last
        this.getArguments().setLastArgArbitraryLength(true);
    }

    @Override
    public void execute(CommandSender sender, String ...args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (this.getArguments().validate(sender, args)) {
                CodeEditor editor = Common.getCodeEditorManager().getEditor(player);

                if (editor == null) {
                    return;
                }

                if (args.length == 1) {
                    editor.deleteLine(IntegerArgument.value(args[0]), 1);
                } else if (args.length > 1){
                    editor.deleteLine(IntegerArgument.value(args[0]), IntegerArgument.value(args[1]));
                }

                editor.render();
            }
        }
    }
}
