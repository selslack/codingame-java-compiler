package me.selslack.codingame.tools.compiler.pass;

import me.selslack.codingame.tools.compiler.CompilationContext;
import me.selslack.codingame.tools.compiler.Type;

public class DependencySolvingPass implements CompilerPass<CompilationContext, CompilationContext> {
    @Override
    public CompilationContext process(CompilationContext input) throws Exception {
        for (Type type : input.getTypes()) {

        }

        return input;
    }
}
