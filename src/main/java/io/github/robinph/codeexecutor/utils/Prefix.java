package io.github.robinph.codeexecutor.utils;

public interface Prefix {
    String PREFIX = "§3[§bCodeExecutor§3] ";

    String NORMAL = Prefix.PREFIX + Prefix.NORMAL_COLOR;
    String SUCCESS = Prefix.PREFIX + Prefix.SUCCESS_COLOR;
    String WARNING = Prefix.PREFIX + Prefix.WARNING_COLOR;
    String ERROR = Prefix.PREFIX + Prefix.ERROR_COLOR;

    String NORMAL_COLOR = "§7";
    String SUCCESS_COLOR = "§a";
    String WARNING_COLOR = "§e";
    String ERROR_COLOR = "§c";

}
