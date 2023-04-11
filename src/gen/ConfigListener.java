package gen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import grammar.CodeGen;
import grammar.Rule;
import grammar.Symbol;

public class ConfigListener extends ConfigGrammarBaseListener {
    public List<String> preamble;
    public  List<Rule> rules = new ArrayList<>();
    public  Map<Rule, String> code = new HashMap<>();
    public Map<String, String> types = new HashMap<>(); // for correcting type

    public final Map<Symbol, String> terminalCode = new HashMap<>();
    public final Map<String, Symbol> byName = new HashMap<>();
    public final Map<String, String> takeIn = new HashMap<>();
    public final Map<Rule, Map<String, String>> takeCode = new HashMap<>();

    public final List<Symbol> terminals = new ArrayList<>();

    private static String cut(String text) {
        return text.substring(1, text.length()-1);
    }

    @Override
    public void exitRule(ConfigGrammarParser.RuleContext ctx) {
        ArrayList<Symbol> symbols = new ArrayList<>();

        var name = ctx.NAME().getText();
        var symbol = Symbol.nonterminal(name);

        if (ctx.type() != null) {
            types.put(name,  cut(ctx.type().TERM().getText()));
        }
        types.putIfAbsent(name, CodeGen.DEF);

        if (ctx.in() != null) {
            takeIn.put(name, cut(ctx.in().TERM().getText()));
        }

        for (ConfigGrammarParser.SymbInContext symbCtxIn : ctx.symbIn()) {
            var symbCtx = symbCtxIn.symb();

            if (symbCtx.TERM() != null) {
                String text = symbCtx.TERM().getText();
                var content = cut(text);
                Symbol terminal = Symbol.terminal(content);
                symbols.add(terminal);
                terminals.add(terminal);
            } else if (symbCtx.NAME() != null) {
                symbols.add(Symbol.nonterminal(symbCtx.NAME().getText()));
            } else if (symbCtx.ID() != null) {
                symbols.add(byName.get(symbCtx.ID().getText()));
            }
        }
        var rule = new Rule(symbol, symbols);

        for (ConfigGrammarParser.SymbInContext symbCtxIn : ctx.symbIn()) {
            if (symbCtxIn.CODE() != null) {
                takeCode.putIfAbsent(rule, new HashMap<>());
                Map<String, String> a = takeCode.get(rule); // should effectively mutate the map
                a.put(symbCtxIn.symb().getText(), cut(symbCtxIn.CODE().getText()));
            }
        }

        rules.add(rule);

        if (ctx.CODE() != null) {
            String text = cut(ctx.CODE().getText());
            code.put(rule, text);
        }
    }

    @Override
    public void exitRegex(ConfigGrammarParser.RegexContext ctx) {
        String text = ctx.ID().getText();

        String ftype =  ctx.TERM() != null ? cut(ctx.TERM().getText()) : null;

        types.put(text, ftype);

        String ftext = ctx.REG().getText();
        Pattern regex = Pattern.compile("^" + cut(ftext));

        Symbol symb = new Symbol(text, regex);

        byName.put(text, symb);
        terminals.add(symb);
        terminalCode.putIfAbsent(symb, cut(ctx.CODE().getText()));
    }

    @Override
    public void exitPreamble(ConfigGrammarParser.PreambleContext ctx) {
        this.preamble = ctx.CODE().stream().map(x -> cut(x.getText())).toList();
    }
}