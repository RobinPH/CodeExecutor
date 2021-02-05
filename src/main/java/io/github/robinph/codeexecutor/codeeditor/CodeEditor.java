package io.github.robinph.codeexecutor.codeeditor;

import io.github.robinph.codeexecutor.Common;
import io.github.robinph.codeexecutor.config.ConfigManager;
import io.github.robinph.codeexecutor.core.chat.ChatBuilder;
import io.github.robinph.codeexecutor.piston.Language;
import io.github.robinph.codeexecutor.piston.PistonAPI;
import io.github.robinph.codeexecutor.utils.FontMetrics;
import io.github.robinph.codeexecutor.utils.FontUtils;
import io.github.robinph.codeexecutor.utils.Prefix;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import java.util.*;

public class CodeEditor implements Cloneable {

    public static final int height = Common.getConfig().get(ConfigManager.Variable.EDITOR_HEIGHT); // Lines of chat
    public static final int maxLineLength = Common.getConfig().get(ConfigManager.Variable.MAX_LINE_LENGTH);
    public static final int maxLineCount = Common.getConfig().get(ConfigManager.Variable.MAX_LINE_COUNT);
    public static final int maxCharacterPerLine = Common.getConfig().get(ConfigManager.Variable.MAX_CHARACTER_PER_LINE); // Characters
    public static final int maxOutputLine = Common.getConfig().get(ConfigManager.Variable.MAX_OUTPUT_LINE);
    public static final int maxSizePerOutput = Common.getConfig().get(ConfigManager.Variable.MAX_SIZE_PER_OUTPUT);

    private @Getter final Player player;

    private @Getter @Setter int currentLine = 1;
    private @Getter @Setter int highlightedLine = 0;

    private @Getter Language language;
    private @Getter @Setter Map<Integer, String> lines = new HashMap<>();
    private @Getter @Setter List<String> footerMessage = new ArrayList<>();

    private @Getter @Setter String[] argv = new String[0];
    private @Getter boolean requiresStdin = false;


    public CodeEditor(Player player) {
        this.player = player;
    }

    public void render() {
        if (CodeEditor.height < 3) {
            player.sendMessage("Could not render your text editor. Text editor height must be greater than 2.");
            return;
        }

        List<TextComponent> texts = new ArrayList<>();

        // Spams white spaces to cover all 20 layers of chat
        for (int i = 0; i < 20 - CodeEditor.height; i++) {
            texts.add(new TextComponent());
        }

        texts.add(this.header());
        texts.addAll(this.body());
        texts.add(this.footer());

        ChatBuilder builder = new ChatBuilder();
        // Merging all components to single component to avoid chat flickering
        for (TextComponent text : texts) {
            builder.newLineAppend(text);
        }

        player.spigot().sendMessage(builder.build());

        this.highlightedLine = 0;
    }

    public TextComponent header() {
        ChatBuilder builder = new ChatBuilder("Language: ");

        ChatBuilder argvBuilder = new ChatBuilder();
        if (this.getArgv().length == 0) {
            argvBuilder.append("No required argv.");
        }

        for (int i = 0; i < this.getArgv().length; i++) {
            argvBuilder.append("§7Argv #" + i + ": §f" + this.getArgument(i));

            if (i < this.getArgv().length - 1) {
                argvBuilder.append("\n");
            }
        }

        List<String> argvSuggestion = new ArrayList<>();
        for (String arg : this.getArgv()) {
            if (arg != null) {
                argvSuggestion.add("\"" + arg + "\"");
            } else {
                argvSuggestion.add("\"\"");
            }
        }

        builder.append(new ChatBuilder("§7[" + (this.getLanguage() != null ? this.getLanguage().getPrefix() : "Not Set") + "§7]§r")
                                        .addHoverText("Click to change.")
                                        .runCommand("code languages"))
                .append(" ")
                .append(new ChatBuilder(this.requiresStdin ? "§f[Stdin*]" : "§7[Stdin]")
                        .addHoverText(this.requiresStdin ? "Click to not require stdin" : "Click to require stdin")
                        .runCommand("code stdin " + (!this.requiresStdin)));

        if (this.getArgv().length == 0) {
            builder.append(" ").append(new ChatBuilder((this.isArgsComplete() ? "§a" : "§c") + "[Argv (" + this.getArgv().length +  ")]")
                            .addHoverText(argvBuilder.build().toPlainText())
                            .suggestCommand("code argv count "));
        } else {
            builder.append(" ").append(new ChatBuilder((this.isArgsComplete() ? "§a" : "§c") + "[Argv (" + this.getArgv().length +  ")]")
                            .addHoverText(argvBuilder.build().toPlainText())
                            .suggestCommand("code argv set " + String.join(" ", argvSuggestion)));
        }

        if (this.isRequiresStdin()) {
            builder.append(" ").append(new ChatBuilder((this.canRun(false) ? "§a" : "§c") + "§l[RUN]§r")
                    .addHoverText("§cStdin is required: §7/code run ...<input>")
                    .suggestCommand("code run "));
        } else {
            builder.append(" ").append(new ChatBuilder((this.canRun(false) ? "§a" : "§c") + "§l[RUN]§r")
                    .addHoverText("Click to run")
                    .runCommand("code run"));
        }


        return builder.build();
    }

