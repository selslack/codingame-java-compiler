package me.selslack.codingame.tools.compiler;

public class CompilationFeatureException extends CompilationException {
    public CompilationFeatureException(String feature, Type type, int line) {
        super(String.format("Can't compile %s in %s at line %d", feature, type.getFullName(), line));
    }
}
