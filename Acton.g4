grammar Acton;
@members{
    void print(String str){
        System.out.print(str);
    }
    void printLine(){
        System.out.println("");
    }
}

acton: (actorDeclaration)+ (main);

main: actorInstantiation*;

actorInstantiation: ID ID '(' (idList)? ')'':''('(expressionList)?')'';';
idList: ID (',' ID)*;

actorDeclaration
    :   ACTOR name=ID (EXTENDS parent=ID)? '(' INTEGER_LITERAL ')'
    {
        print("ActorDec:"+$name.text);
        if($parent!=null)
            print(","+$parent.text);
        printLine();
    }
    '{' (actorBody) '}';

actorBody:
    (knownActors) (actorVars) (initializer)? (messageHandler)*;

initializer:
    MSG_HANDLER 'initial' '(' (argDeclrations)? ')'  handlerBlock;
knownActors:
     KNOWN_ACTOR '{' (knownActorDeclarationStatment)* '}';

knownActorDeclarationStatment: ID ID ';';
actorVars: ACTOR_VARIABLE '{' (varDeclarationStatement)* '}';
varDeclarationStatement: varDeclaration ';';
varDeclaration
    :   ((type name=ID) | (intArrayDeclration));
type: (INT | BOOL | STR);
intArrayDeclration: INT name=ID '[' INTEGER_LITERAL ']';

messageHandler: MSG_HANDLER name=ID '(' (argDeclrations)? ')'  handlerBlock;
argDeclrations: (varDeclaration) (',' varDeclaration)*;

handlerBlock: '{'(varDeclarationStatement)*(blockStatements)? '}';

block
	:	'{' blockStatements? '}'
	;

blockStatements
	:	statementWithoutDeclration+
	;

statementWithoutDeclration
	:	statementWithoutTrailingSubstatement
	|	ifThenStatement
	|	ifThenElseStatement
	|	forStatement
	;

statementNoShortIf
	:	statementWithoutTrailingSubstatement
	|	ifThenElseStatementNoShortIf
	|	forStatementNoShortIf
	;

statementWithoutTrailingSubstatement
	:	block
	|	emptyStatement
	|	expressionStatement
	|	breakStatement
	|	continueStatement
	|   methodCall
	;

emptyStatement
	:	';'
	;

expressionStatement
	:	statementExpression ';'
	;

statementExpression
	:	assignment
	|	preIncrementExpression
	|	preDecrementExpression
	|	postIncrementExpression
	|	postDecrementExpression
	;

ifThenStatement
	:	'if' '(' expression ')' statementWithoutDeclration
	;

ifThenElseStatement
	:	'if' '(' expression ')' statementNoShortIf 'else' statementWithoutDeclration
	;

ifThenElseStatementNoShortIf
	:	'if' '(' expression ')' statementNoShortIf 'else' statementNoShortIf
	;

forStatement
	:	basicForStatement
	;

forStatementNoShortIf
	:	basicForStatementNoShortIf
	;

basicForStatement
	:	'for' '(' assignment? ';' expression? ';' assignment? ')' statementWithoutDeclration
	;

basicForStatementNoShortIf
	:	'for' '(' assignment? ';' expression? ';' assignment? ')' statementNoShortIf
	;

breakStatement
	:	'break' ';'
	;

continueStatement
	:	'continue' ';'
	;

expressionList: expression (',' expression)*;

methodCall
    :   builtInMethodCall
    |   ID '.' '(' expressionList? ')'
    |   SELF '.' '(' expressionList? ')'
    |   SENDER '.' '(' expressionList? ')';

builtInMethodCall
    :   PRINT '(' expression ')' ';';

//varAccessor is used for both lhs and rhs
varAccessor:
    (SELF '.')? ID;

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
    :	preIncrementExpression
    |	preDecrementExpression
	|	'+' unaryExpression
	|	'-' unaryExpression
    |	'!' unaryExpression
    |		postfixExpression
	;

preIncrementExpression
    :	'++' unaryExpression
    ;

preDecrementExpression
    :	'--' unaryExpression
    ;

postfixExpression
	:	(squared)('++'|'--')*
    ;

postIncrementExpression
    :	postfixExpression '++'
    ;

postDecrementExpression
    :	postfixExpression '--'
    ;

squared: parantesed'['expression']' | parantesed;
parantesed: '('expression')' | atomic;
atomic: varAccessor | BOOL_LITERAL | INTEGER_LITERAL | STRING_LITERAL;

// Parser
ACTOR: 'actor';
SELF: 'self';
SENDER: 'sender';
KNOWN_ACTOR: 'knownactors';
ACTOR_VARIABLE: 'actorvars';
EXTENDS: 'extends';

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
PRINT: 'print';

ID: [a-zA-Z_][a-zA-Z_0-9]*;