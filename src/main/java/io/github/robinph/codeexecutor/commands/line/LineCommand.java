package io.github.robinph.codeexecutor.commands.line;

import io.github.robinph.codeexecutor.core.command.AbstractCommand;

public class LineCommand extends AbstractCommand {
    public LineCommand() {
        super("line");

        this.addChild(new InsertCommand());
        this.addChild(new EditCommand());
        this.addChild(new DeleteCommand());
        this.addChild(new MoveCommand());
    }
}
