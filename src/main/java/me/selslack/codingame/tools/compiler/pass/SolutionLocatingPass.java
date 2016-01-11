package me.selslack.codingame.tools.compiler.pass;

import com.github.javaparser.ast.body.ModifierSet;
import com.github.javaparser.ast.body.TypeDeclaration;
import me.selslack.codingame.tools.compiler.CompilationContext;
import me.selslack.codingame.tools.compiler.CompilationException;
import me.selslack.codingame.tools.compiler.Utils;

public class SolutionLocatingPass implements CompilerPass<CompilationContext, CompilationContext> {
    private final String solutionClass;

    public SolutionLocatingPass(String solutionClass) {
        this.solutionClass = solutionClass;
    }

    @Override
    public CompilationContext process(CompilationContext input) throws Exception {
        TypeDeclaration[] matches = input.getTypes().toStream()
            .filter(v -> ModifierSet.isPublic(v.getModifiers()) && !ModifierSet.isAbstract(v.getModifiers()))
            .filter(v -> Utils.getFullName(v).endsWith(solutionClass))
            .toJavaArray(TypeDeclaration.class);

        switch (matches.length) {
            case 0:
                throw new CompilationException(this, "Solution class matching '" + solutionClass + "' was not found");

            case 1:
                input.setSolutionClass(matches[0]);
                break;

            default:
                throw new CompilationException(this, "The definition of solution class '" + solutionClass + "' is too vague");
        }

        return input;
    }
}
