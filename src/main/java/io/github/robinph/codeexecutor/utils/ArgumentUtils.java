package io.github.robinph.codeexecutor.utils;

import java.util.ArrayList;
import java.util.List;

public class ArgumentUtils {
    public static List<String> parse(char encloser, String args) {
        List<String> arguments = new ArrayList<>();

        if (args.replaceAll(String.format("[^%s]", encloser), "").length() % 2 == 1) {
            return arguments;
        }

        int opening = -1;
        for (int i = 0; i < args.length(); i++) {
            if (args.charAt(i) == encloser) {
                if (opening == -1) {
                    opening = i;
                } else {
                    if (opening + 1 < args.length()) {
                        arguments.add(args.substring(opening + 1, i));
                    }
                    opening = -1;
                }
            }
        }

        return arguments;
    }
}
