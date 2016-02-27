package me.selslack.codingame.tools.compiler.dependency;

import javaslang.collection.Set;
import me.selslack.codingame.tools.compiler.CompilationContext;
import me.selslack.codingame.tools.compiler.Type;

import java.util.Optional;

public class PackageSolver implements Solver {
    private final String pkg;
    private final CompilationContext context;

    public PackageSolver(String pkg, CompilationContext context) {
        this.pkg = pkg;
        this.context = context;
    }

    @Override
    public Optional<String> solve(String type) {
        Optional<Set<Type>> types = context.getByPackage(pkg);

        if (types.isPresent()) {
            for (Type test : types.get()) {
                if (test.getName().equals(type)) {
                    return Optional.of(test.getFullName());
                }
            }
        }

        return Optional.empty();
    }
}
