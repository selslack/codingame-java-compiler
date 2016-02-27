package me.selslack.codingame.tools.compiler.pass;

import com.github.javaparser.ast.TypeParameter;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.ReferenceType;
import com.github.javaparser.ast.type.WildcardType;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import javaslang.collection.Stack;
import me.selslack.codingame.tools.compiler.CompilationContext;
import me.selslack.codingame.tools.compiler.Type;
import me.selslack.codingame.tools.compiler.dependency.CompositeSolver;
import me.selslack.codingame.tools.compiler.dependency.JavaLangSolver;
import me.selslack.codingame.tools.compiler.dependency.SimpleImportSolver;
import me.selslack.codingame.tools.compiler.dependency.Solver;

import java.util.Optional;

public class DependencySolvingPass implements CompilerPass<CompilationContext, CompilationContext> {
    @Override
    public CompilationContext process(CompilationContext input) throws Exception {
        return input.map(t -> process(t));
    }

    private Type process(Type type) {
        Solver solver = new CompositeSolver(
            new SimpleImportSolver(type),
            new JavaLangSolver()
        );

        TypeSolverVisitor visitor = new TypeSolverVisitor();
        TypeDeclaration declaration = type.getBody();

        declaration.accept(visitor, solver);

        return new Type(type.getPackage(), type.getImports(), declaration, type.getUnitId());
    }

    private static class TypeSolverVisitor extends VoidVisitorAdapter<Solver> {
        private Stack<String> generics = Stack.empty();

        @Override
        public void visit(final ClassOrInterfaceDeclaration n, Solver solver) {
            if (n.getParentNode() != null) {
                return ;
            }

            for (TypeParameter generic : n.getTypeParameters()) {
                generics = generics.push(generic.toStringWithoutComments());
            }

            super.visit(n, solver);

            for (TypeParameter generic : n.getTypeParameters()) {
                generics = generics.pop();
            }
        }

        @Override
        public void visit(MethodDeclaration n, Solver solver) {
            for (TypeParameter generic : n.getTypeParameters()) {
                generics = generics.push(generic.toStringWithoutComments());
            }

            super.visit(n, solver);

            for (TypeParameter generic : n.getTypeParameters()) {
                generics = generics.pop();
            }
        }

        @Override
        public void visit(MethodCallExpr n, Solver solver) {
            super.visit(n, solver);

            if (n.getScope() instanceof NameExpr) {
                solve((NameExpr) n.getScope(), solver);
            }
        }

        @Override
        public void visit(FieldAccessExpr n, Solver solver) {
            super.visit(n, solver);

            if (n.getScope() instanceof NameExpr) {
                solve((NameExpr) n.getScope(), solver);
            }
        }

        @Override
        public void visit(ClassOrInterfaceType n, Solver solver) {
            ClassOrInterfaceType scope = n.getScope();
            String name = n.getName();
            String result;

            if (scope != null) {
                result = String.format("%s.%s", scope.toStringWithoutComments(), name);
            } else {
                result = name;
            }

            if (scope != null || !generics.contains(result)) {
                Optional<String> solved = solver.solve(result);

                if (solved.isPresent()) {
                    n.setData(solved.get());
                }
            }

            super.visit(n, solver);
        }

        private void solve(com.github.javaparser.ast.expr.NameExpr name, Solver solver) {
            Optional<String> solved = solver.solve(name.getName());

            if (solved.isPresent()) {
                name.setData(solved.get());
            }
        }
    }
}
