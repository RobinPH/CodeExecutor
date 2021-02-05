package io.github.robinph.codeexecutor.core.argument;

import lombok.Getter;

public abstract class Argument {
    private @Getter boolean nullable = false;
    protected @Getter String name;

    abstract public boolean validateType(String value);
    abstract public String errorType(String value);
    abstract public String usage();

    public Argument() {}
    public Argument(String name) {
        this.name = name;
    }

    public boolean validate(String value) {
        if (value == null) {
            return this.isNullable();
        }

        return validateType(value);
    }

    public String error(String value) {
        if (value == null && !this.isNullable()) {
            return "Expecting an argument. Received: null";
        }

        return this.errorType(value);
    }


    public void setNullable(boolean b) {
        this.nullable = b;

    }
}
