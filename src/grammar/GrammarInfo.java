package grammar;

import java.util.*;

public class GrammarInfo {
    public List<Rule> rules;

    private final TreeSet<Symbol> terminals;

    private final TreeSet<Symbol> nonterminals;

    private static final Symbol EPS = Symbol.eps();
    private static final Symbol END = Symbol.end();

    /** FIRST maps nonterminals to sets of terminals **/
    private final Map<Symbol, Set<Symbol>> first = new HashMap<>();
    /** FOLLOW maps nonterminals to sets of terminals **/
    private final Map<Symbol, Set<Symbol>> follow = new HashMap<>();

    public final Map<Symbol, List<Rule>> rulesByName = new HashMap<>();

    public GrammarInfo(List<Rule> rules) {
        this.rules = rules;
        // by default, the first rule is the start rule
        terminals = new TreeSet<>();
        nonterminals = new TreeSet<>();

        for (Rule rule : rules) {
            var n = rule.name();
            nonterminals.add(n);

            rulesByName.putIfAbsent(n , new ArrayList<>());

            var toAdd =  rulesByName.get(n);
            toAdd.add(rule);

            for (Symbol symbol : rule.symbols()) {
                if (symbol.isTerminal()) {
                    terminals.add(symbol);
                } else {
                    nonterminals.add(symbol);
                }
            }
        }
        for (Symbol i : nonterminals) {
            first.put(i, new HashSet<>());
        }

        initFirst();
        initFollow();
    }

    public Symbol getStart() {
        return rules.get(0).name();
    }
    public NavigableSet<Symbol> getTerminals() {
        return terminals;
    }

    public NavigableSet<Symbol> getNonterminals() {
        return nonterminals;
    }

    public void initFirst() {
        boolean changes = true;

        while (changes) {
            changes = false;
            for (Rule rule : rules) {
                Symbol name = rule.name();

                Set<Symbol> defValue = firstOfSeq(rule.symbols());

                Set<Symbol> toAdd = first.get(name);
                int oldSize = toAdd.size();
                toAdd.addAll(defValue);

                if (oldSize != toAdd.size()) {
                    changes = true;
                }
            }
        }
    }

    public Set<Symbol> firstOfSeq (List<Symbol> l) {
        if (l.isEmpty()) {
            return new HashSet<> (List.of(EPS));
        }

        Symbol a = l.get(0);
        if (a.isTerminal()) {
            return new HashSet<> (List.of(a));
        }

        Set<Symbol> alpha = new HashSet<>(first.get(a));

        if (alpha.contains(EPS) && l.size() >= 1) {
            alpha.addAll(firstOfSeq(l.subList(1, l.size())));
        }
        return alpha;
    }

    private void initFollow() {
        for (Symbol i : nonterminals) {
            follow.put(i, new HashSet<>());
        }

        HashSet<Symbol> startSet = new HashSet<>();
        startSet.add(END);

        follow.put(getStart(), startSet);

        boolean changes = true;

        while (changes) {
            changes = false;
            for (Rule rule : rules) {
                var symbs = rule.symbols();
                for (int i = 0; i < symbs.size(); i++) {
                    var b = symbs.get(i);
                    if (!b.isTerminal()) {
                        Set<Symbol> defSet = firstOfSeq(symbs.subList(i + 1, symbs.size()));
                        if (defSet.contains(EPS)) {
                            defSet.addAll(follow.get(rule.name()));
                        }
                        defSet.remove(EPS);

                        var toAdd = follow.get(b);
                        int oldSize = toAdd.size();

                        toAdd.addAll(defSet);

                        if (oldSize != toAdd.size()) {
                            changes = true;
                        }
                    }
                }
            }
        }
    }

    public Set<Symbol> firstOneOfSeq(Symbol a, List<Symbol> seq) {
        Set<Symbol> firstOfSeq = new HashSet<>(firstOfSeq(seq));
        boolean contains = firstOfSeq.contains(Symbol.eps());
        firstOfSeq.remove(Symbol.eps());
        if (contains) {
            firstOfSeq.addAll(follow.get(a));
        }
        return firstOfSeq;
    }

    public Map<Symbol, Set<Symbol>> getFirst() {
        return first;
    }

    public Map<Symbol, Set<Symbol>> getFollow() {
        return follow;
    }

    public boolean isLLOne() {
        for (Symbol n : nonterminals) {
               for (Rule rule1 : rulesByName.get(n)) {
                   for (Rule rule2 : rulesByName.get(n)) {
                       if (rule1 != rule2) {
                           var alpha = firstOfSeq(rule1.symbols());
                           boolean hasEps = alpha.contains(Symbol.eps());

                           var beta = firstOfSeq(rule2.symbols());
                           alpha.retainAll(beta);
                           if (!alpha.isEmpty()) {
                               return false;
                           }

                           if (hasEps) {
                               var follow = getFollow().get(rule1.name());
                               follow.retainAll(beta);
                               if (!follow.isEmpty()) {
                                   return false;
                               }
                           }
                       }
                   }
               }
        }
        return true;
    }
}
