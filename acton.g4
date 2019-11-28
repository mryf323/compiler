grammar acton;

@header {
package parsers;
import main.ast.node.*;
import main.ast.*;
import main.ast.node.expression.*;
import main.ast.node.expression.values.*;
import main.ast.node.statement.*;
import main.ast.node.declaration.*;
import main.ast.type.*;
import main.ast.type.arrayType.*;
import main.ast.type.primitiveType.*;
import main.ast.node.expression.operators.BinaryOperator;
import main.ast.node.expression.operators.UnaryOperator;
import main.ast.node.expression.Identifier;
}

program returns [Program p]
    : {$p = new Program();} (dec = actorDeclaration { $p.addActor($dec.ast); })+ main = mainDeclaration {$p.setMain($main.ast);}
    ;

actorDeclaration returns [ActorDeclaration ast]
     :
        ACTOR name = identifier (EXTENDS parent = identifier)? LPAREN queue = INTVAL RPAREN
        {
            $ast = new ActorDeclaration(new Identifier($name.text));
            $ast.setParentName(new Identifier($parent.text));
            $ast.setQueueSize($queue.int);
        }
        LBRACE

        (KNOWNACTORS
        LBRACE
            (
                actorType = identifier actorName = identifier SEMICOLON
                {
                    $ast.addKnownActor(new VarDeclaration(new Identifier(actorName), new ActorType(new Identifier(actorType))));
                }
            )*
        RBRACE)

        (ACTORVARS
        LBRACE
            varDeclarations {$ast.setActorVars($varDeclarations.ast);}
        RBRACE)

        (initHandlerDeclaration)?
        (msgHandlerDeclaration)*

        RBRACE
    ;

mainDeclaration returns [Main ast]
    :   MAIN
    	LBRACE
        actorInstantiation*
    	RBRACE
    ;

actorInstantiation
    :	identifier identifier
     	LPAREN (identifier(COMMA identifier)* | ) RPAREN
     	COLON LPAREN expressionList RPAREN SEMICOLON
    ;

initHandlerDeclaration
    :	MSGHANDLER INITIAL LPAREN argDeclarations RPAREN
     	LBRACE
     	varDeclarations
     	(statement)*
     	RBRACE
    ;

msgHandlerDeclaration
    :	MSGHANDLER identifier LPAREN argDeclarations RPAREN
       	LBRACE
       	varDeclarations
       	(statement)*
       	RBRACE
    ;

argDeclarations
    :	varDeclaration(COMMA varDeclaration)* |
    ;

varDeclarations returns [List<VarDeclaration> ast]
    : {$ast = new ArrayList<>();}
    (varDeclaration SEMICOLON { $ast.add($varDeclaration.ast);})*
    ;

varDeclaration returns [VarDeclaration ast]
    :	INT identifier
    |   STRING identifier
    |   BOOLEAN identifier
    |   INT identifier LBRACKET INTVAL RBRACKET
    ;

statement
    :	blockStmt
    | 	printStmt
    |  	assignStmt
    |  	forStmt
    |  	ifStmt
    |  	continueStmt
    |  	breakStmt
    |  	msgHandlerCall
    ;

blockStmt
    : 	LBRACE (statement)* RBRACE
    ;

printStmt
    : 	PRINT LPAREN expression RPAREN SEMICOLON
    ;

assignStmt
    :    assignment SEMICOLON
    ;

assignment
    :   orExpression ASSIGN expression
    ;

forStmt
    : 	FOR LPAREN (assignment)? SEMICOLON (expression)? SEMICOLON (assignment)? RPAREN statement
    ;

ifStmt
    :   IF LPAREN expression RPAREN statement elseStmt
    ;

elseStmt
    : ELSE statement |
    ;

continueStmt
    : 	CONTINUE SEMICOLON
    ;

breakStmt
    : 	BREAK SEMICOLON
    ;

msgHandlerCall
    :   (identifier | SELF | SENDER) DOT
        identifier LPAREN expressionList RPAREN SEMICOLON
    ;

