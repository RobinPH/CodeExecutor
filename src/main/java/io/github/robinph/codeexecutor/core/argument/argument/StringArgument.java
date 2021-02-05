package io.github.robinph.codeexecutor.core.argument.argument;

import io.github.robinph.codeexecutor.core.argument.Argument;
import io.github.robinph.codeexecutor.utils.Prefix;

public class StringArgument extends Argument {

    public StringArgument() { super(); }
    public StringArgument(String name) { super(name); }

    public static String value(String value) {
        return value;
    }

    @Override
    public boolean validateType(String value) {
        return true;
    }

    @Override
    public String errorType(String value) {
        return Prefix.ERROR + "Invalid string: " + value;
    }

    @Override
    public String usage() {
        return "<" + (this.isNullable() ? "?" : "") + (this.getName() != null ? this.getName() : "string") + ">";
    }
}
