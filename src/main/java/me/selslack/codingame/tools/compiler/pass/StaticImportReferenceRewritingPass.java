package me.selslack.codingame.tools.compiler.pass;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.resolution.declarations.ResolvedFieldDeclaration;
import io.vavr.collection.Stream;

import me.selslack.codingame.tools.compiler.CompilationContext;

import static me.selslack.codingame.tools.compiler.pass.ReferenceSolvingPass.METHOD_REF;
import static me.selslack.codingame.tools.compiler.pass.ReferenceSolvingPass.VALUE_REF;

public class StaticImportReferenceRewritingPass implements CompilerPass<CompilationContext, CompilationContext> {
    @Override
    public CompilationContext process(CompilationContext input) {
        for (TypeDeclaration<?> declaration : input.getTypes()
                                                   .valuesIterator()
                                                   .map(t -> t.getTypeDeclaration())) {
            for (Node node : Stream.ofAll(declaration.stream())) {
                if (node.containsData(VALUE_REF)) {
                    var reference = node.getData(VALUE_REF);

                    if (reference instanceof ResolvedFieldDeclaration) {
                        if (((ResolvedFieldDeclaration) reference).isStatic()) {
                            if (node instanceof NameExpr) {
                                var expr = String.format(
                                    "%s.%s",
                                    ((ResolvedFieldDeclaration) reference).declaringType().getQualifiedName(),
                                    ((NameExpr) node).getNameAsString()
                                );

                                if (expr.startsWith("java.")) {
                                    if (expr.startsWith("java.lang.")) {
                                        expr = expr.substring(10);
                                    }

                                    node.replace(
                                        StaticJavaParser.parseExpression(expr)
                                    );
                                }
                            }
                        }
                    }
                } else if (node.containsData(METHOD_REF)) {
                    var reference = node.getData(METHOD_REF);

                    if (reference.isStatic()) {
                        if (node instanceof MethodCallExpr) {
                            var expr = reference.declaringType().getQualifiedName();

                            if (expr.startsWith("java.")) {
                                if (expr.startsWith("java.lang.")) {
                                    expr = expr.substring(10);
                                }

                                ((MethodCallExpr) node).setScope(
                                    StaticJavaParser.parseExpression(expr)
                                );
                            }
                        }
                    }
                }
            }
        }

        return input;
    }
}
