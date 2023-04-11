// Generated from java-escape by ANTLR 4.11.1
package gen;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link ConfigGrammarParser}.
 */
public interface ConfigGrammarListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link ConfigGrammarParser#start}.
	 * @param ctx the parse tree
	 */
	void enterStart(ConfigGrammarParser.StartContext ctx);
	/**
	 * Exit a parse tree produced by {@link ConfigGrammarParser#start}.
	 * @param ctx the parse tree
	 */
	void exitStart(ConfigGrammarParser.StartContext ctx);
	/**
	 * Enter a parse tree produced by {@link ConfigGrammarParser#preamble}.
	 * @param ctx the parse tree
	 */
	void enterPreamble(ConfigGrammarParser.PreambleContext ctx);
	/**
	 * Exit a parse tree produced by {@link ConfigGrammarParser#preamble}.
	 * @param ctx the parse tree
	 */
	void exitPreamble(ConfigGrammarParser.PreambleContext ctx);
	/**
	 * Enter a parse tree produced by {@link ConfigGrammarParser#rule}.
	 * @param ctx the parse tree
	 */
	void enterRule(ConfigGrammarParser.RuleContext ctx);
	/**
	 * Exit a parse tree produced by {@link ConfigGrammarParser#rule}.
	 * @param ctx the parse tree
	 */
	void exitRule(ConfigGrammarParser.RuleContext ctx);
	/**
	 * Enter a parse tree produced by {@link ConfigGrammarParser#symbIn}.
	 * @param ctx the parse tree
	 */
	void enterSymbIn(ConfigGrammarParser.SymbInContext ctx);
	/**
	 * Exit a parse tree produced by {@link ConfigGrammarParser#symbIn}.
	 * @param ctx the parse tree
	 */
	void exitSymbIn(ConfigGrammarParser.SymbInContext ctx);
	/**
	 * Enter a parse tree produced by {@link ConfigGrammarParser#type}.
	 * @param ctx the parse tree
	 */
	void enterType(ConfigGrammarParser.TypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link ConfigGrammarParser#type}.
	 * @param ctx the parse tree
	 */
	void exitType(ConfigGrammarParser.TypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link ConfigGrammarParser#in}.
	 * @param ctx the parse tree
	 */
	void enterIn(ConfigGrammarParser.InContext ctx);
	/**
	 * Exit a parse tree produced by {@link ConfigGrammarParser#in}.
	 * @param ctx the parse tree
	 */
	void exitIn(ConfigGrammarParser.InContext ctx);
	/**
	 * Enter a parse tree produced by {@link ConfigGrammarParser#symb}.
	 * @param ctx the parse tree
	 */
	void enterSymb(ConfigGrammarParser.SymbContext ctx);
	/**
	 * Exit a parse tree produced by {@link ConfigGrammarParser#symb}.
	 * @param ctx the parse tree
	 */
	void exitSymb(ConfigGrammarParser.SymbContext ctx);
	/**
	 * Enter a parse tree produced by {@link ConfigGrammarParser#regex}.
	 * @param ctx the parse tree
	 */
	void enterRegex(ConfigGrammarParser.RegexContext ctx);
	/**
	 * Exit a parse tree produced by {@link ConfigGrammarParser#regex}.
	 * @param ctx the parse tree
	 */
	void exitRegex(ConfigGrammarParser.RegexContext ctx);
}