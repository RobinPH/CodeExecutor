package io.github.robinph.codeexecutor.commands;

import io.github.robinph.codeexecutor.Common;
import io.github.robinph.codeexecutor.codeeditor.CodeEditor;
import io.github.robinph.codeexecutor.core.argument.argument.StringArgument;
import io.github.robinph.codeexecutor.core.command.AbstractCommand;
import io.github.robinph.codeexecutor.pastebin.PastebinAPI;
import io.github.robinph.codeexecutor.utils.Prefix;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ImportCommand extends AbstractCommand {
    public ImportCommand() {
        super("import");

        this.addArgument(new StringArgument("pastebinURL"));
    }

    @Override
    public void execute(CommandSender sender, String ...args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (this.getArguments().validate(sender, args)) {
                String url = StringArgument.value(args[0]);

                if (!PastebinAPI.validateURL(url)) {
                    player.sendMessage(Prefix.ERROR + "Invalid pastebin link.");
                    player.sendMessage(Prefix.NORMAL + "Formats:");
                    player.sendMessage(Prefix.NORMAL + "  §fhttps://pastebin.com/xSncfH2H§7");
                    player.sendMessage(Prefix.NORMAL + "  §fpastebin.com/xSncfH2H");
                    player.sendMessage(Prefix.NORMAL + "  §fxSncfH2H");
                    return;
                }

                String lines = PastebinAPI.fetchPaste(url);
                if (lines == null) {
                    player.sendMessage(Prefix.ERROR + "Paste does not exist.");
                    return;
                }

                CodeEditor editor = Common.getCodeEditorManager().newEditor(player);

                if (editor != null) {
                    new BukkitRunnable() {

                        @Override
                        public void run() {
                            Pattern pattern = Pattern.compile("^((?:https://)?pastebin.com/)?([a-zA-Z0-9_]{8})(?:/)?");
                            Matcher matcher = pattern.matcher(url);

                            editor.editLine(1, lines.replace(System.lineSeparator(), "\\n").replace("\11", "\\t"));

                            if (matcher.find()) {
                                String pasteID = matcher.group(2);
                                editor.changeName(pasteID);
                            }

                            editor.setHighlightedLine(0);
                            editor.getFooterMessage().clear();

                            editor.render();
                        }
                    }.runTaskAsynchronously(Common.getPlugin());
                }
            }
        }
    }
}
