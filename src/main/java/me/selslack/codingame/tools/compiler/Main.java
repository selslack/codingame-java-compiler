package me.selslack.codingame.tools.compiler;

import me.selslack.codingame.tools.compiler.pass.*;

public class Main {
    static public void main(String[] args) {
        try {
            Options options = Options.from(args);

            CompilerPass
                .processing(new SourceFindingPass())
                .thenProcessing(new SourceParsingPass())
                .thenProcessing(new TypeExtractingPass())
                .thenProcessing(new ContextCreatingPass())
                .thenProcessing(new SolutionLocatingPass(options.playerClass))
                .thenProcessing(new ResultExportingPass(options.out))
                .process(options.sources);
        }
        catch (OptionsParsingException e) {
            Throwable cause = e.getCause();

            if (cause == null) {
                System.err.println("Failed to parse some options:");
                System.err.println(Options.help());
            }
            else {
                System.err.println("Failed to parse given options:");
                System.err.println(cause.getMessage());
            }

            System.exit(1);
        }
        catch (Exception e) {
            e.printStackTrace(System.err);

            System.exit(42);
        }
    }
}
