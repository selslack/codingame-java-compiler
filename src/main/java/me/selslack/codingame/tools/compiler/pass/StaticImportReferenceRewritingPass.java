package me.selslack.codingame.tools.compiler.pass;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.symbolsolver.javaparsermodel.JavaParserFacade;

public class StaticImportReferenceRewritingPass extends AbstractRewritingPass {
    public StaticImportReferenceRewritingPass() {
        super(
            StaticImportReferenceRewritingPass::rewriteStaticFieldAccess_NameExpr,
            StaticImportReferenceRewritingPass::rewriteStaticMethodCall_MethodCallExpr
        );
    }

    static Node rewriteStaticFieldAccess_NameExpr(Node node, JavaParserFacade facade) {
        if (!(node instanceof NameExpr)) {
            return node;
        }

        var reference = facade.solve((NameExpr) node);

        if (!reference.isSolved() || !reference.getCorrespondingDeclaration().isField()) {
            return node;
        }

        if (!reference.getCorrespondingDeclaration().asField().isStatic()) {
            return node;
        }

        var fqn = String.format(
            "%s.%s",
            reference.getCorrespondingDeclaration().asField().declaringType().getQualifiedName(),
            ((NameExpr) node).getName()
        );

        // Rewrite only JRE imports until type renaming is implemented
        if (!fqn.startsWith("java.")) {
            return node;
        }

        if (fqn.startsWith("java.lang.")) {
            fqn = fqn.substring(10);
        }

        return StaticJavaParser.parseExpression(fqn);
    }

    static Node rewriteStaticMethodCall_MethodCallExpr(Node node, JavaParserFacade facade) {
        if (!(node instanceof MethodCallExpr)) {
            return node;
        }

        var reference = facade.solve((MethodCallExpr) node);

        if (!reference.isSolved() || !reference.getCorrespondingDeclaration().isStatic()) {
            return node;
        }

        var fqn = reference.getCorrespondingDeclaration().declaringType().getQualifiedName();

        // Rewrite only JRE imports until type renaming is implemented
        if (!fqn.startsWith("java.")) {
            return node;
        }

        if (fqn.startsWith("java.lang.")) {
            fqn = fqn.substring(10);
        }

        var replacement = ((MethodCallExpr) node).clone();

        replacement.setScope(
            StaticJavaParser.parseExpression(fqn)
        );

        return replacement;
    }
}
