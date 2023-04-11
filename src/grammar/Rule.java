package grammar;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public record Rule(Symbol name, ArrayList<Symbol> symbols) {
    @Override
    public Symbol name() {
        return name;
    }

    @Override
    public ArrayList<Symbol> symbols() {
        return symbols;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Rule rule)) return false;
        return name().equals(rule.name()) && symbols.equals(rule.symbols);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name(), symbols);
    }

    @Override
    public String toString() {
        return "Rule{" +
                "name='" + name + '\'' +
                ", symbols=" + symbols +
                '}';
    }


}