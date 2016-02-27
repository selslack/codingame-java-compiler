package me.selslack.codingame.tools.compiler;

import javaslang.collection.Map;
import javaslang.collection.Set;

import java.util.Optional;
import java.util.function.Function;

public class CompilationContext {
    private final Set<Type> types;
    private final Map<String, Type> byFqcn;
    private final Map<String, ? extends Set<Type>> byPackage;
    private final Map<String, ? extends Set<Type>> byUnit;
    private final Type solution;

    public CompilationContext(Set<Type> types) {
        this.types = types;
        this.byFqcn = types.groupBy(t -> t.getFullName()).mapValues(t -> t.head());
        this.byPackage = types.groupBy(t -> t.getPackageName());
        this.byUnit = types.groupBy(t -> t.getUnitId());
        this.solution = types.find(t -> t.isSolution()).get();
    }

    public Set<Type> getTypes() {
        return types;
    }

    public Optional<Type> getByFqcn(String fqcn) {
        return byFqcn.get(fqcn).toJavaOptional();
    }

    public Optional<Set<Type>> getByPackage(String pkg) {
        Optional<? extends Set<Type>> types = byPackage.get(pkg).toJavaOptional();

        if (types.isPresent()) {
            return Optional.of(types.get());
        }
        else {
            return Optional.empty();
        }
    }

    public Type getSolution() {
        return solution;
    }

    public CompilationContext map(Function<Type, Type> mapper) {
        return new CompilationContext(types.map(mapper));
    }
}
