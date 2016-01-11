package me.selslack.codingame.tools.compiler;

import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;

import java.util.Collections;
import java.util.List;

interface Dependency {
    default List<ImportDeclaration> getImports() {
        return Collections.emptyList();
    }

    default List<TypeDeclaration> getTypes() {
        return Collections.emptyList();
    }
}
