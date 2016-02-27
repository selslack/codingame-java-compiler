package me.selslack.codingame.tools.compiler;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;

public class CloneVisitor extends com.github.javaparser.ast.visitor.CloneVisitor {
    @Override
    public Node visit(MethodDeclaration _n, Object _arg) {
        MethodDeclaration result = (MethodDeclaration) super.visit(_n, _arg);

        if (_n.isDefault()) {
            result.setDefault(true);
        }

        return result;
    }

    @Override
    protected <T extends Node> T cloneNodes(T _node, Object _arg) {
        T result = super.cloneNodes(_node, _arg);

        if (_node != null && result != null) {
            result.setData(_node.getData());
        }

        return result;
    }
}
