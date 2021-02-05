package io.github.robinph.codeexecutor.commands.argv;

import io.github.robinph.codeexecutor.Common;
import io.github.robinph.codeexecutor.core.argument.argument.StringArgument;
import io.github.robinph.codeexecutor.core.command.AbstractCommand;
import io.github.robinph.codeexecutor.codeeditor.CodeEditor;
import io.github.robinph.codeexecutor.utils.ArgumentUtils;
import io.github.robinph.codeexecutor.utils.Prefix;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class SetCommand extends AbstractCommand {
    public SetCommand() {
        super("set");

        this.addArgument(new StringArgument("argv"));

        this.getArguments().setLastArgArbitraryLength(true);
    }

    @Override
    public void execute(CommandSender sender, String ...args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (this.getArguments().validate(sender, args)) {
                CodeEditor editor = Common.getCodeEditorManager().getEditor(player);

                if (editor != null) {
                    List<String> arguments = ArgumentUtils.parse('"', String.join(" ", args));

                    if (arguments.size() <= 0 || arguments.size() > editor.getArgv().length) {
                        editor.addFooterMessage(Prefix.ERROR_COLOR + "Required length is " + editor.getArgv().length + ". To increase the length: /code argument count <count>");
                        editor.render();
                        return;
                    }

                    for (int i = 0; i < arguments.size(); i++) {
                        editor.setArgument(i, arguments.get(i));
                    }

                    editor.render();
                }
            }
        }
    }
}
