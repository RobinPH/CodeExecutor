package io.github.robinph.codeexecutor.commands;

import io.github.robinph.codeexecutor.Common;
import io.github.robinph.codeexecutor.codeeditor.CodeEditor;
import io.github.robinph.codeexecutor.core.argument.argument.BooleanArgument;
import io.github.robinph.codeexecutor.core.command.AbstractCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StdinCommand extends AbstractCommand {
    public StdinCommand() {
        super("stdin");

        this.addArgument(new BooleanArgument());

        this.setDescription("Enable/Disable stdin");
    }

    @Override
    public void execute(CommandSender sender, String ...args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (this.getArguments().validate(sender, args)) {
                CodeEditor editor = Common.getCodeEditorManager().getEditor(player);

                if (editor != null) {
                    editor.requireStdin(BooleanArgument.value(args[0]));

                    editor.render();
                }
            }
        }
    }

    @Override
    public List<String> getTabComplete(Player player, String ...args) {
        return new ArrayList<>(Arrays.asList("true", "false"));
    }
}
