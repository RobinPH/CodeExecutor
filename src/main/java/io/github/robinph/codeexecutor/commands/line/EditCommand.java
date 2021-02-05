package io.github.robinph.codeexecutor.commands.line;

import io.github.robinph.codeexecutor.Common;
import io.github.robinph.codeexecutor.core.argument.argument.IntegerArgument;
import io.github.robinph.codeexecutor.core.argument.argument.StringArgument;
import io.github.robinph.codeexecutor.core.command.AbstractCommand;
import io.github.robinph.codeexecutor.codeeditor.CodeEditor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.server.TabCompleteEvent;

import java.util.Arrays;

public class EditCommand extends AbstractCommand {
    public EditCommand() {
        super("edit");

        this.addArgument(new IntegerArgument("line number"));
        this.addArgument(new StringArgument("text"));

        this.getArguments().setLastArgArbitraryLength(true);
    }

    @Override
    public void execute(CommandSender sender, String ...args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (this.getArguments().validate(sender, args)) {
                CodeEditor editor = Common.getCodeEditorManager().getEditor(player);

                if (editor != null) {
                    editor.editLine(IntegerArgument.value(args[0]), String.join(" ", Arrays.copyOfRange(args, 1, args.length)));
                    editor.setHighlightedLine(0);
                    editor.render();
                }
            }
        }
    }

    @Override
    public void onTabComplete(TabCompleteEvent event, String buffer) {
        if (!(event.getSender() instanceof Player)) {
            return;
        }

        if (buffer == null) {
            return;
        }

        String[] args = buffer.split(" ", 2);
        String command = args[0], leftOver = null;

        if (args.length > 1) {
            leftOver = args[1];
        }

        Player player = (Player) event.getSender();

        if (command.equalsIgnoreCase(this.getCmd())) {
            if (leftOver != null) {
                String[] input = leftOver.split(" ", 2);
                String lineNumber = input[0], text = "";

                if (input.length > 1) {
                    text = input[1];
                }

                try {
                    int line = Integer.parseInt(lineNumber);

                    CodeEditor editor = Common.getCodeEditorManager().getEditor(player, false);

                    if (editor != null) {
                        editor.editLine(line, text);
                        editor.render();
                    }

                } catch (NumberFormatException ignored) {}
            }
        }
    }
}
