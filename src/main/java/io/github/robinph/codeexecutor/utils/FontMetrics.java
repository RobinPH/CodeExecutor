package io.github.robinph.codeexecutor.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FontMetrics {
    private static @Getter final Map<Character, MinecraftCharacter> metrics = new HashMap<>();

    private @Getter final char character;
    private @Getter final int length;

    private FontMetrics(char character, int length) {
        this.character = character;
        this.length = length;
    }

    public static void register(char character, int length) {
        FontMetrics.getMetrics().put(character, new MinecraftCharacter(character, length));
    }

    public static int getLength(char character) {
        FontMetrics.getMetrics().computeIfAbsent(character, ch -> new MinecraftCharacter(character, 6));

        return FontMetrics.getMetrics().get(character).getLength();
    }

    public static int getLength(String text) {
        String BOLD_RESET_CODE = "0123456789AaBbCcDdEeFfRr";
        int length = 0;
        boolean isBold = false;

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == ChatColor.COLOR_CHAR) {
                if (i + 1 < text.length()) {
                    char code = text.toLowerCase().charAt(i + 1);

                    if (code == ChatColor.BOLD.toString().charAt(1)) {
                        isBold = true;
                    } else if (BOLD_RESET_CODE.indexOf(code) != -1) {
                        isBold = false;
                    }

                    if (ChatColor.ALL_CODES.indexOf(code) != -1) {
                        i++;
                    }
                }
            } else {
                length += FontMetrics.getLength(c) + (isBold ? 1 : 0);
            }
        }

        return length;
    }

    public static void registerDefault() {
        FontMetrics.register('§', 0); // Minecraft Color Symbol (§) is 0

        FontMetrics.register('A', 6);
        FontMetrics.register('a', 6);
        FontMetrics.register('B', 6);
        FontMetrics.register('b', 6);
        FontMetrics.register('C', 6);
        FontMetrics.register('c', 6);
        FontMetrics.register('D', 6);
        FontMetrics.register('d', 6);
        FontMetrics.register('E', 6);
        FontMetrics.register('e', 6);
        FontMetrics.register('F', 6);
        FontMetrics.register('f', 5);
        FontMetrics.register('G', 6);
        FontMetrics.register('g', 6);
        FontMetrics.register('H', 6);
        FontMetrics.register('h', 6);
        FontMetrics.register('I', 4);
        FontMetrics.register('i', 2);
        FontMetrics.register('J', 6);
        FontMetrics.register('j', 6);
        FontMetrics.register('K', 6);
        FontMetrics.register('k', 5);
        FontMetrics.register('L', 6);
        FontMetrics.register('l', 3);
        FontMetrics.register('M', 6);
        FontMetrics.register('m', 6);
        FontMetrics.register('N', 6);
        FontMetrics.register('n', 6);
        FontMetrics.register('O', 6);
        FontMetrics.register('o', 6);
        FontMetrics.register('P', 6);
        FontMetrics.register('p', 6);
        FontMetrics.register('Q', 6);
        FontMetrics.register('q', 6);
        FontMetrics.register('R', 6);
        FontMetrics.register('r', 6);
        FontMetrics.register('S', 6);
        FontMetrics.register('s', 6);
        FontMetrics.register('T', 6);
        FontMetrics.register('t', 4);
        FontMetrics.register('U', 6);
        FontMetrics.register('u', 6);
        FontMetrics.register('V', 6);
        FontMetrics.register('v', 6);
        FontMetrics.register('W', 6);
        FontMetrics.register('w', 6);
        FontMetrics.register('X', 6);
        FontMetrics.register('x', 6);
        FontMetrics.register('Y', 6);
        FontMetrics.register('y', 6);
        FontMetrics.register('Z', 6);
        FontMetrics.register('z', 6);
        FontMetrics.register('1', 6);
        FontMetrics.register('2', 6);
        FontMetrics.register('3', 6);
        FontMetrics.register('4', 6);
        FontMetrics.register('5', 6);
        FontMetrics.register('6', 6);
        FontMetrics.register('7', 6);
        FontMetrics.register('8', 6);
        FontMetrics.register('9', 6);
        FontMetrics.register('0', 6);
        FontMetrics.register('!', 2);
        FontMetrics.register('@', 7);
        FontMetrics.register('#', 6);
        FontMetrics.register('$', 6);
        FontMetrics.register('%', 6);
        FontMetrics.register('^', 6);
        FontMetrics.register('&', 6);
        FontMetrics.register('*', 4);
        FontMetrics.register('(', 4);
        FontMetrics.register(')', 4);
        FontMetrics.register('-', 6);
        FontMetrics.register('_', 6);
        FontMetrics.register('+', 6);
        FontMetrics.register('=', 6);
        FontMetrics.register('{', 4);
        FontMetrics.register('}', 4);
        FontMetrics.register('[', 4);
        FontMetrics.register(']', 4);
        FontMetrics.register(':', 2);
        FontMetrics.register(';', 2);
        FontMetrics.register('"', 4);
        FontMetrics.register('\'', 2);
        FontMetrics.register('<', 5);
        FontMetrics.register('>', 5);
        FontMetrics.register('?', 6);
        FontMetrics.register('/', 6);
        FontMetrics.register('\\', 6);
        FontMetrics.register('|', 2);
        FontMetrics.register('~', 7);
        FontMetrics.register('`', 3);
        FontMetrics.register('.', 2);
        FontMetrics.register(',', 2);
        FontMetrics.register(' ', 4);
    }

    public static void importFontData(JsonObject data) {
        FontMetrics.getMetrics().clear();

        for (Map.Entry<String, JsonElement> letterData : data.entrySet()) {
            try {
                char[] characters = letterData.getKey().toCharArray();

                if (characters.length != 1) {
                    throw new Exception("Error register the character `" + letterData.getKey() + "`. Skipping it.");
                }

                int length = letterData.getValue().getAsInt();

                FontMetrics.register(characters[0], length);

            } catch (Exception e) {
                Bukkit.getLogger().warning(e.getMessage());
            }
        }
    }

    public static String makePadding(int length) {
        String[] choices = { "§l §r", " ", "§l.§r", "." }; // (Default) Length = 5 4 3 2 , respectively
        int choicesMinimum = Integer.MAX_VALUE;

        for (String choice : choices) {
            if (FontMetrics.getLength(choice) == 0) {
                continue;
            }

            choicesMinimum = Math.min(choicesMinimum, FontMetrics.getLength(choice));
        }

        if (length < choicesMinimum) {
            return "";
        }

        List<String> toAppend = new ArrayList<>();

        for (String choice : choices) {
            if (FontMetrics.getLength(choice) > length) {
                continue;
            }

            toAppend.add(choice);
            toAppend.add(FontMetrics.makePadding(length - FontMetrics.getLength(choice)));

            String merged = String.join("", toAppend);
            if (FontMetrics.getLength(merged) == length) {
                return merged;
            }

            toAppend.remove(toAppend.size() - 1);
            toAppend.remove(toAppend.size() - 1);
        }

        return "";
    }

    public static void test(Player player) {
        int maxLength = 0;

        for (MinecraftCharacter character : FontMetrics.getMetrics().values()) {
            maxLength = Math.max(character.getLength(), maxLength);
        }

        for (MinecraftCharacter character : FontMetrics.getMetrics().values()) {
            int gap = 10; // gap should be greater than 1. Generating a padding for 1 space length is impossible for default minecraft font
            int paddingLength = maxLength + gap - character.getLength();

            player.sendMessage(character + FontMetrics.makePadding(paddingLength) + "|");
        }

        player.sendMessage("All | should be aligned");
    }

    @Getter
    @RequiredArgsConstructor
    private static class MinecraftCharacter {
        private final char character;
        private final int length;

        @Override
        public String toString() {
            return String.valueOf(this.getCharacter());
        }
    }
}
