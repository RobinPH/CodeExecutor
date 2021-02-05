package io.github.robinph.codeexecutor.core.argument.argument;

import io.github.robinph.codeexecutor.core.argument.Argument;
import io.github.robinph.codeexecutor.utils.Prefix;

public class BooleanArgument extends Argument {
    public BooleanArgument() { super(); }
    public BooleanArgument(String name) { super(name); }

    public static boolean value(String value) {
        switch (value.toLowerCase()) {
            case "1":
            case "true":
            case "yes":
            case "yup":
            case "yeah":
            case "on":
                return true;
            case "0":
            case "false":
            case "no":
            case "nope":
            case "nah":
            case "off":
            default:
                return false;
        }
    }

    @Override
    public boolean validateType(String value) {
        switch (value.toLowerCase()) {
            case "1":
            case "true":
            case "yes":
            case "yup":
            case "yeah":
            case "on":
            case "0":
            case "false":
            case "no":
            case "nope":
            case "nah":
            case "off":
                return true;
            default:
                return false;
        }
    }

    @Override
    public String errorType(String value) {
        return Prefix.ERROR + "Invalid boolean value: " + value;
    }

    @Override
    public String usage() {
        return "<true | false>";
    }
}
