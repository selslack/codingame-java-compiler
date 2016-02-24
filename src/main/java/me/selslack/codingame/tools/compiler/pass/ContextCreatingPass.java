package me.selslack.codingame.tools.compiler.pass;

import javaslang.collection.Set;
import me.selslack.codingame.tools.compiler.CompilationContext;
import me.selslack.codingame.tools.compiler.Type;

public class ContextCreatingPass implements CompilerPass<Set<Type>, CompilationContext> {
    @Override
    public CompilationContext process(final Set<Type> input) throws Exception {
        return new CompilationContext(input);
    }
}
