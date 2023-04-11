import parsing.Demo;
import parsing.MyParser;
import parsing.ParseException;
import gen.ConfigGrammarLexer;
import gen.ConfigGrammarParser;
import gen.ConfigListener;
import grammar.CodeGen;
import grammar.GrammarInfo;
import grammar.Symbol;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.junit.Test;

import java.io.*;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.*;

public class Main {
    private static final String ARITH = "arith.example";
    private static final String SOURCE = "src";
    private static final String PSP = "psp.example";


    public static void main(String[] args) {
        if (args == null || args.length != 2) {
            System.out.println("Please, specify command line arguments (className and mode)");
            return;
        }
        String className = args[0];
        String mode = args[1];

        if (mode.equals("gen")) {
            try {
                CodeGen g = grammarFromFile(Path.of(SOURCE, className).toString());
                g.generateCode("Demo");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // should change type here for running repl
            MyParser<Object> parser = new Demo();

            try {
                InputStreamReader r = new InputStreamReader(System.in);
                BufferedReader br = new BufferedReader(r);

                String name = "";

                while (!name.equals("stop")) {
                    name = br.readLine();
                    try {
                        System.out.println(parser.parse(name.trim()));
                    } catch (ParseException e) {
                        System.err.println(e.getMessage());
                    }

                    parser.reset();
                }

                br.close();
                r.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public static CodeGen grammarFromFile(String file) throws IOException {
        InputStream fileInput = new FileInputStream(file);

        // create a CharStream that reads from standard input
        CharStream input = CharStreams.fromStream(fileInput);

        // create a lexer that feeds off of input CharStream
        ConfigGrammarLexer lexer = new ConfigGrammarLexer(input);

        // create a buffer of tokens pulled from the lexer
        CommonTokenStream tokens = new CommonTokenStream(lexer);

        // create a parser that feeds off the tokens buffer
        ConfigGrammarParser parser = new ConfigGrammarParser(tokens);
        ParseTree tree = parser.start(); // begin parsing at init rule

        // walk the parsed grammar tree to collect information.
        ParseTreeWalker walker = new ParseTreeWalker();
        ConfigListener listener = new ConfigListener();
        walker.walk(listener, tree);

        return new CodeGen(listener);
    }

    @Test
    public void testArithFirst() {
        try {
            GrammarInfo info = grammarFromFile(Path.of(SOURCE, ARITH).toString()).grammar;

            Symbol LP = Symbol.terminal("(");
            Symbol plus = Symbol.terminal("+");
            Symbol times = Symbol.terminal("*");
            Symbol var = Symbol.terminal("n");
            Symbol eps = Symbol.eps();

            Map<Symbol, Set<Symbol>> correctFirst = new HashMap<>();
            add(correctFirst, Symbol.nonterminal("e"), LP, var);
            add(correctFirst, Symbol.nonterminal("ep"), plus, eps);
            add(correctFirst, Symbol.nonterminal("t"), LP, var);
            add(correctFirst, Symbol.nonterminal("tp"), times, eps);
            add(correctFirst, Symbol.nonterminal("f"), LP, var);

            info.initFirst();

            assertEquals(correctFirst, info.getFirst());
        } catch (IOException e) {
            fail("File + " + ARITH + "must be available for this test");
        }
    }

    @Test
    public void testArithFollow() {
        try {
            GrammarInfo info = grammarFromFile(Path.of(SOURCE, ARITH).toString()).grammar;

            Symbol RP = Symbol.terminal(")");
            Symbol plus = Symbol.terminal("+");
            Symbol times = Symbol.terminal("*");

            Symbol end = Symbol.end();
            Map<Symbol, Set<Symbol>> correctFollow = new HashMap<>();
            add(correctFollow, Symbol.nonterminal("e"), RP, end);
            add(correctFollow, Symbol.nonterminal("ep"), RP, end);
            add(correctFollow, Symbol.nonterminal("t"), RP, plus, end);
            add(correctFollow, Symbol.nonterminal("tp"), plus, RP, end);
            add(correctFollow, Symbol.nonterminal("f"), times, plus, RP, end);
            assertEquals(correctFollow, info.getFollow());
        } catch (IOException e) {
            fail("File + " + ARITH + "must be available for this test");
        }
    }

    @Test
    public void arithShouldBeLLOne() {
        try {
            GrammarInfo info = grammarFromFile(Path.of(SOURCE, ARITH).toString()).grammar;
            assertTrue(info.isLLOne());
        } catch (IOException e) {
            fail("File + " + ARITH + "must be available for this test");
        }
    }

    @Test
    public void pspShouldBeLLOne() {
        try {
            GrammarInfo grammar = grammarFromFile(Path.of(SOURCE, PSP).toString()).grammar;
            assertTrue(grammar.isLLOne());
        } catch (IOException e) {
            System.err.println("A crucial file (" + PSP + ") is missing");
        }
    }

    private void add(Map<Symbol, Set<Symbol>> map, Symbol key, Symbol... symbols) {
        map.put(key, Stream.of(symbols).collect(Collectors.toSet()));
    }
}