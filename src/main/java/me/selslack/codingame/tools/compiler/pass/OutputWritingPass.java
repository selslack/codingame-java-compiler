package me.selslack.codingame.tools.compiler.pass;

import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.body.ModifierSet;
import com.github.javaparser.ast.body.TypeDeclaration;
import me.selslack.codingame.tools.compiler.CompilationContext;
import me.selslack.codingame.tools.compiler.Type;

import java.io.Writer;

public class OutputWritingPass implements CompilerPass<CompilationContext, Void> {
    private final Writer writer;

    public OutputWritingPass(Writer writer) {
        this.writer = writer;
    }

    @Override
    public Void process(CompilationContext input) throws Exception {
        Type type = input.getSolutionClass();
        TypeDeclaration declaration = type.getBody();

        declaration.setModifiers(
            ModifierSet.removeModifier(declaration.getModifiers(), ModifierSet.PUBLIC)
        );

        for (ImportDeclaration imp : type.getImports()) {
            writer.write(imp.toString());
        }

        writer.write(declaration.toString());
        writer.flush();

        return null;
    }
}