    public List<TextComponent> body() {
        int bodyHeight = CodeEditor.height - 2;
        List<TextComponent> texts = new ArrayList<>();
        List<List<String>> lines = this.getLines(this.currentLine, bodyHeight, CodeEditor.maxLineLength);
        int linesCount = 0;

        for (List<String> line : lines) {
            linesCount += line.size();
        }

        String maxLineNumber = " " + (this.currentLine - 1 + lines.size() + (bodyHeight - linesCount));
        int maxLineNumberWidth = FontMetrics.getLength(maxLineNumber);

        for (int i = 0; i < lines.size() && texts.size() <= bodyHeight; i++) {
            int lineNumber = this.currentLine + i;
            texts.addAll(this.line(lineNumber > CodeEditor.maxLineCount ? -1 : lineNumber, maxLineNumberWidth, lines.get(i)));
        }

        // Filling the empty spaces with empty lines
        for (int i = 0; i < bodyHeight - linesCount && texts.size() <= bodyHeight; i++) {
            int lineNumber = this.currentLine + lines.size() + i;
            texts.addAll(this.line(lineNumber > CodeEditor.maxLineCount ? -1 : lineNumber, maxLineNumberWidth, new ArrayList<>()));
        }

        return texts.subList(0, Math.min(texts.size(), bodyHeight));
    }

    public TextComponent footer() {
        ChatBuilder builder = new ChatBuilder();

        builder.append(new ChatBuilder("[ ▲ ]").addHoverText("Scroll Up").runCommand("code scroll -1"))
                .append(" ")
                .append(new ChatBuilder("[ ▼ ]").addHoverText("Scroll Down").runCommand("code scroll 1"));

        if (this.getFooterMessage() != null) {
            String trimmed = FontUtils.trimString(String.join(" ", this.getFooterMessage()), FontMetrics.getLength('A') * 32);

            builder.append(" ").append(new ChatBuilder(trimmed).addHoverText(String.join("\n", this.getFooterMessage())));

            this.getFooterMessage().clear();
        }

        return builder.build();
    }

    public List<List<String>> getLines(int start, int maxLines, int maxLength) {
        List<List<String>> lines = new ArrayList<>();

        start = Math.max(1, start);

        for (int i = start; i < start + maxLines; i++) {
            List<String> line;

            if (this.getLines().containsKey(i)) {
                line = this.splitLine(this.getLines().get(i), maxLength);
            } else {
                line = new ArrayList<>();
            }
            lines.add(line);
        }

        return lines;
    }

    public List<String> splitLine(String text, int maxLength) {
        List<String> split = new ArrayList<>();

        if (text.length() == 0) {
            return split;
        }

        int lastSpacePos = -1;
        int lastBreak = 0;
        int length = 0;
        char[] arr = text.toCharArray();

        for (int i = 0; i < arr.length; i++) {
            char c = arr[i];

            if (length + FontMetrics.getLength(c + "") > maxLength) {
                if (lastSpacePos + 1 == lastBreak) {
                    lastSpacePos = i;
                }

                String fragment = text.substring(lastBreak, lastSpacePos + 1);

                length -= FontMetrics.getLength(fragment);
                split.add(fragment);
                lastBreak = lastSpacePos + 1;
            }

            length += FontMetrics.getLength(c + "");

            if (c == ' ') {
                lastSpacePos = i;
            }
        }

        String lastFragment = text.substring(lastBreak);

        if (lastFragment.length() > 0) {
            split.add(lastFragment);
        }

        return split;
    }

