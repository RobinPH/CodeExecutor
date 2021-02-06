package io.github.robinph.codeexecutor.commands;

import io.github.robinph.codeexecutor.Common;
import io.github.robinph.codeexecutor.codeeditor.CodeEditor;
import io.github.robinph.codeexecutor.core.argument.argument.IntegerArgument;
import io.github.robinph.codeexecutor.core.chat.ChatBuilder;
import io.github.robinph.codeexecutor.core.command.AbstractCommand;
import io.github.robinph.codeexecutor.database.Response;
import io.github.robinph.codeexecutor.utils.FontMetrics;
import io.github.robinph.codeexecutor.utils.FontUtils;
import io.github.robinph.codeexecutor.utils.Prefix;
import lombok.Getter;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ListCommand extends AbstractCommand {
    private @Getter final int perPage = 18;

    public ListCommand() {
        super("list");

        this.addArgument(new IntegerArgument().setNullable(true));
    }

    @Override
    public void execute(CommandSender sender, String ...args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (!this.getArguments().validate(sender, args)) {
                return;
            }

            Response<List<CodeEditor>> response = Common.getDatabase().getEditors(player);

            if (response.isSuccess()) {
                ChatBuilder builder = new ChatBuilder();

                List<CodeEditor> editors = response.getContent();
                List<ChatBuilder> editorsText = new ArrayList<>();

                int page = Math.max(1, args.length >= 1 ? Integer.parseInt(args[0]) : 1) - 1;
                page = (int) Math.min(page, Math.ceil((double) editors.size() / perPage) - 1);

                int maxNumber = Math.min(page * perPage + perPage, editors.size());
                int maxPaddingLength = FontMetrics.getLength(" " + maxNumber);

                for (int i = 0; i < 20 - (page*perPage - maxNumber) - 2; i++) {
                    builder.newLine();
                }

                builder.append(new ChatBuilder("§3§lCodes of " + player.getName() + " §7(" + editors.size() + " Codes)").buildCenterAligned()).newLine();

                if (!editors.isEmpty()) {
                    for (int i = page * perPage; i < editors.size() && i < (page + 1) * perPage; i++) {
                        CodeEditor editor = editors.get(i);
                        String name = FontUtils.trimString(editor.getName(), FontMetrics.getLength('A') * 30);
                        String padding = FontMetrics.makePadding(maxPaddingLength - FontMetrics.getLength((i + 1) + ""));

                        ChatBuilder line = new ChatBuilder();

                        line.append(new ChatBuilder(" " + FontUtils.colorString('0', padding) + Prefix.NORMAL_COLOR + (i + 1) + " §b" + name)
                                .addHoverText("Click to open " + editor.getName())
                                .runCommand("code open " + editor.getName()));
                        line.resize(FontMetrics.getLength('A') * 47);
                        line.append(new ChatBuilder("§c§l[-]")
                                .addHoverText("§cDelete " + editor.getName())
                                .runCommand("code delete " + editor.getName()));

                        editorsText.add(line);
                    }
                }

                builder.mergeNewLine(editorsText.toArray(new ChatBuilder[0]));
                builder.newLineAppend(this.pageNumber(page + 1, editors.size()));

                player.spigot().sendMessage(builder.build());

            } else {
                player.sendMessage(Prefix.ERROR + "Failed to fetch all of your editors.");
            }
        }
    }

    public TextComponent pageNumber(int currentPage, int total) {
        ChatBuilder builder = new ChatBuilder();

        int first = currentPage == 0 ? 0 : Math.max(1, currentPage - 1);

        int fourth = (int) Math.ceil((double) total / this.getPerPage());
        int third = Math.max(currentPage, fourth - 1);

        String listCmd = "code list ";

        builder.append(new ChatBuilder("§3«§r").addHoverText("Previous Page").runCommand(listCmd + (currentPage - 1)).append(" "));
        builder.append((new ChatBuilder("§b" + first + "§r").addHoverText("Page " + first).runCommand(listCmd + first).append(" ")));
        builder.append((new ChatBuilder("§b§n" + currentPage + "§r").addHoverText("Page " + currentPage).runCommand(listCmd + currentPage).append(" ")));
        builder.append("...").append(" ");
        builder.append(new ChatBuilder("§b" + third + "§r").addHoverText("Page " + third).runCommand(listCmd + third).append(" "));
        builder.append(new ChatBuilder("§b" + fourth + "§r").addHoverText("Page " + fourth).runCommand(listCmd + fourth).append(" "));
        builder.append(new ChatBuilder("§3»§r").addHoverText("Next Page").runCommand(listCmd + (currentPage + 1)));

        return builder.buildCenterAligned();
    }
}
