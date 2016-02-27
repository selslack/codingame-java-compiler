package me.selslack.codingame.tools.compiler.dependency;

import com.github.javaparser.ast.ImportDeclaration;
import me.selslack.codingame.tools.compiler.Type;

import java.util.Optional;

public class SimpleImportSolver implements Solver {
    private final Type context;

    public SimpleImportSolver(Type context) {
        this.context = context;
    }

    @Override
    public Optional<String> solve(String type) {
        for (ImportDeclaration imp : context.getImports()) {
            if (imp.getName().getName().equals(type)) {
                return Optional.of(
                    imp.getName().toStringWithoutComments()
                );
            }
        }

        return Optional.empty();
    }
}
