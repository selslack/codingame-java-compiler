package me.selslack.codingame.tools.compiler.pass;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class SourceParsingPass implements CompilerPass<List<File>, List<CompilationUnit>> {
    @Override
    public List<CompilationUnit> process(List<File> input) throws Exception {
        List<CompilationUnit> result = new LinkedList<>();

        for (File file : input) {
            result.add(JavaParser.parse(file, null, false));
        }

        return result;
    }
}
