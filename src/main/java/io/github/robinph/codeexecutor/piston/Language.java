package io.github.robinph.codeexecutor.piston;


import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Language {
    private @Getter final String name;
    private @Getter final String prefix;
    private @Getter final List<String> aliases = new ArrayList<>();
    private @Getter @Setter String version;

    public Language(String name, String prefix, String ...aliases) {
        this.name = name;
        this.prefix = prefix;
        this.aliases.addAll(Arrays.asList(aliases));
    }

    public void addAlias(String alias) {
        this.aliases.add(alias);
    }
}
