package io.github.robinph.codeexecutor.commands.argv;

import io.github.robinph.codeexecutor.core.command.AbstractCommand;
import io.github.robinph.codeexecutor.core.argument.argument.StringArgument;

public class ArgvCommand extends AbstractCommand {
    public ArgvCommand() {
        super("argv");

        this.addChild(new CountCommand());
        this.addChild(new SetCommand());

        this.addArgument(new StringArgument());

        this.getArguments().setLastArgArbitraryLength(true);
    }
}
