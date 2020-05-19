package me.selslack.codingame.tools.compiler;

public class CompilationFeatureException extends CompilationException {
    public CompilationFeatureException(String feature, int line) {
        super(String.format("Can't compile %s in %s at line %d", feature, line));
    }
}
