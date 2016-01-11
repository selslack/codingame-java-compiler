package me.selslack.codingame.tools.compiler;

import com.github.javaparser.ast.body.TypeDeclaration;
import javaslang.collection.HashSet;
import javaslang.collection.Set;

import java.util.List;
import java.util.Optional;

public class CompilationContext {
    private Optional<TypeDeclaration> solutionClass = Optional.empty();

    final private Set<TypeDeclaration> types;

    public CompilationContext(List<TypeDeclaration> types) {
        this.types = HashSet.ofAll(types);
    }

    public Optional<TypeDeclaration> getSolutionClass() {
        return solutionClass;
    }

    public void setSolutionClass(TypeDeclaration solutionClass) {
        this.solutionClass = Optional.of(solutionClass);
    }

    public Set<TypeDeclaration> getTypes() {
        return types;
    }
}
