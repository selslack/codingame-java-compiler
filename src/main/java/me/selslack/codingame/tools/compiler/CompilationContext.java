package me.selslack.codingame.tools.compiler;

import io.vavr.collection.Map;
import io.vavr.collection.Set;
import io.vavr.control.Option;

public class CompilationContext {
    private final Type player;
    private final Map<String, Type> types;
    private final TypeSolver solver;

    public CompilationContext(final Type player,
                              final Set<Type> types) {
        this.player = player;
        this.types = types.toMap(t -> t.getTypeFqn(), t -> t);
        this.solver = new TypeSolver(this);
    }

    public Type getPlayer() {
        return player;
    }

    public Map<String, Type> getTypes() {
        return types;
    }

    public Option<Type> getType(String fqn) {
        return types.get(fqn);
    }

    public TypeSolver getTypeSolver() {
        return solver;
    }
}
