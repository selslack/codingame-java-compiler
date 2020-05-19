package me.selslack.codingame.tools.compiler;

import io.vavr.collection.List;
import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;

public class Options {
    static final private OptionParser parser = new OptionParser();

    static {
        parser.accepts("out", "Compilation output path").withRequiredArg().required().ofType(File.class);
        parser.accepts("source").withRequiredArg().required().ofType(File.class);
        parser.accepts("help", "Show this help message").forHelp();
    }

    public static Options from(String... args) throws OptionsParsingException {
        try {
            OptionSet  parsed = parser.parse(args);
            File       out    = (File) parsed.valueOf("out");

            @SuppressWarnings("unchecked")
            List<File> paths = List.ofAll((java.util.List<File>) parsed.valuesOf("source"));

            return new Options(paths, out, parsed.has("help"));
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
    final public File out;
    final public boolean showHelp;

    private Options(List<File> sources, File out, boolean showHelp) {
        this.sources = sources;
        this.out = out;
        this.showHelp = showHelp;
    }
}
