package me.selslack.codingame.tools.compiler.pass;

import com.github.javaparser.ast.DataKey;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.MethodReferenceExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.resolution.UnsolvedSymbolException;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedValueDeclaration;
import com.github.javaparser.resolution.types.ResolvedType;
import com.github.javaparser.symbolsolver.javaparsermodel.JavaParserFacade;

import me.selslack.codingame.tools.compiler.CompilationContext;

public class ReferenceSolvingPass implements CompilerPass<CompilationContext, CompilationContext> {
    public static final DataKey<ResolvedValueDeclaration> VALUE_REF = new DataKey<>() { };
    public static final DataKey<ResolvedMethodDeclaration> METHOD_REF = new DataKey<>() { };
    public static final DataKey<ResolvedType> TYPE_REF = new DataKey<>() { };

    @Override
    public CompilationContext process(CompilationContext input) {
        JavaParserFacade facade = input.getTypeSolver().getFacade();

        for (TypeDeclaration<?> declaration : input.getTypes().valuesIterator().map(t -> t.getTypeDeclaration())) {
            for (NameExpr expr : declaration.findAll(NameExpr.class)) {
                var solved = facade.solve(expr);

                if (solved.isSolved()) {
                    expr.setData(VALUE_REF, solved.getCorrespondingDeclaration());
                }
            }

            for (FieldAccessExpr expr : declaration.findAll(FieldAccessExpr.class)) {
                try {
                    var solved = facade.solve(expr);

                    if (solved.isSolved()) {
                        expr.setData(VALUE_REF, solved.getCorrespondingDeclaration());
                    }
                }
                catch (UnsolvedSymbolException e) {
                    // This may happen in chained Field Access:
                    // java.lang.System.out.println("Test")
                }
            }

            for (MethodCallExpr expr : declaration.findAll(MethodCallExpr.class)) {
                var solved = facade.solve(expr);

                if (solved.isSolved()) {
                    expr.setData(METHOD_REF, solved.getCorrespondingDeclaration());
                }
            }

            for (MethodReferenceExpr expr : declaration.findAll(MethodReferenceExpr.class)) {
                var solved = facade.solve(expr);

                if (solved.isSolved()) {
                    expr.setData(METHOD_REF, solved.getCorrespondingDeclaration());
                }
            }

            for (com.github.javaparser.ast.type.Type expr : declaration.findAll(com.github.javaparser.ast.type.Type.class)) {
                expr.setData(TYPE_REF, facade.convertToUsage(expr));
            }
        }

        return input;
    }
}
