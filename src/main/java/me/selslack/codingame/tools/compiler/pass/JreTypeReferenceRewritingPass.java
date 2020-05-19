package me.selslack.codingame.tools.compiler.pass;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import io.vavr.collection.Stream;

import me.selslack.codingame.tools.compiler.CompilationContext;

import static me.selslack.codingame.tools.compiler.pass.ReferenceSolvingPass.TYPE_REF;

public class JreTypeReferenceRewritingPass implements CompilerPass<CompilationContext, CompilationContext> {
    @Override
    public CompilationContext process(CompilationContext input) {
        for (TypeDeclaration<?> declaration : input.getTypes().valuesIterator().map(t -> t.getTypeDeclaration())) {
            for (Node node : Stream.ofAll(declaration.stream())) {
                if (node instanceof ClassOrInterfaceType) {
                    if (node.containsData(TYPE_REF)) {
                        var reference = node.getData(TYPE_REF);
                        var fqn = reference.getQualifiedName();

                        if (fqn.startsWith("java.") && !fqn.startsWith("java.lang.")) {
                            Node replacement = StaticJavaParser.parseClassOrInterfaceType(
                                reference.getQualifiedName()
                            );

                            replacement.setData(TYPE_REF, reference);

                            node.replace(replacement);
                        }
                    }
                }
            }
        }

        return input;
    }
}
