package me.selslack.codingame.tools.compiler;

import com.github.javaparser.ast.body.TypeDeclaration;

import java.util.Objects;

public class Type {
    private final TypeDeclaration<?> typeDeclaration;

    public Type(TypeDeclaration<?> typeDeclaration) {
        this.typeDeclaration = typeDeclaration;
    }

    public TypeDeclaration<?> getTypeDeclaration() {
        return typeDeclaration;
    }

    public String getTypeFqn() {
        return typeDeclaration.getFullyQualifiedName().orElseThrow();
    }

    public boolean isPlayerClass() {
        try {
            var clazz = typeDeclaration.asClassOrInterfaceDeclaration();

            return !clazz.isInterface()
                && !clazz.isInnerClass()
                && !clazz.isAbstract()
                && clazz.getNameAsString().equals("Player")
                && clazz.getMethodsBySignature("main", "String[]").size() > 0;
        }
        catch (IllegalStateException e) {
            return false;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Type type = (Type) o;

        return typeDeclaration.equals(type.typeDeclaration)
            && typeDeclaration.getFullyQualifiedName().equals(type.typeDeclaration.getFullyQualifiedName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(typeDeclaration);
    }
}
