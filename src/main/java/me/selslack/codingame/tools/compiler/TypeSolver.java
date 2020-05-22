package me.selslack.codingame.tools.compiler;

import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedReferenceTypeDeclaration;
import com.github.javaparser.symbolsolver.javaparsermodel.JavaParserFacade;
import com.github.javaparser.symbolsolver.model.resolution.SymbolReference;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ClassLoaderTypeSolver;
import io.vavr.collection.List;
import io.vavr.control.Option;

import java.util.Objects;

public class TypeSolver implements com.github.javaparser.symbolsolver.model.resolution.TypeSolver {
    private final CompilationContext context;
    private final com.github.javaparser.symbolsolver.model.resolution.TypeSolver jre;
    private com.github.javaparser.symbolsolver.model.resolution.TypeSolver parent;

    public TypeSolver(CompilationContext context) {
        this.context = context;

        this.jre = new ClassLoaderTypeSolver(TypeSolver.class.getClassLoader()) {
            @Override
            protected boolean filterName(String name) {
                return name.startsWith("java.");
            }
        };
    }

    public JavaParserFacade getFacade() {
        return JavaParserFacade.get(this);
    }

    @Override
    public com.github.javaparser.symbolsolver.model.resolution.TypeSolver getParent() {
        return parent;
    }

    @Override
    public void setParent(com.github.javaparser.symbolsolver.model.resolution.TypeSolver parent) {
        Objects.requireNonNull(parent);

        if (this.parent != null) {
            throw new IllegalStateException("This TypeSolver already has a parent.");
        }

        if (parent == this) {
            throw new IllegalStateException("The parent of this TypeSolver cannot be itself.");
        }

        this.parent = parent;
    }

    @Override
    public SymbolReference<ResolvedReferenceTypeDeclaration> tryToSolveType(String name) {
        List<String> parts = List.of(name.split("\\."));

        for (int l = parts.length(), r = 0; l > 0; l--, r++) {
            String outer = parts.take(l).mkString(".");
            String inner = parts.takeRight(r + 1).mkString(".");

            Option<Type> type = context.getType(outer);

            if (type.isEmpty()) {
                continue;
            }

            Option<TypeDeclaration<?>> declaration = findType(type.get().getTypeDeclaration(), inner);

            if (declaration.isDefined()) {
                return SymbolReference.solved(JavaParserFacade.get(this).getTypeDeclaration(declaration.get()));
            }
            else {
                return SymbolReference.unsolved(ResolvedReferenceTypeDeclaration.class);
            }
        }

        return jre.tryToSolveType(name);
    }

    static private Option<TypeDeclaration<?>> findType(TypeDeclaration<?> declaration, String name) {
        String[] parts = name.split("\\.", 2);

        if (declaration.getName().getId().equals(parts[0])) {
            switch (parts.length) {
                case 1:
                    return Option.of(declaration);

                case 2:
                    for (TypeDeclaration<?> inner : declaration.findAll(TypeDeclaration.class, i -> i != declaration)) {
                        var result = findType(inner, parts[1]);

                        if (result.isDefined()) {
                            return result;
                        }
                    }
            }
        }

        return Option.none();
    }
}
