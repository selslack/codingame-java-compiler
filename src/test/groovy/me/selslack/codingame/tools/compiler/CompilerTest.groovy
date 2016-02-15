package me.selslack.codingame.tools.compiler

import spock.lang.*

class CompilerTest extends Specification {
    @Unroll
    def "compile unsupported feature from #sources"() {
        given:
        def compiler = new Compiler(mapResourceToFile(sources), solutionClass, new FileWriter(out))

        when:
        compiler.compile()

        then:
        thrown(CompilationFeatureException)

        where:
        sources                                   | solutionClass | out
        ["projects/unsupported-static-import"]    | "Main"        | "/dev/null"
        ["projects/unsupported-asterisk-import"]  | "Main"        | "/dev/null"
        ["projects/unsupported-inner-class"]      | "Main"        | "/dev/null"
        ["projects/unsupported-local-class"]      | "Main"        | "/dev/null"
        ["projects/unsupported-equal-class-name"] | "Main"        | "/dev/null"
    }

    @Unroll
    def "compile from #sources"() {
        given:
        def compiler = new Compiler(mapResourceToFile(sources), solutionClass, new FileWriter(out))

        when:
        compiler.compile()

        then:
        notThrown(Exception)

        where:
        sources                | solutionClass | out
        ["projects/basic/src"] | ".Solution"   | "/dev/null"
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
}
