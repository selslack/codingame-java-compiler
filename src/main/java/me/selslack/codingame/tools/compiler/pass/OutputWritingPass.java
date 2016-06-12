package me.selslack.codingame.tools.compiler.pass;

import com.github.javaparser.ASTHelper;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ModifierSet;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import javaslang.Tuple2;
import javaslang.collection.HashSet;
import javaslang.collection.Set;
import javaslang.collection.Stack;
import me.selslack.codingame.tools.compiler.CompilationContext;
import me.selslack.codingame.tools.compiler.DumpVisitor;
import me.selslack.codingame.tools.compiler.Type;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Optional;

public class OutputWritingPass implements CompilerPass<CompilationContext, Void> {
    private final Writer writer;
    private final CompilationUnit unit;

    public OutputWritingPass(Writer writer) {
        this.writer = writer;
        this.unit = new CompilationUnit();
    }

    @Override
    public Void process(CompilationContext input) throws Exception {
        Stack<Type> queue = Stack.of(input.getSolution());
        Set<Type> visited = HashSet.empty();
        DumpVisitor dumper = new DumpVisitor(false);

        while (!queue.isEmpty()) {
            Tuple2<Type, ? extends Stack<Type>> result = queue.pop2();

            if (visited.contains(result._1)) {
                queue = result._2;
            }
            else {
                queue = result._2.appendAll(process(result._1, input));
            }

            visited = visited.add(result._1);
        }

        unit.accept(dumper, null);

        writer.write(dumper.getSource());
        writer.flush();

        return null;
    }

    private Stack<Type> process(Type type, CompilationContext context) throws IOException {
        TypeDeclaration declaration = type.getBody();
        TypeVisitor visitor = new TypeVisitor();
        Stack<Type> dependencies = Stack.empty();

        declaration.accept(visitor, null);
        declaration.setModifiers(
            ModifierSet.removeModifier(declaration.getModifiers(), ModifierSet.PUBLIC)
        );

        for (String dependency : visitor.getTypes()) {
            if (dependency.startsWith("java.")) {
                List<ImportDeclaration> imports = unit.getImports();
                ImportDeclaration imp = new ImportDeclaration(ASTHelper.createNameExpr(dependency), false, false);

                if (dependency.startsWith("java.lang.")) {
                    // java.lang. package is imported anyway
                }
                else if (imports.contains(imp)) {
                    // Avoid import duplicating
                }
                else {
                    unit.getImports().add(new ImportDeclaration(ASTHelper.createNameExpr(dependency), false, false));
                }
            }
            else {
                Optional<Type> depType = context.getByFqcn(dependency);

                if (depType.isPresent()) {
                    dependencies = dependencies.append(depType.get());
                }
            }
        }

        unit.getTypes().add(declaration);

        return dependencies;
    }

    private static class TypeVisitor extends VoidVisitorAdapter<Void> {
        private Set<String> types = HashSet.empty();

        public Set<String> getTypes() {
            return types;
        }

        @Override
        public void visit(ClassOrInterfaceDeclaration n, Void arg) {
            if (n.getParentNode() == null) {
                super.visit(n, arg);
            }
        }

        @Override
        public void visit(ClassOrInterfaceType n, Void arg) {
            if (n.getData() instanceof String) {
                types = types.add((String) n.getData());
            }

            super.visit(n, arg);
        }

        @Override
        public void visit(NameExpr n, Void arg) {
            if (n.getData() instanceof String) {
                types = types.add((String) n.getData());
            }

            super.visit(n, arg);
        }
    }
}
