package me.selslack.codingame.tools.compiler

import com.github.javaparser.JavaParser
import com.github.javaparser.ast.CompilationUnit
import spock.lang.*

class CompilerTest extends Specification {
    @Unroll
    def "compile unsupported feature from #sources"() {
        given:
        def compiler = new Compiler(mapResourceToFile(sources), new FileWriter(out))

        when:
        compiler.compile()

        then:
        thrown(CompilationFeatureException)

        where:
        sources                                   | out
        ["projects/unsupported-static-import"]    | "/dev/null"
        ["projects/unsupported-asterisk-import"]  | "/dev/null"
        ["projects/unsupported-inner-class"]      | "/dev/null"
        ["projects/unsupported-local-class"]      | "/dev/null"
        ["projects/unsupported-equal-class-name"] | "/dev/null"
    }

    @Unroll
    def "compile from #sources"() {
        given:
        def output = new StringWriter(1024)
        def compiler = new Compiler(mapResourceToFile(sources), output)

        when:
        compiler.compile()

        then:
        assertCompilationUnitsEquals(
            sourceToUnit(mapResourceToFile(expected)),
            sourceToUnit(output.toString())
        )

        where:
        sources                            | expected
        ["projects/basic/src"]             | "projects/basic/output/Result.java"
        ["projects/basic-dep-solving/src"] | "projects/basic-dep-solving/output/Result.java"
        ["projects/dep-same-pkg/src"]      | "projects/dep-same-pkg/output/Result.java"
    }

    def mapResourceToFile(String resource) {
        URL result = getClass().classLoader.getResource(resource)

        if (!result) {
            throw new RuntimeException("Resource path '$resource' not found")
        }

        new File(result.file)
    }

    def mapResourceToFile(Collection<String> resource) {
        resource.collect { f -> mapResourceToFile(f) } as File[]
    }

    def sourceToUnit(String source) {
        JavaParser.parse(new StringReader(source), true)
    }

    def sourceToUnit(File source) {
        JavaParser.parse(source)
    }

    def unitToString(CompilationUnit unit) {
        def dumper = new DumpVisitor(false)

        unit.accept(dumper, null)

        return dumper.getSource()
    }

    def assertCompilationUnitsEquals(CompilationUnit expected, CompilationUnit result) {
        def expectedSource = unitToString(expected)
        def resultSource = unitToString(result)

        assert resultSource == expectedSource

        true
    }
}
