package me.selslack.codingame.tools.compiler

import spock.lang.*

class OptionsTest extends Specification {
    @Unroll("Correct parameters: #input")
    def "Correct parameters"() {
        given:
        def options = Options.from(input as String[])

        expect:
        options.sources.toJavaArray() == sources as File[]
        options.out == (out ? new File(out) : null)
        options.showHelp == help

        where:
        input                                                               | sources  | out         | help
        ["--player=me.selslack.Player", "--source=/tmp", "--out=/dev/null"] | ["/tmp"] | "/dev/null" | false
        ["--help"]                                                          | []       | null        | true
    }
}