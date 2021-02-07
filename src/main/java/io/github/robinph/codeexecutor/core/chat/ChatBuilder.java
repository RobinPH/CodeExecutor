package io.github.robinph.codeexecutor.core.chat;

import io.github.robinph.codeexecutor.utils.FontMetrics;
import io.github.robinph.codeexecutor.utils.FontUtils;
import lombok.Getter;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;

public class ChatBuilder {
    private @Getter
    final TextComponent text;

    public ChatBuilder() {
        this.text = new TextComponent();
    }

    public ChatBuilder(String text) {
        this.text = new TextComponent(text);
    }

    public ChatBuilder append(ChatBuilder builder) {
        this.text.addExtra(builder.getText());

        return this;
    }

    public ChatBuilder append(BaseComponent component) {
        this.text.addExtra(component);

        return this;
    }

    public ChatBuilder append(String text) {
        this.text.addExtra(new TextComponent(text));

        return this;
    }

    public ChatBuilder newLineAppend(ChatBuilder builder) {
        this.newLine().append(builder.getText());

        return this;
    }

    public ChatBuilder newLineAppend(BaseComponent component) {
        this.newLine().append(component);

        return this;
    }

    public ChatBuilder newLineAppend(String text) {
        this.newLine().append(new TextComponent(text));

        return this;
    }

    public ChatBuilder addHoverText(String text) {
        this.text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(text)));

        return this;
    }

    public ChatBuilder runCommand(String cmd) {
        this.text.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + cmd));

        return this;
    }

    public ChatBuilder suggestCommand(String cmd) {
        this.text.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/" + cmd));

        return this;
    }

    public ChatBuilder newLine() {
        this.text.addExtra("\n");
        return this;
    }

    public ChatBuilder merge(String ...args) {
        for (String arg : args) {
            this.append(arg);
        }

        return this;
    }

    public ChatBuilder mergeNewLine(String ...args) {
        for (int i = 0; i < args.length; i++) {
            this.append(args[i]);

            if (i < args.length - 1) {
                this.newLine();
            }
        }

        return this;
    }

    public ChatBuilder mergeNewLine(ChatBuilder ...args) {
        for (int i = 0; i < args.length; i++) {
            this.append(args[i]);

            if (i < args.length - 1) {
                this.newLine();
            }
        }

        return this;
    }

    public ChatBuilder resize(int length) {
        int thisLength = FontMetrics.getLength(this.getText().toPlainText());

        if (thisLength <= length) {
            this.append(FontUtils.colorString('0', FontMetrics.makePadding(length - thisLength)));
        }

        return this;
    }

    public TextComponent build() {
        return this.getText();
    }

    public TextComponent buildCenterAligned() {
        int paddingLength = FontMetrics.MAX_CHAT_LENGTH - FontMetrics.getLength(this.getText().toPlainText());

        // Center aligned
        return new ChatBuilder(FontUtils.colorString('0', FontMetrics.makePadding(paddingLength / 2))).append(this).build();
    }
}
