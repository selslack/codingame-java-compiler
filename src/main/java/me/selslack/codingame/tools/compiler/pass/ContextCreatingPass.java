package me.selslack.codingame.tools.compiler.pass;

import javaslang.collection.Set;
import me.selslack.codingame.tools.compiler.CompilationContext;
import me.selslack.codingame.tools.compiler.CompilationException;
import me.selslack.codingame.tools.compiler.Type;

public class ContextCreatingPass implements CompilerPass<Set<Type>, CompilationContext> {
    private final String solutionClass;

    public ContextCreatingPass(String solutionClass) {
        this.solutionClass = solutionClass;
    }

    @Override
    public CompilationContext process(final Set<Type> input) throws Exception {
        Set<Type> matches = input.toStream()
            .filter(v -> v.isPublic() && !v.isAbstract())
            .filter(v -> v.getFullName().endsWith(solutionClass))
            .toSet();

        switch (matches.length()) {
            case 1:
                return new CompilationContext(input, matches.head());

            case 0:
                throw new CompilationException(this, String.format("Solution class matching '%s' was not found", solutionClass));

            default:
                throw new CompilationException(this, String.format("The definition of solution class '%s' is too vague", solutionClass));
        }
    }
}
