package parsing;

import grammar.Symbol;

import java.util.List;

public abstract class Base<Output> implements MyParser<Output>{
    List<Symbol> tokenText;
    private int pos;
    private void advance() {
        pos+= 1;
    }

    public void reset() {
        pos = 0;
    }

    public Symbol accept(String t) throws Exception {
        if (tokenText.get(pos).getText().equals(t)) {
            var ans = tokenText.get(pos);
            advance();
            return ans;
        }
        else {
            throw new Exception(String.format("Error: Expected string %s", t));
        }
    }
    public void eof() throws Exception {
        accept("$");
    }

    /** Checks if current position matches name t **/
    public boolean check(String t) {
        if (pos >= tokenText.size()) {
            return false;
        }
        return tokenText.get(pos).getText().equals(t);
    }

    public Symbol curSymb() {
        if (pos >= tokenText.size()) {
            return Symbol.end();
        }
        return tokenText.get(pos);
    }
    @Override
    public abstract Output parse(String text) throws ParseException;
}