expression
    :	orExpression (ASSIGN expression)?
    ;

orExpression
    :	andExpression (OR andExpression)*
    ;

andExpression
    :	equalityExpression (AND equalityExpression)*
    ;

equalityExpression
    :	relationalExpression ( (EQ | NEQ) relationalExpression)*
    ;

relationalExpression
    : additiveExpression ((LT | GT) additiveExpression)*
    ;

additiveExpression
    : multiplicativeExpression ((PLUS | MINUS) multiplicativeExpression)*
    ;

multiplicativeExpression
    : preUnaryExpression ((MULT | DIV | PERCENT) preUnaryExpression)*
    ;

preUnaryExpression
    :   NOT preUnaryExpression
    |   MINUS preUnaryExpression
    |   PLUSPLUS preUnaryExpression
    |   MINUSMINUS preUnaryExpression
    |   postUnaryExpression
    ;

postUnaryExpression
    :   otherExpression (postUnaryOp)?
    ;

postUnaryOp
    :	PLUSPLUS | MINUSMINUS
    ;

otherExpression
    :    LPAREN expression RPAREN
    |    identifier
    |    arrayCall
    |    actorVarAccess
    |    value
    |    SENDER
    ;

arrayCall
    :   (identifier | actorVarAccess) LBRACKET expression RBRACKET
    ;

actorVarAccess
    :   SELF DOT identifier
    ;

expressionList
    :	(expression(COMMA expression)* | )
    ;

identifier
    :   IDENTIFIER
    ;

value
    :   INTVAL | STRINGVAL | TRUE | FALSE
    ;

// values
INTVAL
    : [1-9][0-9]* | [0]
    ;

STRINGVAL
    : '"'~["]*'"'
    ;

TRUE
    :   'true'
    ;

FALSE
    :   'false'
    ;

//types
INT
    : 'int'
    ;

BOOLEAN
    : 'boolean'
    ;

STRING
    : 'string'
    ;

//keywords
ACTOR
	:	'actor'
	;

EXTENDS
	:	'extends'
	;

ACTORVARS
	:	'actorvars'
	;

KNOWNACTORS
	:	'knownactors'
	;

INITIAL
    :   'initial'
    ;

MSGHANDLER
	: 	'msghandler'
	;

SENDER
    :   'sender'
    ;

SELF
    :   'self'
    ;

MAIN
	:	'main'
	;

FOR
    :   'for'
    ;

CONTINUE
    :   'continue'
    ;

BREAK
    :   'break'
    ;

IF
    :   'if'
    ;

ELSE
    :   'else'
    ;

PRINT
    :   'print'
    ;

//symbols
LPAREN
    :   '('
    ;

RPAREN
    :   ')'
    ;

LBRACE
    :   '{'
    ;

RBRACE
    :   '}'
    ;

LBRACKET
    :   '['
    ;

RBRACKET
    :   ']'
    ;

COLON
    :   ':'
    ;

SEMICOLON
    :   ';'
    ;

COMMA
    :   ','
    ;

DOT
    :   '.'
    ;

//operators
ASSIGN
    :   '='
    ;

EQ
    :   '=='
    ;

NEQ
    :   '!='
    ;

GT
    :   '>'
    ;

LT
    :   '<'
    ;

PLUSPLUS
    :   '++'
    ;

MINUSMINUS
    :   '--'
    ;

PLUS
    :   '+'
    ;

MINUS
    :   '-'
    ;

MULT
    :   '*'
    ;

DIV
    :   '/'
    ;

PERCENT
    :   '%'
    ;

NOT
    :   '!'
    ;

AND
    :   '&&'
    ;

OR
    :   '||'
    ;

QUES
    :   '?'
    ;

IDENTIFIER
    :   [a-zA-Z_][a-zA-Z0-9_]*
    ;

COMMENT
    :   '//' ~[\n\r]* -> skip
    ;

WHITESPACE
    :   [ \t\r\n] -> skip
    ;