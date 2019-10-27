grammar Acton;
@members{
    void print(String str){
        System.out.print(str);
    }

    void println(String str){
        System.out.println(str);
    }

    void printLine(){
        System.out.println("");
    }
}

acton
    :   (actorDeclaration)+ (main);

main
    : MAIN LCURLYBR actorInstantiation* RCURLYBR;

actorInstantiation
    :   {print("ActorInstantiation:");}
        actorType=ID{print($actorType.text);}
        actorName=ID {print(","+$actorName.text);}
        LPAR (idList)? RPAR
        {printLine();}
        COLLON LPAR(expressionList)? RPAR EOS;

idList
    :   knownactor1=ID {print(","+$knownactor1.text);} (COMMA knownactor2=ID {print(","+$knownactor2.text);})*;

actorDeclaration
    :   ACTOR name=ID (EXTENDS parent=ID)? LPAR INTEGER_LITERAL RPAR
        {
            print("ActorDec:"+$name.text);
            if($parent!=null)
                print(","+$parent.text);
            printLine();
        }
    LCURLYBR (actorBody) RCURLYBR;

actorBody
    :   (knownActors) (actorVars) (initializer)? (messageHandler)*;

initializer
    :    MSG_HANDLER INITIAL {println("MsgHandlerDec:initial");} LPAR (argDeclrations)? RPAR handlerBlock;

knownActors
    :     KNOWN_ACTOR LCURLYBR (knownActorDeclarationStatment)* RCURLYBR;

knownActorDeclarationStatment
    :   actorType=ID name=ID EOS {println("KnownActor:"+$actorType.text+","+$name.text);};

actorVars
    :   ACTOR_VARIABLE LCURLYBR (varDeclarationStatement)* RCURLYBR;

varDeclarationStatement
    :   varDeclaration EOS ;

varDeclaration returns [String name, String varType]
    :   (type {print("VarDec:"+$type.text);} ID {println(","+$ID.text);})
    |   (intArrayDeclration {println("VarDec:int[],"+$intArrayDeclration.name);});

type
    :   INT
    |   BOOL
    |   STR;

intArrayDeclration returns [String name, String size]
    :   INT ID  LSQRB  INTEGER_LITERAL  RSQRB {$name=$ID.text;$size=$INTEGER_LITERAL.text;};

messageHandler
    :   MSG_HANDLER name=ID {println("MsgHandlerDec:"+$name.text);} LPAR (argDeclrations)? RPAR  handlerBlock;

argDeclrations
    :   (varDeclaration) (COMMA varDeclaration)*;

handlerBlock
    :   LCURLYBR(varDeclarationStatement)*(blockStatements)? RCURLYBR;

block
	:	LCURLYBR blockStatements? RCURLYBR
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
	:	EOS
	;

expressionStatement
	:	statementExpression EOS
	;

statementExpression
	:	assignment
	|	preIncrementExpression
	|	preDecrementExpression
	|	postIncrementExpression
	|	postDecrementExpression
	;

ifThenStatement
	:	{println("Conditional:if");}
	    IF LPAR expression RPAR statementWithoutDeclration
	;

ifThenElseStatement
	:	{println("Conditional:if");}
	    IF LPAR expression RPAR statementNoShortIf {println("Conditional:else");}  ELSE statementWithoutDeclration
	;

ifThenElseStatementNoShortIf
	:	{println("Conditional:if");}
	    IF LPAR expression RPAR statementNoShortIf {println("Conditional:else");} ELSE statementNoShortIf
	;

forStatement
	:	basicForStatement
	;

forStatementNoShortIf
	:	basicForStatementNoShortIf
	;

basicForStatement
	:	{println("Loop:for");}
	    FOR LPAR assignment? EOS expression? EOS assignment? RPAR statementWithoutDeclration
	;

basicForStatementNoShortIf
	:	{println("Loop:for");}
	    FOR LPAR assignment? EOS expression? EOS assignment? RPAR statementNoShortIf
	;

breakStatement
	:	 BREAK  EOS
	;

continueStatement
	:	CONTINUE EOS
	;

expressionList: expression (COMMA expression)*;

