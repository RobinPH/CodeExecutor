package io.github.robinph.codeexecutor.core.argument.argument;


import io.github.robinph.codeexecutor.core.argument.Argument;
import io.github.robinph.codeexecutor.utils.Prefix;

public class IntegerArgument extends Argument {

    public IntegerArgument() { super(); }
    public IntegerArgument(String name) { super(name); }

    public static int value(String value) {
        return Integer.parseInt(value);
    }


    @Override
    public boolean validateType(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public String errorType(String value) {
        return Prefix.ERROR + "Invalid number: " + value;
    }

    @Override
    public String usage() {
        return "<" + (this.isNullable() ? "?" : "") + (this.getName() != null ? this.getName() : "number") + ">";
    }
}