    public List<TextComponent> line(int lineNumber, int maxLineNumberWidth, List<String> line) {
        List<TextComponent> texts = new ArrayList<>();

        boolean highlighted = lineNumber == this.highlightedLine;

        ChatBuilder builder = new ChatBuilder();
        String padding = FontMetrics.makePadding(maxLineNumberWidth - FontMetrics.getLength(Integer.toString(lineNumber)));
        builder.append(this.paddingFormat(FontUtils.colorString('0', padding)))
                .append(this.lineNumberFormat(Integer.toString(lineNumber), highlighted));

        if (line.size() > 0) {
            builder.append(" ")
                    .append(this.lineTextFormat(line.get(0), highlighted));

            String buttonsPadding = FontMetrics.makePadding(CodeEditor.maxLineLength + maxLineNumberWidth + 20 - FontMetrics.getLength("§l" + builder.getText().toPlainText()));
            builder.append(FontUtils.colorString('0', buttonsPadding))
                    .append(new ChatBuilder("§a[+] ")
                            .addHoverText("Insert line")
                            .runCommand("code line insert " + lineNumber + " 1")
                    ).append(new ChatBuilder("§c[-] ")
                            .addHoverText("Delete line " + lineNumber)
                            .runCommand("code line delete " + lineNumber + " 1")
                    ).append(new ChatBuilder("§b[▲] ")
                            .addHoverText("Move line " + lineNumber + " up")
                            .runCommand("code line move " + lineNumber + " " + (lineNumber - 1))
                    ).append(new ChatBuilder("§b[▼]")
                            .addHoverText("Move line " + lineNumber + " down")
                            .runCommand("code line move " + lineNumber + " " + (lineNumber + 1))
                    );
        }

        if (line.size() == 0) {
            builder.append(this.emptyLine(CodeEditor.maxLineLength));
        }

        if (lineNumber > 0 && lineNumber <= CodeEditor.maxLineCount) {
            builder.addHoverText("Edit line " + lineNumber);

            String text = (lines.size() != 0 ? String.join("", line).replace("  ", "\\t") : "");
            builder.suggestCommand("code line edit " + lineNumber + " " + text);
        }

        texts.add(builder.build());

        for (int i = 1; i < line.size(); i++) {
            builder = new ChatBuilder();
            String extraLinePadding = FontMetrics.makePadding(maxLineNumberWidth);
            builder.append(this.paddingFormat(FontUtils.colorString('0', extraLinePadding)));

            builder.append(" ")
                    .append(this.lineTextFormat(line.get(i), highlighted));

            if (lineNumber > 0 && lineNumber <= CodeEditor.maxLineCount) {
                builder.addHoverText("Edit line " + lineNumber);

                String text = (lines.size() != 0 ? String.join("", line).replace("  ", "\\t") : "");
                builder.suggestCommand("code line edit " + lineNumber + " " + text);
            }

            texts.add(builder.build());
        }


        return texts;
    }

    public void setLanguage(String language) {
        Language lang = PistonAPI.getLanguage(language);

        if (lang != null) {
            this.language = lang;
            this.addFooterMessage(Prefix.SUCCESS_COLOR + "Language set to: " + this.getLanguage().getPrefix());

        } else {
            this.addFooterMessage(Prefix.ERROR_COLOR + "Invalid language: " + language + ". Full list: /code languages");
        }

    }

