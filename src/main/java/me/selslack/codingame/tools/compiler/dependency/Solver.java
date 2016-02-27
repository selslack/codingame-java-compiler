package me.selslack.codingame.tools.compiler.dependency;

import java.util.Optional;

public interface Solver {
    Optional<String> solve(String type);
}
