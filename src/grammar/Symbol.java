package grammar;

import java.util.Objects;
import java.util.regex.Pattern;

public class Symbol implements Comparable<Symbol> {
    private final String text; // name of the symbol

    public String contents; // a field to store result of lexer
    public Pattern pattern; // pattern with which it's parsed

    private final boolean isTerminal;

    public Symbol(String text, boolean isTerminal) {
        this.text = text;
        this.isTerminal = isTerminal;
    }

    public Symbol(String text, String contents, Pattern pattern) {
        this.text = text;
        this.contents = contents;
        this.pattern = pattern;
        this.isTerminal = true;
    }

    public Symbol(String text, Pattern pattern) {
        this.text = text;
        this.isTerminal = true;
        this.pattern = pattern;
    }

    public static Symbol nonterminal(String name) {
        return new Symbol(name, false);
    }

    public static Symbol terminal(String name) {
        return new Symbol(name, true);
    }

    public boolean isTerminal() {
        return isTerminal;
    }

    @Override
    public String toString() {
        return this.text;
    }

    public String getText() {
        return text;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public static Symbol end() {
        return terminal("$");
    }

    public static Symbol eps() {return terminal(""); }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Symbol symbol)) return false;
        return isTerminal() == symbol.isTerminal() && getText().equals(symbol.getText());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getText(), isTerminal());
    }


    @Override
    public int compareTo(Symbol o) {
        return this.text.compareTo(o.text);
    }
}
