grammar ConfigGrammar;

start : preamble regex* rule+ ;

preamble : CODE*;

rule : NAME type? in? EQ (symbIn)* CODE? END;

symbIn : symb (DASH CODE)?;

type : COL TERM;

in : DASH TERM;
symb : NAME | TERM | ID;

regex : ID (COL TERM)? EQ REG CODE? END;

REG : '<' .*? '>';
NAME : [a-z]+;

ID : [A-Z][a-zA-Z]*;

TERM : '\'' .*? '\'';
CODE : '#' .*? '#';

EQ : '=';
COL : ':';
DASH : '<-';
END : ';';

NEWLINE : '\r'?'\n' -> skip;
WS  : [ \t\n\r]+ -> skip ;
COMMENT : '--' .*? '--' -> skip;