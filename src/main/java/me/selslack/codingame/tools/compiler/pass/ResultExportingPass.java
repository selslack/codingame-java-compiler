package me.selslack.codingame.tools.compiler.pass;

import me.selslack.codingame.tools.compiler.CompilationContext;

import java.io.File;

public class ResultExportingPass implements CompilerPass<CompilationContext, Void> {
    final private File out;

    public ResultExportingPass(File out) {
        this.out = out;
    }

    @Override
    public Void process(CompilationContext input) throws Exception {
        return null;
    }
}
