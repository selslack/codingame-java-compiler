package me.selslack.codingame.tools.compiler.pass;

import com.github.javaparser.ParseProblemException;
import com.github.javaparser.StaticJavaParser;
import io.vavr.collection.HashSet;
import io.vavr.collection.List;
import io.vavr.collection.Set;

import me.selslack.codingame.tools.compiler.Type;

import java.io.File;

public class SourceParsingPass implements CompilerPass<List<File>, Set<Type>> {
    @Override
    public Set<Type> process(List<File> input) throws Exception {
        Set<Type> context = HashSet.empty();

        for (File source : input) {
            try {
                context = context.addAll(
                    List.ofAll(StaticJavaParser.parse(source).getTypes())
                        .map(t -> new Type(t))
                );
            } catch (ParseProblemException e) {
                throw new RuntimeException(String.format("Can not parse %s", source.getAbsolutePath()), e);
            }
        }

        return context;
    }
}
