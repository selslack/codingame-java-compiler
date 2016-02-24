package me.selslack.codingame.tools.compiler.ast;

import javaslang.collection.HashMap;
import javaslang.collection.Map;

import java.util.Optional;

public class Metadata {
    /** There should be only one */
    private static final Metadata EMPTY = new Metadata();

    private Map<String, Object> metadata;

    private Metadata() {
        this.metadata = HashMap.empty();
    }

    private Metadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    public static Metadata empty() {
        return EMPTY;
    }

    public Metadata setType(String type) {
        return new Metadata(metadata.put("type", type));
    }

    public Optional<String> getType() {
        if (metadata.containsKey("type")) {
            return Optional.of((String) metadata.get("type").get());
        }
        else {
            return Optional.empty();
        }
    }
}
