package me.selslack.codingame.tools.compiler.pass;

import io.vavr.collection.List;

import java.io.File;
import java.nio.file.Files;

public class SourceFindingPass implements CompilerPass<Void, List<File>> {
    private final List<File> sources;

    public SourceFindingPass(List<File> sources) {
        this.sources = sources;
    }

    @Override
    public List<File> process(Void input) throws Throwable {
        List<File> result = List.empty();

        for (File source : sources) {
            List<File> files = Files.walk(source.toPath())
                .filter(p -> Files.isRegularFile(p))
                .filter(p -> p.toString().endsWith(".java"))
                .map(p -> p.toFile())
                .collect(List.collector());

            result = result.appendAll(files);
        }

        return result;
    }
}
