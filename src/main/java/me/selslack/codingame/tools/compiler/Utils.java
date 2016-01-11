package me.selslack.codingame.tools.compiler;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.TypeDeclaration;
import javaslang.collection.Stack;

public class Utils {
    static final public String NS_DELIMITER = ".";

    static public String getFullName(TypeDeclaration type) {
        Stack<String> names = Stack.of(type.getName());
        Node parent = type.getParentNode();

        while (parent != null) {
            if (parent instanceof TypeDeclaration) {
                names = names.push(((TypeDeclaration) parent).getName());
            }
            else if (parent instanceof CompilationUnit) {
                names = names.push(((CompilationUnit) parent).getPackage().getName().toString());
            }

            parent = parent.getParentNode();
        }

        return names.mkString(NS_DELIMITER);
    }
}
