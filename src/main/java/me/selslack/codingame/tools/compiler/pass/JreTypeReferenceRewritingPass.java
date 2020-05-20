package me.selslack.codingame.tools.compiler.pass;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.resolution.types.ResolvedReferenceType;
import com.github.javaparser.symbolsolver.javaparsermodel.JavaParserFacade;

public class JreTypeReferenceRewritingPass extends AbstractRewritingPass {
    public JreTypeReferenceRewritingPass() {
        super(
            JreTypeReferenceRewritingPass::rewriteJreTypeReference
        );
    }

    static Node rewriteJreTypeReference(Node node, JavaParserFacade facade) {
        if (!(node instanceof ClassOrInterfaceType)) {
            return node;
        }

        var type = facade.convertToUsage((Type) node);

        if (!(type instanceof ResolvedReferenceType)) {
            return node;
        }

        var fqn = ((ResolvedReferenceType) type).getQualifiedName();
        var args = ((ClassOrInterfaceType) node).getTypeArguments();

        if (fqn.startsWith("java.") && !fqn.startsWith("java.lang.")) {
            return StaticJavaParser.parseClassOrInterfaceType(fqn)
                                   .setTypeArguments(args.orElse(null));
        }
        else {
            return node;
        }
    }
}
