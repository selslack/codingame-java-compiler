package me.selslack.codingame.tools.compiler;

import io.vavr.collection.List;

import me.selslack.codingame.tools.compiler.pass.CompilationPass;
import me.selslack.codingame.tools.compiler.pass.CompilerPass;
import me.selslack.codingame.tools.compiler.pass.ContextCreatingPass;
import me.selslack.codingame.tools.compiler.pass.OutputWritingPass;
import me.selslack.codingame.tools.compiler.pass.ReferenceSolvingPass;
import me.selslack.codingame.tools.compiler.pass.SourceFindingPass;
import me.selslack.codingame.tools.compiler.pass.SourceParsingPass;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

public class Compiler {
    private final List<File> sources;
    private final Writer out;

    public Compiler(List<File> sources, Writer out) {
        this.sources = sources;
        this.out = out;
    }

    public Compiler(File[] sources, Writer out) {
        this(List.of(sources), out);
    }

    public Compiler(Options options) throws IOException {
        this(options.sources, new FileWriter(options.out));
    }

    public void compile() throws Exception {
        CompilerPass.processing(new SourceFindingPass(sources))
                    .thenProcessing(new SourceParsingPass())
                    .thenProcessing(new ContextCreatingPass())
                    .thenProcessing(new ReferenceSolvingPass())
                    .thenProcessing(new CompilationPass())
                    .thenProcessing(new OutputWritingPass(out))
                    .process(null)
        ;
    }
}
