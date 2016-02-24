package me.selslack.codingame.tools.compiler;

import javaslang.collection.List;
import me.selslack.codingame.tools.compiler.pass.*;

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
            .thenProcessing(new FeatureCheckingPass())
            .thenProcessing(new ContextCreatingPass())
            .thenProcessing(new DependencySolvingPass())
            .thenProcessing(new OutputWritingPass(out))
            .process(null)
        ;
    }
}
