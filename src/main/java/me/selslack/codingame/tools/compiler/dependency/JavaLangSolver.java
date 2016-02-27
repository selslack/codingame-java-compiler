package me.selslack.codingame.tools.compiler.dependency;

import java.util.Optional;

public class JavaLangSolver implements Solver {
    @Override
    public Optional<String> solve(String type) {
        try {
            return Optional.of(
                getClass().getClassLoader().loadClass("java.lang." + type).getCanonicalName()
            );
        }
        catch (ClassNotFoundException e) {
            return Optional.empty();
        }
    }
}
