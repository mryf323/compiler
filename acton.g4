grammar acton;

@header {
package parsers;
import main.ast.node.declaration.handler.*;
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
import main.ast.type.actorType.*;
import java.util.*;
}

program returns [Program p]
    : {$p = new Program();} (dec = actorDeclaration { $p.addActor($dec.ast); })+ main = mainDeclaration {$p.setMain($main.ast);}
    ;

actorDeclaration returns [ActorDeclaration ast]
     :
        ACTOR name = identifier (EXTENDS parent = identifier)? LPAREN queue = INTVAL RPAREN
        {
            $ast = new ActorDeclaration($name.ast);
            $ast.setParentName($parent.ast);
            $ast.setQueueSize($queue.int);
        }
        LBRACE

        (KNOWNACTORS
        LBRACE
            (
                actorType = identifier actorName = identifier SEMICOLON
                {
                    $ast.addKnownActor(new VarDeclaration($actorName.ast, new ActorType($actorType.ast)));
                }
            )*
        RBRACE)

        (ACTORVARS
        LBRACE
            varDeclarations {$ast.setActorVars($varDeclarations.ast);}
        RBRACE)

        (initHandlerDeclaration {$ast.setInitHandler($initHandlerDeclaration.ast);})?
        (msgHandlerDeclaration {$ast.addMsgHandler($msgHandlerDeclaration.ast);})*

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

initHandlerDeclaration returns [InitHandlerDeclaration ast]
    :	MSGHANDLER INITIAL LPAREN argDeclarations RPAREN
        {
        $ast=new InitHandlerDeclaration(new Identifier($INITIAL.text));
        $ast.setLine($MSGHANDLER.getLine());
        }
     	LBRACE
     	args = varDeclarations {$ast.setArgs($args.ast);}

     	(statement)*
     	RBRACE
    ;

msgHandlerDeclaration returns [MsgHandlerDeclaration ast]
    :	MSGHANDLER identifier LPAREN argDeclarations RPAREN
        {$ast = new MsgHandlerDeclaration($identifier.ast);
         $ast.setLine($MSGHANDLER.getLine());
        }
       	LBRACE
       	args = varDeclarations {$ast.setArgs($args.ast);}
       	(statement)*
       	RBRACE
    ;

argDeclarations
    :	varDeclaration(COMMA varDeclaration)* |
    ;

varDeclarations returns [ArrayList<VarDeclaration> ast]
    : {$ast = new ArrayList<>();}
    (varDeclaration SEMICOLON { $ast.add($varDeclaration.ast);})*
    ;

varDeclaration returns [VarDeclaration ast]
    :	INT identifier
        {
            $ast = new VarDeclaration($identifier.ast, new IntType());
        }
    |   STRING identifier
            {
                $ast = new VarDeclaration($identifier.ast, new StringType());
            }
    |   BOOLEAN identifier
                {
                    $ast = new VarDeclaration($identifier.ast, new BooleanType());
                }
    |   INT identifier LBRACKET INTVAL RBRACKET
                    {
                        ArrayType type = new ArrayType($INTVAL.int);
                        $ast = new VarDeclaration($identifier.ast, type);
                    }
    ;

statement returns [Statement ast]
    :	blockStmt
    | 	printStmt
    |  	assignStmt
    |  	forStmt
    |  	ifStmt
    |  	continueStmt
    |  	breakStmt
    |  	msgHandlerCall
    ;

blockStmt returns [Block ast]
    : 	LBRACE (statement)* RBRACE
        {
        $ast = new Block();
        $ast.addStatement($statement.ast);
        $ast.setLine($LBRACE.getLine());
        }
    ;

printStmt returns [Statement ast]
    : 	PRINT LPAREN expression RPAREN SEMICOLON
        {
        $ast = new Print($expression.ast);
        $ast.setLine($PRINT.getLine());
        }
    ;

assignStmt returns [Assign ast]
    :    assignment SEMICOLON {$ast = $assignment.ast;}
    ;

assignment returns [Assign ast]
    :   orExpression ASSIGN expression {$ast = new Assign($orExpression.ast, $expression.ast); $ast.setLine($orExpression.ast.getLine());}
    ;

forStmt returns [For ast]
    : 	FOR LPAREN (Initialize = assignment)? SEMICOLON (Condition = expression)? SEMICOLON (Update = assignment)? RPAREN body= statement
        {
        $ast = new For();
        $ast.setInitialize($Initialize.ast);
        $ast.setCondition($Condition.ast);
        $ast.setUpdate($Update.ast);
        $ast.setBody($body.ast);
        $ast.setLine($FOR.getLine());
        }
    ;

ifStmt returns [Conditional ast]
    :   IF LPAREN expression RPAREN statement elseStmt
        {
            $ast = new Conditional($expression.ast, $statement.ast);
            $ast.setElseBody($elseStmt.ast);
        }
    ;

elseStmt returns [Statement ast]
    : ELSE statement {$ast = $statement.ast;} | {$ast = null;}
    ;

continueStmt returns [Continue ast]
    : 	CONTINUE SEMICOLON
        {
        $ast = new Continue();
        $ast.setLine($CONTINUE.getLine());
        }
    ;

breakStmt returns [Break ast]
    : 	BREAK SEMICOLON
        {
        $ast = new Break();
        $ast.setLine($BREAK.getLine());
        }
    ;

msgHandlerCall returns [MsgHandlerCall ast]
    :   {Expression ins = null;}
    (
    identifier {ins = $identifier.ast; ins.setLine($identifier.ast.getLine());} |
        SELF {ins = new Self(); ins.setLine($SELF.getLine());} |
        SENDER {ins = new Sender(); ins.setLine($SENDER.getLine());}
     ) DOT
        name = identifier LPAREN expressionList RPAREN SEMICOLON
        {
            $ast = new MsgHandlerCall(ins, $name.ast);
            $ast.setArgs($expressionList.ast);
            $ast.setLine(ins.getLine());
        }
    ;

expression returns [Expression ast]
    :	orExpression (ASSIGN expression)?
    ;

orExpression returns [BinaryExpression ast]
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

expressionList returns [ArrayList<Expression> ast]
    :	{$ast = new ArrayList();}(expression {$ast.add($expression.ast);}(COMMA expression{$ast.add($expression.ast);})* | )
    ;

identifier returns [Identifier ast]
    :   IDENTIFIER {$ast = new Identifier($IDENTIFIER.text);}
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