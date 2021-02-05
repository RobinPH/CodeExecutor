package io.github.robinph.codeexecutor.commands;

import io.github.robinph.codeexecutor.Common;
import io.github.robinph.codeexecutor.core.argument.argument.StringArgument;
import io.github.robinph.codeexecutor.core.command.AbstractCommand;
import io.github.robinph.codeexecutor.piston.PistonAPI;
import io.github.robinph.codeexecutor.codeeditor.CodeEditor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class SetLanguageCommand extends AbstractCommand {
    public SetLanguageCommand() {
        super("language");

        this.addArgument(new StringArgument("language"));
    }

    @Override
    public void execute(CommandSender sender, String ...args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (this.getArguments().validate(sender, args)) {
                CodeEditor editor = Common.getCodeEditorManager().getEditor(player);

                if (editor != null) {
                    editor.setLanguage(args[0]);
                    editor.render();
                }
            }
        }
    }

    @Override
    public List<String> getTabComplete(Player player, String ...args) {
        return PistonAPI.getLanguageList();
    }
}
