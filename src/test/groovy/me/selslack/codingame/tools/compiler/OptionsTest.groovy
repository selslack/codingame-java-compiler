package me.selslack.codingame.tools.compiler

import spock.lang.*

class OptionsTest extends Specification {
    @Unroll("Correct parameters: #input")
    def "Correct parameters"() {
        given:
        def options = Options.from(input as String[])

        expect:
        options.playerClass == player
        options.sources.toJavaArray() == sources as File[]
        options.out == (out ? new File(out) : null)
        options.showHelp == help

        where:
        input                                                               | player               | sources  | out         | help
        ["--player=me.selslack.Player", "--source=/tmp", "--out=/dev/null"] | "me.selslack.Player" | ["/tmp"] | "/dev/null" | false
        ["--help"]                                                          | null                 | []       | null        | true
    }
}