package me.selslack.codingame.tools.compiler.pass;

import com.github.javaparser.ast.Node;
import com.github.javaparser.symbolsolver.javaparsermodel.JavaParserFacade;
import io.vavr.CheckedFunction2;
import io.vavr.collection.List;
import io.vavr.collection.Stream;

import me.selslack.codingame.tools.compiler.CompilationContext;

abstract public class AbstractRewritingPass implements CompilerPass<CompilationContext, CompilationContext> {
    private final List<CheckedFunction2<Node, JavaParserFacade, Node>> mappers;

    @SafeVarargs
    protected AbstractRewritingPass(CheckedFunction2<Node, JavaParserFacade, Node>... mappers) {
        if (mappers.length < 1) {
            throw new IllegalArgumentException();
        }

        this.mappers = List.of(mappers);
    }

    @Override
    public final CompilationContext process(CompilationContext input) throws Throwable {
        for (Node node : input.getTypes().valuesIterator().map(t -> t.getTypeDeclaration().stream()).flatMap(Stream::ofAll)) {
            for (CheckedFunction2<Node, JavaParserFacade, Node> mapper : mappers) {
                if (node.findCompilationUnit().isEmpty()) {
                    break;
                }

                Node result = mapper.apply(node, input.getTypeSolver().getFacade());

                if (result == null) {
                    node.remove();
                }
                else if (result != node) {
                    node.replace(result);
                }
            }
        }

        return input;
    }
}
