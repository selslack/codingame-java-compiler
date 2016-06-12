package me.selslack.codingame.tools.compiler.pass;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import javaslang.collection.HashSet;
import javaslang.collection.List;
import javaslang.collection.Set;
import me.selslack.codingame.tools.compiler.Type;

import java.io.File;
import java.util.Optional;

public class SourceParsingPass implements CompilerPass<List<File>, Set<Type>> {
    @Override
    public Set<Type> process(List<File> input) throws Exception {
        Set<Type> context = HashSet.empty();

        for (File source : input) {
            try {
                CompilationUnit unit = JavaParser.parse(source);
                PackageDeclaration pkg = unit.getPackage();
                List<ImportDeclaration> imports = List.ofAll(unit.getImports());

                for (TypeDeclaration type : unit.getTypes()) {
                    context = context.add(new Type(Optional.ofNullable(pkg), imports, type, source.getAbsolutePath()));
                }
            }
            catch (ParseException e) {
                throw new RuntimeException(String.format("Can not parse %s", source.getAbsolutePath()), e);
            }
        }

        return context;
    }
}
