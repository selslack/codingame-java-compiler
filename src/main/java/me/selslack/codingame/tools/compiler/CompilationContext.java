package me.selslack.codingame.tools.compiler;

import javaslang.collection.Map;
import javaslang.collection.Set;
import javaslang.collection.Stream;

public class CompilationContext {
    private final Set<Type> types;
    private final Type solutionClass;
    private Map<Type, Set<?>> dependencies;

    public CompilationContext(Set<Type> types, Type solutionClass) {
        this.types = types;
        this.solutionClass = solutionClass;
    }

    public Type getSolutionClass() {
        return solutionClass;
    }

    public Set<Type> getTypes() {
        return types;
    }

    public Stream<Type> getTypesStream() {
        return types.toStream();
    }
}
