package me.selslack.codingame.tools.compiler.pass;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.DataKey;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.resolution.types.ResolvedReferenceType;
import com.github.javaparser.resolution.types.ResolvedType;
import com.github.javaparser.symbolsolver.javaparsermodel.JavaParserFacade;
import com.github.javaparser.symbolsolver.model.resolution.SymbolReference;
import io.vavr.Tuple2;
import io.vavr.collection.List;
import io.vavr.collection.Stream;
import io.vavr.control.Option;

import me.selslack.codingame.tools.compiler.CompilationContext;

import java.util.function.Function;

import static me.selslack.codingame.tools.compiler.pass.ReferenceSolvingPass.TYPE_REF;

public class CompilationPass implements CompilerPass<CompilationContext, CompilationUnit> {
    public static final DataKey<SymbolReference<?>> SYMBOL_REF = new DataKey<>() { };
    public static final DataKey<String> TYPE = new DataKey<>() { };

    @Override
    public CompilationUnit process(CompilationContext input) throws Exception {
        CompilationUnit result = new CompilationUnit();
        JavaParserFacade facade = input.getTypeSolver().getFacade();

        for (Node node : dependencies(input.getPlayer().getTypeDeclaration(), facade)) {
            if (node instanceof ImportDeclaration) {
                result.addImport((ImportDeclaration) node);
            }
            else if (node instanceof TypeDeclaration) {
                result.addType((TypeDeclaration<?>) node);
            }
        }

        return result;
    }

    static List<Node> dependencies(TypeDeclaration<?> original, JavaParserFacade facade) {
        var copy = original.clone();
        var result = List.<Node>empty();

        for (Node t : Stream.ofAll(copy.stream(Node.TreeTraversal.POSTORDER))) {
            if (t.containsData(TYPE_REF)) {
                ResolvedType typeRef = t.getData(TYPE_REF);

                if (typeRef instanceof ResolvedReferenceType) {
                    result = result.append(
                        new ImportDeclaration(
                            ((ResolvedReferenceType) typeRef).getQualifiedName(),
                            false,
                            false
                        )
                    );
                }
            }
        }

        copy.removeModifier(
            Modifier.Keyword.PUBLIC,
            Modifier.Keyword.PROTECTED,
            Modifier.Keyword.PRIVATE
        );

        return result.prepend(copy);
    }

    static <N extends Node> Option<String> solve(N original, N copy, Function<N, ResolvedType> mapper) {
        var resolved = mapper.apply(original);

        if (resolved instanceof ResolvedReferenceType) {
            var fqn = ((ResolvedReferenceType) resolved).getQualifiedName();

            copy.setData(TYPE, fqn);

            return Option.of(fqn);
        }

        return Option.none();
    }
}
