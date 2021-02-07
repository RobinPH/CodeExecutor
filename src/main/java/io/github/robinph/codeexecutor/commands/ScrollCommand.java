package io.github.robinph.codeexecutor.commands;

import io.github.robinph.codeexecutor.Common;
import io.github.robinph.codeexecutor.core.argument.argument.IntegerArgument;
import io.github.robinph.codeexecutor.core.command.AbstractCommand;
import io.github.robinph.codeexecutor.codeeditor.CodeEditor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ScrollCommand extends AbstractCommand {
    public ScrollCommand() {
        super("scroll");

        this.addArgument(new IntegerArgument("distance").setNullable(true));

        this.setDescription("Scroll up/down the editor");
    }

    @Override
    public void execute(CommandSender sender, String ...args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (this.getArguments().validate(sender, args)) {
                CodeEditor editor = Common.getCodeEditorManager().getEditor(player);

                if (editor != null) {
                    if (args.length > 0) {
                        editor.scroll(IntegerArgument.value(args[0]));
                    } else {
                        editor.scroll(1);
                    }
                    editor.render();
                }
            }
        }
    }
}
