package me.selslack.codingame.tools.compiler

import me.selslack.codingame.tools.compiler.pass.*
import spock.lang.*

@Stepwise
class CompilerTest extends Specification {
    @Shared result

    def setupSpec() {
        result = [
            new File(CompilerTest.getResource("simple/").toURI()),
            new File(CompilerTest.getResource("simple/Player.java").toURI()),
        ]
    }

    def "Source finding pass"() {
        given:
        def pass = new SourceFindingPass()

        when:
        //noinspection GroovyAssignabilityCheck
        result = pass.process(result)

        then:
        result.size() == 4
        result[0].toString().endsWith("/Player.java")
        result[1].toString().endsWith("/Utils.java")
        result[2].toString().endsWith("/pkg/Library.java")
        result[3].toString().endsWith("/unused/Source.java")
    }

    def "Source parsing pass"() {
        given:
        def pass = new SourceParsingPass()

        when:
        //noinspection GroovyAssignabilityCheck
        result = pass.process(result)

        then:
        result.size() == 4
    }

    def "Type extracting pass"() {
        given:
        def pass = new TypeExtractingPass()

        when:
        //noinspection GroovyAssignabilityCheck
        result = pass.process(result)

        then:
        result.size() == 1
    }

    def "Context creating pass"() {
        given:
        def pass = new ContextCreatingPass()

        when:
        //noinspection GroovyAssignabilityCheck
        result = pass.process(result)

        then:
        true
    }

    def "Solution locating pass"() {
        given:
        def pass = new SolutionLocatingPass(".Player")

        when:
        //noinspection GroovyAssignabilityCheck
        result = pass.process(result)

        then:
        result.solutionClass.isPresent()
        result.solutionClass.get().name == "Player"
    }
}