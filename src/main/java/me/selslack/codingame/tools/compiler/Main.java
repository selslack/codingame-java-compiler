package me.selslack.codingame.tools.compiler;

public class Main {
    static public void main(String[] args) {
        try {
            new Compiler(Options.from(args)).compile();
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
