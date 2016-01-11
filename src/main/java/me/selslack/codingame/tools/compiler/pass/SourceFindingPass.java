package me.selslack.codingame.tools.compiler.pass;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SourceFindingPass implements CompilerPass<List<File>, List<File>> {
    @Override
    public List<File> process(List<File> input) throws Exception {
        Set<File> result = new HashSet<>();

        for (File file : input) {
            if (file.isDirectory()) {
                result.addAll(FileUtils.listFiles(file, new String[] { "java" }, true));
            }
            else {
                result.add(file);
            }
        }

        return result.stream().sorted().collect(Collectors.toList());
    }
}
