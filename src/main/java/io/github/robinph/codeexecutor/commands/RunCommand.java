package io.github.robinph.codeexecutor.commands;

import io.github.robinph.codeexecutor.Common;
import io.github.robinph.codeexecutor.codeeditor.CodeEditor;
import io.github.robinph.codeexecutor.core.argument.argument.StringArgument;
import io.github.robinph.codeexecutor.core.command.AbstractCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RunCommand extends AbstractCommand {
    public RunCommand() {
        super("run");

        this.addArgument(new StringArgument("stdin"));

        this.getArguments().setLastArgArbitraryLength(true);

        this.setDescription("Run the code in your editor");
    }

    @Override
    public void execute(CommandSender sender, String ...args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            CodeEditor editor = Common.getCodeEditorManager().getEditor(player);

            if (this.getArguments().validate(sender, args)) {
                if (editor != null) {
                    if (editor.canRun(true)) {
                        if (editor.isRequiresStdin()) {
                            Common.getPistonQueue().add(editor, String.join(" ", args));
                        } else {
                            Common.getPistonQueue().add(editor, null);
                        }
                    } else {
                        editor.render();
                    }
                }
            }
        }
    }
}
