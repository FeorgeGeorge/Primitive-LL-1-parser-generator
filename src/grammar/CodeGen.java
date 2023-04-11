package grammar;

import parsing.MyLexer;
import gen.ConfigListener;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class CodeGen {
    public static String DEF = "Default";
    private static String parseNonterm(Symbol name) {
        return String.format("nonterm_%s", name.getText());
    }

    private static String parseByRule(Symbol name, int id) {
        return String.format("rule_%s_%d", name, id);
    }

    private static String parseByTerm(Symbol name) {
        return String.format("term_%s", name);
    }

    public ConfigListener config;
    public GrammarInfo grammar;
    public MyLexer lexer;
    /**
     * A mapping from nonterminals to their types
     **/

    private PrintWriter writer;

    public CodeGen(ConfigListener config) {
        this.config = config;

        grammar = new GrammarInfo(config.rules);
        lexer = new MyLexer(config.terminals);
    }

    private void preamble(String className, String returnType) {
        writer.println("package examples;");
        writer.println("import java.util.*;");
        writer.println("import grammar.*;");
        writer.println("import java.util.regex.Pattern;");

        if (config.preamble.size() >= 1) {
            writer.println(config.preamble.get(0));
        }

        writer.printf("public class %s extends Base<%s>  {\n", className, returnType);

        if (config.preamble.size() >= 2) {
            writer.println(config.preamble.get(1));
        }

        writer.printf("\t public %s(){}\n", className);
    }

    private void close() {
        writer.println("}");
    }


    private void writeTerm(Symbol terminal) {
        String type = config.types.getOrDefault(terminal.getText(), DEF);

        String takeType = config.takeIn.get(terminal.getText());
        String argf = takeType == null || takeType.isEmpty() ? "" : takeType + " inh";

        writer.printf("private %s %s(%s) throws Exception {\n", type, parseByTerm(terminal), argf);
        writer.printf("var arg = accept(\"%s\").contents;\n", terminal.getText());
        writer.println(config.terminalCode.get(terminal));
        close();
    }
    private void writeParseNonterm(Symbol nonterm) {
        String type = config.types.getOrDefault(nonterm.getText(), DEF);
        String takeType = config.takeIn.get(nonterm.getText());

        String argf = takeType == null || takeType.isEmpty() ? "" : takeType + " inh";

        writer.printf("public %s %s(%s) throws Exception {\n", type, parseNonterm(nonterm), argf);
        int count = 0;
        for (Rule rule : grammar.rulesByName.get(nonterm)) {
            writer.printf("switch (curSymb().getText()) {\n");
            for (Symbol x : grammar.firstOneOfSeq(nonterm, rule.symbols())) {
                if (!x.equals(Symbol.eps())) {
                    writer.printf("case \"%s\": ", x.getText());
                }
                String f = argf.isEmpty() ? "" : "inh";
                writer.printf("return %s(%s);\n", parseByRule(nonterm, count), f);
            }
            count += 1;
            close();
        }
        writer.printf("throw new Exception(\" An error occurred while parsing %s\");\n", nonterm);
        close();
    }

    private void writeParse(String returnType) {
        writer.println("@Override");
        writer.printf("public %s parse(String text) throws Exception {\n", returnType);

        writer.println("ArrayList<Symbol> s = new ArrayList<>();");

        for (Symbol s : grammar.getTerminals()) {
            String type = config.types.get(s.getText());
            if (s.pattern != null) {
                writer.printf("s.add(new Symbol(\"%s\", Pattern.compile(\"%s\")));\n", s.getText(), s.getPattern().pattern());
            } else {
                writer.printf("s.add(new Symbol(\"%s\", Pattern.compile(\"^\" + Pattern.quote(\"%s\"))));\n", s.getText(), s.getText());
            }
        }

        writer.printf("MyLexer lexer = new MyLexer(s);\n");
        writer.printf("tokenText = lexer.tokenize(text);\n");
        writer.println("tokenText.add(Symbol.end());");
        writer.printf("var ans = %s();\n", parseNonterm(grammar.getStart()));
        writer.println("eof();");
        writer.println("return ans;");
        close();
    }

    private void writeParseRuleCode(Rule rule, int id) {
        String ruleName =  rule.name().getText();
        String type = config.types.getOrDefault(ruleName, DEF);
        String takeType = config.takeIn.get(ruleName);
        String argf = takeType == null || takeType.isEmpty() ? "" : takeType + " inh";
        writer.printf("private %s %s(%s) throws Exception  {\n", type, parseByRule(rule.name(), id), argf);

        config.takeCode.putIfAbsent(rule, new HashMap<>());
        Map<String, String> takeArg = config.takeCode.get(rule);

        int count = 1;
        int termcount = 1;

        for (Symbol x : rule.symbols()) {
            String argp = takeArg.get(x.getText());
            String arg = argp == null ? "" : argp;
            if (x.isTerminal()) {
                if (config.types.get(x.getText()) != null) {
                    writer.printf("var term%d = %s(%s);", termcount, parseByTerm(x), arg);
                }
                else {
                    writer.printf("var term%d = accept(\"%s\");\n", termcount, x.getText());
                }
                termcount += 1;
            } else {
                writer.printf("var arg%d = %s(%s);\n", count, parseNonterm(x), arg);
                count +=1;
            }
        }
        if (type.equals(DEF)) {
            writer.println("return new Default();");
        } else {
            writer.println(config.code.get(rule));
        }
        close();
    }

    public void generateCode(String className) {
        String fileName = className + ".java";
        String file = Path.of("src", "parsing", fileName).toString();
        try (PrintWriter writer = new PrintWriter(file, StandardCharsets.UTF_8)) {
            this.writer = writer;
            String returnType = config.types.get(grammar.getStart().getText());

            preamble(className, returnType);

            writeParse(returnType);

            for (Symbol x : grammar.getNonterminals()) {
                writeParseNonterm(x);

                var list = grammar.rulesByName.get(x);
                for (int id = 0; id < list.size(); id++) {
                    writeParseRuleCode(list.get(id), id);
                }
            }

            for (Symbol x : grammar.getTerminals()) {
                if (config.types.get(x.getText())!= null) {
                writeTerm(x);
                }
            }

            close();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
}
