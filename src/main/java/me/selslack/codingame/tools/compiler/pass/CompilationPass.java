package me.selslack.codingame.tools.compiler.pass;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.resolution.types.ResolvedReferenceType;
import io.vavr.collection.List;
import io.vavr.collection.Stream;

import me.selslack.codingame.tools.compiler.CompilationContext;

import static me.selslack.codingame.tools.compiler.pass.ReferenceSolvingPass.METHOD_REF;
import static me.selslack.codingame.tools.compiler.pass.ReferenceSolvingPass.VALUE_REF;

public class CompilationPass implements CompilerPass<CompilationContext, CompilationUnit> {
    @Override
    public CompilationUnit process(CompilationContext input) {
        CompilationUnit result = new CompilationUnit();

        for (TypeDeclaration<?> node : dependencies(input.getPlayer().getTypeDeclaration(), input)) {
            result.addType(node);
        }

        return result;
    }

    static List<TypeDeclaration<?>> dependencies(TypeDeclaration<?> original, CompilationContext context) {
        TypeDeclaration<?> copy = original.clone();
        List<TypeDeclaration<?>> result = List.of(copy);

        for (Node node : Stream.ofAll(original.stream())) {
            if (node.containsData(VALUE_REF)) {
                var reference = node.getData(VALUE_REF);
                var refType = reference.getType();

                if (refType instanceof ResolvedReferenceType) {
                    var type = context.getTypes().get(
                        ((ResolvedReferenceType) refType).getQualifiedName()
                    );

                    if (type.isDefined()) {
                        result = result.appendAll(dependencies(type.get().getTypeDeclaration(), context));
                    }
                }
            }
            else if (node.containsData(METHOD_REF)) {
                var reference = node.getData(METHOD_REF);
                var type = context.getTypes().get(reference.declaringType().getQualifiedName());

                if (type.isDefined()) {
                    result = result.appendAll(dependencies(type.get().getTypeDeclaration(), context));
                }
            }
        }

        result.forEach(t -> t.removeModifier(
            Modifier.Keyword.PUBLIC,
            Modifier.Keyword.PROTECTED,
            Modifier.Keyword.PRIVATE
        ));

        return result;
    }
}