    public void editLine(int lineNumber, String text) {
        if (lineNumber > CodeEditor.maxLineCount || lineNumber < 1) {
           this.addFooterMessage(Prefix.ERROR_COLOR + "Maximum line number is: " + CodeEditor.maxLineCount);
            return;
        }

        if (text.length() == 0) {
            lines.remove(lineNumber);
        } else {
            String t = text;
            List<String> lines = new ArrayList<>();

            while (true) {
                String[] split = t.split("\\\\n", 2);
                lines.add(split[0]);

                if (split.length <= 1) {
                    break;
                }

                if (split.length == 2) {
                    t = split[1];
                }
            }

            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                if (line.length() > CodeEditor.maxCharacterPerLine) {
                    this.addFooterMessage(Prefix.WARNING_COLOR + "Maximum line length is: " + CodeEditor.maxCharacterPerLine + " characters. Input has been trimmed down.");
                }

                this.highlightedLine = lineNumber + i;

                this.lines.put(lineNumber + i, line.replace("\\t", "  ").substring(0, Math.min(line.length(), CodeEditor.maxCharacterPerLine)));
            }

        }
    }

    public void scroll(int distance) {
        this.currentLine += distance;

        if (this.currentLine < 1 || this.currentLine > CodeEditor.maxLineCount) {
            this.addFooterMessage(Prefix.ERROR_COLOR + "Could not scroll to line " + this.currentLine + ". Allowed lines are between 1 to " + CodeEditor.maxLineCount);
        }

        this.currentLine = Math.max(1, this.currentLine);
        this.currentLine = Math.min(this.currentLine, CodeEditor.maxLineCount);

        this.addFooterMessage(Prefix.SUCCESS_COLOR + "Scrolled to line " + this.currentLine);
    }

    public void goTo(int lineNumber) {
        if (lineNumber < 1 || lineNumber > CodeEditor.maxLineCount) {
            this.addFooterMessage(Prefix.ERROR_COLOR + String.format("Line %s not found. Available lines: 1-%s.", lineNumber, CodeEditor.maxLineCount));
            return;
        }

        this.scroll(lineNumber - this.currentLine);
    }

    public void insertLine(int lineNumber, int count) {
        if (lineNumber + count > CodeEditor.maxLineCount || lineNumber < 1) {
            this.addFooterMessage(Prefix.ERROR_COLOR + "Line must be between 1 to " + CodeEditor.maxLineCount);
            return;
        }

        if (count < 1) {
            this.addFooterMessage(Prefix.ERROR_COLOR + "Number of lines to delete must be greater than 0.");
            return;
        }


        List<Integer> l = new ArrayList<>(this.lines.keySet());

        l.sort(Collections.reverseOrder());

        l.stream().filter(n -> n >= lineNumber).forEach(n -> lines.put(n + count, lines.remove(n)));

        for (int i = 0; i < count; i++) {
            lines.put(lineNumber + i, "");
        }

        if (count > 1) {
            this.addFooterMessage(Prefix.SUCCESS_COLOR + String.format("Inserted %s new lines (%s-%s).", count, lineNumber, lineNumber + count - 1));
        } else {
            this.addFooterMessage(Prefix.SUCCESS_COLOR + String.format("Inserted 1 new line (%s).", lineNumber));
        }
    }

    public void deleteLine(int lineNumber, int count) {
        if (lineNumber > CodeEditor.maxLineCount || lineNumber < 1) {
            this.addFooterMessage(Prefix.ERROR_COLOR + "Line must be between 1 to " + CodeEditor.maxLineCount);
            return;
        }

        if (count < 1) {
            this.addFooterMessage(Prefix.ERROR_COLOR + "Number of lines to delete must be greater than 0.");
            return;
        }

        int linesToDelete = Math.min(count, CodeEditor.maxLineCount - lineNumber - 1);

        for (int i = 0; i < linesToDelete; i++) {
            lines.remove(lineNumber + i);
        }

        this.addFooterMessage(Prefix.SUCCESS_COLOR + "Deleted lines " + lineNumber + " to " + (lineNumber + count - 1));
    }

    public void moveLine(int lineNumber, int to) {
        if (to > CodeEditor.maxLineCount || lineNumber < 1 || to < 1) {
            this.addFooterMessage(Prefix.ERROR_COLOR + "Line " + lineNumber + " can not be moved to line " + to + ". Line must be between 1 to " + CodeEditor.maxLineCount);
            return;
        }

        List<Integer> l = new ArrayList<>(this.lines.keySet());

        if (!lines.containsKey(lineNumber)) return;

        if (lineNumber == to) {
            return;
        }

        String moving = lines.remove(lineNumber);
        if (to - lineNumber >= 1) {
            Collections.sort(l);
            l.stream().filter(n -> n > lineNumber && n <= to).forEach(n -> lines.put(n - 1, lines.remove(n)));

            lines.put(to, moving);
        } else {
            int d = Math.max(to - lineNumber, Math.min(0, 1 - lineNumber));

            l.sort(Collections.reverseOrder());
            l.stream().filter(n -> n < lineNumber && n >= lineNumber + d).forEach(n -> lines.put(n + 1, lines.remove(n)));

            lines.put(lineNumber + d, moving);
        }

        this.addFooterMessage(Prefix.SUCCESS_COLOR + "Line " + lineNumber + " has been moved to line " + to);
    }

    public void setArgvCount(int count) {
        if (count < 0) {
            this.addFooterMessage(Prefix.ERROR_COLOR + "Argv count should be greater than or equal to 0");
            return;
        }


        String[] newArguments = new String[count];

        for (int i = 0; i < this.getArgv().length && i < count; i++) {
            newArguments[i] = this.getArgument(i);
        }

        for (int i = this.getArgv().length; i < count; i++) {
            newArguments[i] = "";
        }

        this.setArgv(newArguments);
        this.addFooterMessage(Prefix.SUCCESS_COLOR + "Argv length changed to: " + count);
    }

    public void setArgument(int pos, String arg) {
        if (pos > this.getArgv().length - 1) {
            this.addFooterMessage(Prefix.ERROR_COLOR + "Maximum number of arguments is " + this.getArgv().length + ". To increase: /code argument count <count>");
            return;
        }

        this.getArgv()[pos] = arg;
        this.addFooterMessage(Prefix.SUCCESS_COLOR + "Argv #" + pos + " has been set to: " + arg);
    }

    public String getArgument(int pos) {
        if (pos > this.getArgv().length - 1) {
            return null;
        }

        return this.getArgv()[pos];
    }

    public boolean isArgsComplete() {
        for (String arg : this.getArgv()) {
            if (arg.isEmpty()) {
                return false;
            }
        }

        return true;
    }

    public void addFooterMessage(String text) {
        this.getFooterMessage().add(text);
    }

    public void requireStdin(boolean b) {
        this.requiresStdin = b;

        if (this.requiresStdin) {
            this.addFooterMessage(Prefix.SUCCESS_COLOR + "Stdin is now required.");
        } else {
            this.addFooterMessage(Prefix.SUCCESS_COLOR + "Stdin is no longer required.");
        }
    }

    public String getText() {
        int max = 0;
        for (int n : lines.keySet()) {
            max = Math.max(max, n);
        }

        List<String> lines = new ArrayList<>();

        for (int i = 1; i <= max; i++) {
            lines.add(this.lines.getOrDefault(i, ""));
        }

        return String.join("\n", lines);
    }

    public boolean canRun(boolean sendMessage) {
        if (this.getLanguage() == null) {
            if (sendMessage) this.addFooterMessage(Prefix.ERROR_COLOR + "Could not run. Language is not set.");
            return false;
        }

        if (this.lines.size() < 1) {
            if (sendMessage) this.addFooterMessage(Prefix.ERROR_COLOR + "Code is empty.");
            return false;
        }

        return true;
    }

    public String emptyLine(int length) {
        StringBuilder empty = new StringBuilder();
        int spaceLength = FontMetrics.getLength(' ');

        while (length >= spaceLength) {
            length -= spaceLength;
            empty.append(" ");
        }

        return empty.toString();
    }

    public String paddingFormat(String padding) {
        return "§0" + padding + "§r";
    }

    public String lineNumberFormat(String lineNumber, boolean highlighted) {
        if (highlighted) {
            return "§e" + lineNumber;
        } else {
            return "§7" + lineNumber;
        }
    }

    public String lineTextFormat(String line, boolean highlighted) {
        if (highlighted) {
            return "§e" + line;
        } else {
            return "§f" + line;
        }
    }

    @Override
    public CodeEditor clone() {
        try {
            CodeEditor clone = (CodeEditor) super.clone();

            clone.setLines(new HashMap<>(this.lines));
            clone.setFooterMessage(new ArrayList<>(this.footerMessage));
            clone.setArgv(this.argv.clone());

            return clone;
        } catch (CloneNotSupportedException ignored) { }

        return null;
    }
}
