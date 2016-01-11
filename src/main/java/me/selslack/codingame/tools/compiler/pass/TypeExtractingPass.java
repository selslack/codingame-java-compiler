package me.selslack.codingame.tools.compiler.pass;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.TypeDeclaration;

import java.util.List;
import java.util.stream.Collectors;

public class TypeExtractingPass implements CompilerPass<List<CompilationUnit>, List<TypeDeclaration>> {
    @Override
    public List<TypeDeclaration> process(List<CompilationUnit> input) throws Exception {
        return input.stream().flatMap(v -> v.getTypes().stream()).collect(Collectors.toList());
    }
}
