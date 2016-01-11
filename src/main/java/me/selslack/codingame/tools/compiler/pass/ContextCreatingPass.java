package me.selslack.codingame.tools.compiler.pass;

import com.github.javaparser.ast.body.TypeDeclaration;
import me.selslack.codingame.tools.compiler.CompilationContext;

import java.util.List;

public class ContextCreatingPass implements CompilerPass<List<TypeDeclaration>, CompilationContext> {
    @Override
    public CompilationContext process(List<TypeDeclaration> input) throws Exception {
        return new CompilationContext(input);
    }
}
