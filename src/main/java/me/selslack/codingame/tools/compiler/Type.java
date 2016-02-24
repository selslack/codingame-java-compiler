package me.selslack.codingame.tools.compiler;

import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.comments.Comment;
import javaslang.collection.List;

import java.util.Objects;
import java.util.Optional;

public class Type {
    private final Optional<PackageDeclaration> pkg;
    private final List<ImportDeclaration> imports;
    private final TypeDeclaration body;
    private final Optional<Type> parent;

    public Type(Optional<PackageDeclaration> pkg, List<ImportDeclaration> imports, TypeDeclaration body) {
        this.pkg = pkg;
        this.imports = imports;
        this.body = body;
        this.parent = Optional.empty();
    }

    public Type(Optional<PackageDeclaration> pkg, java.util.List<ImportDeclaration> imports, TypeDeclaration body) {
        this(pkg, List.ofAll(imports), body);
    }

    public Optional<PackageDeclaration> getPackage() {
        if (pkg.isPresent()) {
            return Optional.of((PackageDeclaration) pkg.get().clone());
        }
        else {
            return pkg;
        }
    }

    public List<ImportDeclaration> getImports() {
        return imports.map(i -> (ImportDeclaration) i.clone());
    }

    public TypeDeclaration getBody() {
        return (TypeDeclaration) body.clone();
    }

    public boolean isAbstract() {
        return ModifierSet.isAbstract(body.getModifiers());
    }

    public boolean isPublic() {
        return ModifierSet.isPublic(body.getModifiers());
    }

    public boolean isProtected() {
        return ModifierSet.isProtected(body.getModifiers());
    }

    public boolean isPrivate() {
        return ModifierSet.isPrivate(body.getModifiers());
    }

    public boolean hasPackageLevelAccess() {
        return ModifierSet.hasPackageLevelAccess(body.getModifiers());
    }

    public boolean isAnnotation() {
        return body instanceof AnnotationDeclaration;
    }

    public boolean isEnum() {
        return body instanceof EnumDeclaration;
    }

    public boolean isInterface() {
        return body instanceof ClassOrInterfaceDeclaration && ((ClassOrInterfaceDeclaration) body).isInterface();
    }

    public boolean isClass() {
        return !isAnnotation() && !isEnum() && !isInterface();
    }

    public boolean isSolution() {
        Comment comment = body.getComment();

        if (comment == null) {
            return false;
        }
        else {
            return isPublic() && !isAbstract() && comment.getContent().contains("@solution");
        }
    }

    public String getPackageName() {
        return pkg.map(v -> v.getName().toStringWithoutComments()).orElse("");
    }

    public String getName() {
        return body.getName();
    }

    public String getFullName() {
        List<String> result = List.empty();
        Node node = body;

        while (node != null) {
            if (node instanceof TypeDeclaration) {
                result = result.prepend(((TypeDeclaration) node).getName());
            }

            node = node.getParentNode();
        }

        if (pkg.isPresent()) {
            result = result.prepend(pkg.get().toStringWithoutComments());
        }

        return result.mkString(".");
    }

    public List<Type> getInnerTypes() {
        List<Type> result = List.empty();

        for (BodyDeclaration declaration : body.getMembers()) {
            if (declaration instanceof ClassOrInterfaceDeclaration || declaration instanceof EnumDeclaration) {
                result = result.prepend(new Type(pkg, imports, (TypeDeclaration) declaration));
            }
        }

        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (null == other || this.getClass() != other.getClass()) {
            return false;
        }

        Type type = (Type) other;

        return
            Objects.equals(this.pkg, type.pkg) &&
            Objects.equals(this.imports, type.imports) &&
            Objects.equals(this.body, type.body);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pkg, imports, body);
    }
}
