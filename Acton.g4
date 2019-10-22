grammar Acton;
@members{
    void print(String str){
        System.out.println(str);
    }
}

acton: (actorDeclaration);//+ (main);

actorDeclaration:
    ACTOR ID (inherite)? LPAR INTEGER_LITERAL RPAR LEFT_CURLY_BRACKET (actorBody) RIGHT_CURLY_BRACKET;

inherite:
    EXTENDS ID;

actorBody:
    (knownActors) (actorVars) (initializer)? (messageHandler)*;

initializer:
    MSG_HANDLER 'initial' LPAR (argDeclrations)? RPAR  handlerBlock;
knownActors:
     KNOWN_ACTOR LEFT_CURLY_BRACKET (knownActorDeclarationStatment)* RIGHT_CURLY_BRACKET;

knownActorDeclarationStatment: ID ID END_OF_STATEMENT;
actorVars: ACTOR_VARIABLE LEFT_CURLY_BRACKET (varDeclaration)* RIGHT_CURLY_BRACKET;
varDeclaration: ((type ID) | (intArrayDeclration)) END_OF_STATEMENT;
type: (INT | BOOL | STR);
intArrayDeclration: INT ID LEFT_SQUARE_BRACKET INTEGER_LITERAL RIGHT_SQUARE_BRACKET;

messageHandler: MSG_HANDLER ID LPAR (argDeclrations)? RPAR  handlerBlock;
argDeclrations: (varDeclaration) (',' (type ID) | (intArrayDeclration))*;
handlerBlock: LEFT_CURLY_BRACKET(varDeclaration)*(bss)? RIGHT_CURLY_BRACKET;
//BSS: Bare Scope & Statement (Bare both means without curly brace and without var declration)
bss:
    (innerScope) (bss) | (statement) (bss) | (innerScope) | (statement) |;
innerScope: LEFT_CURLY_BRACKET (bss) RIGHT_CURLY_BRACKET;
statement: ';';
expressionList: expression (',' expression)*;

methodCall:
    ID '.' LPAR expressionList? RPAR |
    THIS '.' LPAR expressionList? RPAR |
    SENDER '.' LPAR expressionList? RPAR;

//varAccessor is used for both lhs and rhs
varAccessor:
    ('self' '.')? ID;

assignment:
    (varAccessor) '=' (expression);


expression
	:	conditionalExpression
	|	assignment
	;

conditionalExpression
    :	(conditionalOrExpression)
    |	(conditionalOrExpression '?' expression ':' (conditionalExpression))
    ;

conditionalOrExpression
	:	conditionalAndExpression
	|	conditionalAndExpression '||' conditionalOrExpression
	;

conditionalAndExpression
	:	equalityExpression
	|	conditionalAndExpression '&&' equalityExpression
	;
equalityExpression
	:	relationalExpression
	|	relationalExpression '==' equalityExpression
	|	 relationalExpression '!=' equalityExpression
	;

relationalExpression
	:	additiveExpression
	|	additiveExpression '<' relationalExpression
	|	additiveExpression '>' relationalExpression
	;
additiveExpression
	:	multiplicativeExpression
	|	multiplicativeExpression '+' additiveExpression
	|	multiplicativeExpression '-' additiveExpression
	;

multiplicativeExpression
	:	unaryExpression
	|	unaryExpression '*' multiplicativeExpression
	|	unaryExpression '/' multiplicativeExpression
	|	unaryExpression '%' multiplicativeExpression
	;

unaryExpression
	:	'--' unaryExpression
	|	'++' unaryExpression
	|	'+' unaryExpression
	|	'-' unaryExpression
    |	'!' unaryExpression
    |		postfixExpression
	;


postfixExpression
	:	(squared)('++'|'--')*
    ;
squared: parantesed'['expression']' | parantesed;
parantesed: '('expression')' | atomic;
atomic: varAccessor | BOOL_LITERAL | INTEGER_LITERAL | STRING_LITERAL;

// Parser
ACTOR: 'actor';
THIS: 'this';
SENDER: 'sender';
KNOWN_ACTOR: 'knownactors';
ACTOR_VARIABLE: 'actorvars';
EXTENDS: 'extends';

LPAR: '(';
RPAR: ')';
LEFT_CURLY_BRACKET: '{';
RIGHT_CURLY_BRACKET: '}';
LEFT_SQUARE_BRACKET: '[';
RIGHT_SQUARE_BRACKET: ']';
END_OF_STATEMENT: ';';
INT: 'int';
STR: 'string';
MSG_HANDLER: 'msghandler';
INIT: 'initial';
BOOL: 'boolean';

BOOL_LITERAL: 'true'|'false';
STRING_LITERAL: '"' ~('"')* '"';
INTEGER_LITERAL: [0-9]+;
WS: [\t\r\n ]+ -> skip;
LINE_COMMENT: '//' ~[\r\n]* -> skip;


ID: [a-zA-Z_][a-zA-Z_0-9]*;