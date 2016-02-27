package me.selslack.codingame.tools.compiler;

import com.github.javaparser.ast.Node;

public class CloneVisitor extends com.github.javaparser.ast.visitor.CloneVisitor {
    @Override
    protected <T extends Node> T cloneNodes(T _node, Object _arg) {
        T result = super.cloneNodes(_node, _arg);

        if (_node != null && result != null) {
            result.setData(_node.getData());
        }

        return result;
    }
}
