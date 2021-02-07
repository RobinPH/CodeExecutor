package io.github.robinph.codeexecutor.commands.argv;

import io.github.robinph.codeexecutor.core.command.AbstractCommand;

public class ArgvCommand extends AbstractCommand {
    public ArgvCommand() {
        super("argv");

        this.addChild(new CountCommand());
        this.addChild(new SetCommand());
    }
}
