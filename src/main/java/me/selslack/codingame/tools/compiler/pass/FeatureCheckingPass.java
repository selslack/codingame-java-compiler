package me.selslack.codingame.tools.compiler.pass;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.visitor.GenericVisitorAdapter;
import javaslang.Tuple2;
import javaslang.collection.List;
import javaslang.collection.Set;
import me.selslack.codingame.tools.compiler.CompilationException;
import me.selslack.codingame.tools.compiler.CompilationFeatureException;
import me.selslack.codingame.tools.compiler.Type;

import java.util.Optional;

public class FeatureCheckingPass implements CompilerPass<Set<Type>, Set<Type>> {
    @Override
    public Set<Type> process(final Set<Type> input) throws Exception {
        Optional<Type> solution = Optional.empty();

        for (final Type type : input) {
            final List<Type> inner = type.getInnerTypes();
            final Optional<ClassOrInterfaceDeclaration> local = Optional.ofNullable(
                type.getBody().accept(new LocalClassCheckerVisitor(), true)
            );

            for (final ImportDeclaration imp : type.getImports()) {
                if (imp.isStatic()) {
                    throw new CompilationFeatureException("static import", type, imp.getBeginLine());
                }

                if (imp.isAsterisk()) {
                    throw new CompilationFeatureException("asterisk import", type, imp.getBeginLine());
                }
            }
            
            if (inner.size() > 0) {
                throw new CompilationFeatureException("inner class", type, inner.head().getBody().getBeginLine());
            }

            if (local.isPresent()) {
                throw new CompilationFeatureException("local class", type, local.get().getBeginLine());
            }

            if (type.isSolution()) {
                if (solution.isPresent()) {
                    throw new CompilationFeatureException("ambiguous solution", solution.get(), solution.get().getBody().getBeginLine());
                }
                else {
                    solution = Optional.of(type);
                }
            }
        }

        if (!solution.isPresent()) {
            throw new CompilationException("Solution class wasn't found. Please mark any public non-abstract class with @solution Javadoc tag.");
        }

        for (final Tuple2<String, ? extends Set<Type>> grouped : input.groupBy(t -> t.getName())) {
            final Type head = grouped._2.head();

            if (grouped._2.size() > 1) {
                throw new CompilationFeatureException("equal class name", head, head.getBody().getBeginLine());
            }
        }

        for (final Tuple2<String, ? extends Set<Type>> grouped : input.groupBy(t -> t.getFullName())) {
            final Type head = grouped._2.head();

            if (grouped._2.size() > 1) {
                throw new CompilationFeatureException("equal fqcn", head, head.getBody().getBeginLine());
            }
        }

        return input;
    }

    private static class LocalClassCheckerVisitor extends GenericVisitorAdapter<ClassOrInterfaceDeclaration, Boolean> {
        @Override
        public ClassOrInterfaceDeclaration visit(final ClassOrInterfaceDeclaration node, final Boolean inClassScope) {
            List<Class> validParents = List.of(
                CompilationUnit.class,
                ClassOrInterfaceDeclaration.class
            );

            if (inClassScope) {
                for (Class clazz : validParents) {
                    if (clazz.isInstance(node.getParentNode())) {
                        return super.visit(node, true);
                    }
                }

                return super.visit(node, false);
            }
            else {
                return node;
            }
        }
    }
}
