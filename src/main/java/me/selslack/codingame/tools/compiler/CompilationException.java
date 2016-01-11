package me.selslack.codingame.tools.compiler;

import me.selslack.codingame.tools.compiler.pass.CompilerPass;

public class CompilationException extends Exception {
    public CompilationException(String message) {
        super(message);
    }

    public CompilationException(CompilerPass pass, String message) {
        super("[" + pass.getClass().getSimpleName() + "] " + message);
    }
}
