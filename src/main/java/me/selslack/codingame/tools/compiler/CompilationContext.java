package me.selslack.codingame.tools.compiler;

import javaslang.collection.HashMap;
import javaslang.collection.Map;
import javaslang.collection.Set;

import java.util.function.Function;

public class CompilationContext {
    private final Set<Type> types;
    private final Map<String, Type> byFqcn;
    private final Map<String, ? extends Set<Type>> byPackage;
    private final Type solution;

    public CompilationContext(Set<Type> types) {
        this.types = types;
        this.byFqcn = HashMap.empty();
        this.byPackage = types.groupBy(t -> t.getPackageName());
        this.solution = types.find(t -> t.isSolution()).get();
    }

    public Set<Type> getTypes() {
        return types;
    }

    public Type getSolution() {
        return solution;
    }

    public CompilationContext map(Function<Type, Type> mapper) {
        return new CompilationContext(types.map(mapper));
    }
}
