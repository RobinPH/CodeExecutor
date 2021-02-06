package io.github.robinph.codeexecutor.utils;

import net.md_5.bungee.api.ChatColor;

public class FontUtils {

    public static String colorString(char code, String text) {
        if (ChatColor.ALL_CODES.indexOf(code) == -1) {
            return text;
        }

        String RESET_CODES = "0123456789AaBbCcDdEeFfRr";

        StringBuilder builder = new StringBuilder("§" + code);

        char[] chars = text.toCharArray();

        int lastBreak = 0;
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == ChatColor.COLOR_CHAR) {
                if (i < chars.length - 1) {
                    if (RESET_CODES.indexOf(chars[i + 1]) != -1) {
                        builder.append(text, lastBreak, i + 2).append("§").append(code);
                        lastBreak = i + 2;
                        i++;
                    }
                }
            }
        }

        if (lastBreak == 0) {
            return text;
        } else {
            builder.append(text.substring(lastBreak));
            return builder.toString();
        }
    }

    public static String trimString(String text, int maxWidth) {
        return FontUtils.trimString(text, "...", maxWidth);
    }

    public static String trimString(String text, String suffix, int maxWidth) {
        int suffixLength = FontMetrics.getLength(suffix);

        if (maxWidth <= suffixLength) {
            return suffix;
        }

        int length = FontMetrics.getLength(text);
        if (length <= maxWidth) {
            return text;
        }

        int charToTrim = 0;

        while (length - suffixLength > maxWidth) {
            length -= FontMetrics.getLength(text.charAt(text.length() - 1 - charToTrim));
            charToTrim++;
        }

        return text.substring(0, text.length() - charToTrim) + suffix;
    }
}