methodCall
    :   builtInMethodCall
    |   {print("MsgHandlerCall:");} instance=ID {print($instance.text);} DOT name=ID {println(","+$name.text);} LPAR expressionList? RPAR EOS
    |   {print("MsgHandlerCall:self");} SELF DOT name=ID {println(","+$name.text);} LPAR expressionList? RPAR EOS
    |   {print("MsgHandlerCall:sender");}SENDER DOT name=ID {println(","+$name.text);} LPAR expressionList? RPAR EOS;

builtInMethodCall
    :   {println("Built-in:Print");} PRINT LPAR expression RPAR EOS;

//varAccessor is used for both lhs and rhs
varAccessor:
    (SELF DOT)? ID;

assignment:
    {println("Operator:=");} (squared) '=' (expression);


expression
	:	conditionalExpression
	|	assignment
	;

conditionalExpression
    :	(conditionalOrExpression)
    |	 {println("Operator:?:");} (conditionalOrExpression '?' expression COLLON (conditionalExpression))
    ;

conditionalOrExpression
	:	conditionalAndExpression
	|	{println("Operator:||");} conditionalAndExpression '||' conditionalOrExpression
	;

conditionalAndExpression
	:	equalityExpression
	|	{println("Operator:&&");} equalityExpression '&&' conditionalAndExpression
	;
equalityExpression
	:	relationalExpression
	|	{println("Operator:==");} relationalExpression '==' equalityExpression
	|	{println("Operator:!=");} relationalExpression '!=' equalityExpression
	;

relationalExpression
	:	additiveExpression
	|	{println("Operator:<");}additiveExpression '<' relationalExpression
	|	{println("Operator:>");}additiveExpression '>' relationalExpression
	;

additiveExpression
	:	multiplicativeExpression
	|	{println("Operator:+");}multiplicativeExpression '+' additiveExpression
	|	{println("Operator:-");}multiplicativeExpression '-' additiveExpression
	;

multiplicativeExpression
	:	unaryExpression
	|	{println("Operator:*");}unaryExpression '*' multiplicativeExpression
	|	{println("Operator:/");}unaryExpression '/' multiplicativeExpression
	|	{println("Operator:%");}unaryExpression '%' multiplicativeExpression
	;

unaryExpression
    :	preIncrementExpression
    |	preDecrementExpression
	|	{println("Operator:&&");} '-' unaryExpression
    |	{println("Operator:!"); } '!' unaryExpression
    |	postfixExpression
	;

preIncrementExpression
    :	{println("Operator:++");}'++' unaryExpression
    ;

preDecrementExpression
    :	{println("Operator:--");}'--' unaryExpression
    ;

postfixExpression
	:	(squared)('++' {println("Operator:++");})*
	|   (squared)('--' {println("Operator:--");})*
    ;

postIncrementExpression
    :	{println("Operator:++");} postfixExpression '++'
    ;

postDecrementExpression
    :	{println("Operator:--");} postfixExpression '--'
    ;

squared
    :   parantesed LSQRB expression RSQRB 
    |   parantesed;

parantesed
    :   LPAR expression RPAR
    |   atomic;

atomic
    :   varAccessor
    |   SELF
    |   SENDER
    |   BOOL_LITERAL
    |   INTEGER_LITERAL
    |   STRING_LITERAL
    ;

// Parser
ACTOR: 'actor';
SELF: 'self';
SENDER: 'sender';
KNOWN_ACTOR: 'knownactors';
ACTOR_VARIABLE: 'actorvars';
EXTENDS: 'extends';
MAIN: 'main';
IF: 'if';
ELSE: 'else';
FOR: 'for';
INT: 'int';
STR: 'string';
BREAK: 'break';
CONTINUE: 'continue';
LCURLYBR: '{';
RCURLYBR: '}';
LPAR: '(';
RPAR: ')';
LSQRB: '[';
RSQRB: ']';
DOT: '.';
COMMA: ',';
COLLON: ':';
EOS: ';';
MSG_HANDLER: 'msghandler';
INITIAL: 'initial';
BOOL: 'boolean';
BOOL_LITERAL
    :   'true'
    |   'false'
    ;
WS: [\t\r\n ]+ -> skip;
LINE_COMMENT: '//' ~[\r\n]* -> skip;
PRINT: 'print';
INTEGER_LITERAL: [0-9]+;
STRING_LITERAL: '"' ~('"')* '"';
ID: [a-zA-Z_][a-zA-Z_0-9]*;
