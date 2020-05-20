package me.selslack.codingame.tools.compiler.pass;

@FunctionalInterface
public interface CompilerPass<T, R> {
    R process(T input) throws Throwable;

    default <P> CompilerPass<T, P> thenProcessing(CompilerPass<R, P> pass) {
        return input -> pass.process(process(input));
    }

    static <T, R> CompilerPass<T, R> processing(CompilerPass<T, R> pass) {
        return pass;
    }
}
