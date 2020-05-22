package me.selslack.codingame.tools.compiler.pass;

import io.vavr.collection.Set;

import me.selslack.codingame.tools.compiler.CompilationContext;
import me.selslack.codingame.tools.compiler.CompilationException;
import me.selslack.codingame.tools.compiler.Type;

public class ContextCreatingPass implements CompilerPass<Set<Type>, CompilationContext> {
    @Override
    public CompilationContext process(final Set<Type> input) throws Throwable {
        var players = input.filter(t -> t.isPlayerClass());

        if (players.isEmpty()) {
            throw new CompilationException("Can find the Player class");
        }

        if (players.size() > 1) {
            throw new CompilationException(
                "Multiple Player class candidates found: " + players.map(c -> c.getTypeFqn())
            );
        }

        return new CompilationContext(players.get(), input);
    }
}
