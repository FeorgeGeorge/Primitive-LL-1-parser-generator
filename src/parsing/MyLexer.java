package parsing;

import grammar.Symbol;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class MyLexer {
    private final List<Symbol> patterns;
    StringBuilder input = new StringBuilder();

    // used for testing
    public MyLexer(List<Symbol> patterns) {
        this.patterns = patterns.stream().distinct().collect(Collectors.toList());

    }

    static int endOfMatch(Pattern pattern, String s) {
        Matcher m = pattern.matcher(s);
        if (m.find()) {
            return m.end();
        }
        return -1;
    }

    private Symbol findNextToken() {
        for (int i = 0; i < patterns.size(); i++) {
            Pattern p = patterns.get(i).getPattern();
            int end = endOfMatch(p, input.toString());
            if (end != -1) {
                String answer = input.substring(0, end);
                input.delete(0, end);
                return new Symbol(patterns.get(i).getText(), answer, p);
            }
        }
        return null;
    }

    public List<Symbol> tokenize(String inputString) throws Exception {
        this.input = new StringBuilder(inputString);

        List<Symbol> tokens = new ArrayList<>();

        boolean changes = true;
        while (true) {
            // no empty pattern should be allowed here.
            // todo : add sanity checks
            Symbol opt = findNextToken();
            if (opt != null) {
                tokens.add(opt);
            } else {
                break;
            }
        }

        if (!this.input.isEmpty()) {
            throw new Exception("Lexer could not tokenize the rest of the input: " + input.toString());
        }

        return tokens;
    }
}
