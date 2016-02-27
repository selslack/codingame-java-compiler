package me.selslack.codingame.tools.compiler.dependency;

import javaslang.collection.List;

import java.util.Optional;

public class CompositeSolver implements Solver {
    private List<Solver> solvers;

    public CompositeSolver(Solver... solvers) {
        this.solvers = List.of(solvers);
    }

    @Override
    public Optional<String> solve(String type) {
        for (Solver solver : solvers) {
            Optional<String> result = solver.solve(type);

            if (result.isPresent()) {
                return result;
            }
        }

        return Optional.empty();
    }
}
