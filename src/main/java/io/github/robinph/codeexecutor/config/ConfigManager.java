package io.github.robinph.codeexecutor.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.Map;

public class ConfigManager {
    private @Getter final FileConfiguration config;

    public ConfigManager(FileConfiguration config) {
        this.config = config;
        this.addDefaults();
    }

    public int get(Variable var) {
        if (this.config.contains(var.toString())) {
            return this.config.getInt(var.toString());
        }

        return ConfigManager.DEFAULT.get(var);
    }

    public void addDefaults() {
        for (Variable var : ConfigManager.Variable.values()) {
            if (ConfigManager.DEFAULT.containsKey(var)) {
                this.config.addDefault(var.toString(), ConfigManager.DEFAULT.get(var));
            }
        }
    }

    @RequiredArgsConstructor
    public enum Variable {
        EDITOR_HEIGHT("editor-height"),
        MAX_LINE_LENGTH("max-line-length"),
        MAX_LINE_COUNT("max-line-count"),
        MAX_CHARACTER_PER_LINE("max-character-per-line"),
        MAX_OUTPUT_LINE("max-output-line"),
        MAX_SIZE_PER_OUTPUT("max-size-per-output");

        private @Getter final String name;

        @Override
        public String toString() {
            return this.name;
        }
    }

    public static Map<Variable, Integer> DEFAULT = new HashMap<Variable, Integer>(){{
        put(Variable.EDITOR_HEIGHT, 20);
        put(Variable.MAX_LINE_LENGTH, 192);
        put(Variable.MAX_LINE_COUNT, 1028);
        put(Variable.MAX_CHARACTER_PER_LINE, 128);
        put(Variable.MAX_OUTPUT_LINE, 64);
        put(Variable.MAX_SIZE_PER_OUTPUT, 16384);
    }};
}
