package io.github.robinph.codeexecutor.codeeditor;

import io.github.robinph.codeexecutor.utils.Prefix;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class CodeEditorManager {
    private static final CodeEditorManager instance = new CodeEditorManager();

    private final Map<Player, CodeEditor> editors = new HashMap<>();

    public CodeEditor newEditor(Player player) {
        if (editors.containsKey(player)) {
            player.sendMessage(Prefix.WARNING + "You have an open editor. To open your editor: /code open");
            return null;
        }

        CodeEditor editor = new CodeEditor(player);

        editors.put(player, editor);

        return editor;
    }

    public CodeEditor getEditor(Player player) {
        if (!editors.containsKey(player)) {
            player.sendMessage(Prefix.WARNING + "You do not have an open editor. To open new one: /code new");
            return null;
        }

        return editors.get(player);
    }

    public CodeEditor getEditor(Player player, boolean sendMessage) {
        if (!editors.containsKey(player) && sendMessage) {
            player.sendMessage(Prefix.WARNING + "You do not have an open editor. To open new one: /code new");
            return null;
        }

        return editors.get(player);
    }

    public boolean openEditor(CodeEditor editor) {
        if (editors.containsKey(editor.getPlayer())) {
            this.closeEditor(editor.getPlayer());
        }

        editors.put(editor.getPlayer(), editor);

        return true;
    }

    public boolean closeEditor(Player player) {
        if (!editors.containsKey(player)) {
            return false;
        }

        editors.remove(player);

        return true;
    }

    public static CodeEditorManager getInstance() { return instance; }
}
