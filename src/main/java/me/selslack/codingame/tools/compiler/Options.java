package me.selslack.codingame.tools.compiler;

import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Collections;
import java.util.List;

public class Options {
    static final private OptionParser parser = new OptionParser();

    static {
        parser.accepts("player", "Player FQCN").withRequiredArg().required();
        parser.accepts("out", "Compilation output path").withRequiredArg().required().ofType(File.class);
        parser.accepts("source").withRequiredArg().required().ofType(File.class);
        parser.accepts("help", "Show this help message").forHelp();
    }

    public static Options from(String... args) throws OptionsParsingException {
        try {
            OptionSet parsed = parser.parse(args);

            String     fqcn  = (String) parsed.valueOf("player");
            File       out   = (File) parsed.valueOf("out");

            @SuppressWarnings("unchecked")
            List<File> paths = (List<File>) parsed.valuesOf("source");

            return new Options(paths, fqcn, out, parsed.has("help"));
        }
        catch (OptionException e) {
            throw new OptionsParsingException(e);
        }
    }

    public static String help() {
        StringWriter writer = new StringWriter();

        try {
            parser.printHelpOn(writer);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }

        return writer.getBuffer().toString();
    }

    final public List<File> sources;
    final public String playerClass;
    final public File out;
    final public boolean showHelp;

    private Options(List<File> sources, String playerClass, File out, boolean showHelp) {
        this.sources = Collections.unmodifiableList(sources);
        this.playerClass = playerClass;
        this.out = out;
        this.showHelp = showHelp;
    }
}
