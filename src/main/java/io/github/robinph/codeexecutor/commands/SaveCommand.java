package io.github.robinph.codeexecutor.commands;

import io.github.robinph.codeexecutor.Common;
import io.github.robinph.codeexecutor.codeeditor.CodeEditor;
import io.github.robinph.codeexecutor.core.command.AbstractCommand;
import io.github.robinph.codeexecutor.database.Response;
import io.github.robinph.codeexecutor.utils.Prefix;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SaveCommand extends AbstractCommand {
    public SaveCommand() {
        super("save");
    }

    @Override
    public void execute(CommandSender sender, String ...args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            CodeEditor editor = Common.getCodeEditorManager().getEditor(player);

            if (editor != null) {
                Response<Boolean> response = Common.getDatabase().newEditor(editor);

                if (response.isSuccess()) {
                    editor.setSaved(response.getContent());

                    editor.addFooterMessage(Prefix.SUCCESS_COLOR + "Saved!");
                    editor.render();
                } else {
                    editor.setSaved(response.getContent());

                    editor.addFooterMessage(Prefix.ERROR_COLOR + "Failed to save: " + response.getMessage());
                    editor.render();
                }
            }
        }
    }
}
