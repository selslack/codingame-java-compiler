package me.selslack.codingame.tools.compiler.pass;

import me.selslack.codingame.tools.compiler.CompilationContext;

public class DependencyBuildingPass implements CompilerPass<CompilationContext, CompilationContext> {
    @Override
    public CompilationContext process(CompilationContext input) throws Exception {
        input.getTypes().toStream();

        return null;
    }
}
