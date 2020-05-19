package me.selslack.codingame.tools.compiler.pass;

import com.github.javaparser.ast.CompilationUnit;

import java.io.Writer;

public class OutputWritingPass implements CompilerPass<CompilationUnit, Void> {
    private final Writer writer;

    public OutputWritingPass(Writer writer) {
        this.writer = writer;
    }

    @Override
    public Void process(CompilationUnit input) throws Exception {
        writer.write(input.toString());
        writer.flush();

        return null;
    }
}
