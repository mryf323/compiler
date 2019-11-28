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
    : {$p = new Program();} (dec = actorDeclaration { $p.addActor($dec.synAst); })+ main = mainDeclaration {$p.setMain($main.synAst);}
    ;

actorDeclaration returns [ActorDeclaration synAst]
     :
        ACTOR name = identifier (EXTENDS parent = identifier)? LPAREN queue = INTVAL RPAREN
        {
            $synAst = new ActorDeclaration($name.synAst);
            $synAst.setParentName($parent.synAst);
            $synAst.setQueueSize($queue.int);
            $synAst.setLine($ACTOR.getLine());
        }
        LBRACE

        (KNOWNACTORS
        LBRACE
            (
                actorType = identifier actorName = identifier SEMICOLON
                {
                    $synAst.addKnownActor(new VarDeclaration($actorName.synAst, new ActorType($actorType.synAst)));
                }
            )*
        RBRACE)

        (ACTORVARS
        LBRACE
            varDeclarations {$synAst.setActorVars($varDeclarations.synAst);}
        RBRACE)

        (initHandlerDeclaration {$synAst.setInitHandler($initHandlerDeclaration.synAst);})?
        (msgHandlerDeclaration {$synAst.addMsgHandler($msgHandlerDeclaration.synAst);})*

        RBRACE
    ;

mainDeclaration returns [Main synAst]
    :   MAIN
    	LBRACE
        actorInstantiation*
    	RBRACE
    	{
    	$synAst = new Main();
    	$synAst.addActorInstantiation($actorInstantiation.synAst);
    	$synAst.setLine($MAIN.getLine());
    	}
    ;

actorInstantiation returns [ActorInstantiation synAst]
    :	type = identifier name = identifier
     	LPAREN (identifier{$synAst.addKnownActor($identifier.synAst);}(COMMA identifier{$synAst.addKnownActor($identifier.synAst);})* | ) RPAREN
     	COLON LPAREN expressionList RPAREN SEMICOLON
     	{
     	$synAst = new ActorInstantiation(new ActorType($type.synAst), $name.synAst);
        $synAst.setInitArgs($expressionList.synAst);
        $synAst.setLine($LPAREN.getLine());
     	}
    ;

initHandlerDeclaration returns [InitHandlerDeclaration synAst]
    :	MSGHANDLER INITIAL LPAREN args = argDeclarations {$synAst.setArgs($args.synAst);} RPAREN
        {
        $synAst=new InitHandlerDeclaration(new Identifier($INITIAL.text));
        $synAst.setLine($MSGHANDLER.getLine());
        }
     	LBRACE
     	varDeclarations {$synAst.setLocalVars($varDeclarations.synAst);}
        (statement {$synAst.addStatement($statement.synAst);})*
     	RBRACE
    ;

msgHandlerDeclaration returns [MsgHandlerDeclaration synAst]
    :	MSGHANDLER identifier LPAREN args = argDeclarations {$synAst.setArgs($args.synAst);} RPAREN
        {$synAst = new MsgHandlerDeclaration($identifier.synAst);
         $synAst.setLine($MSGHANDLER.getLine());
        }
       	LBRACE
       	 varDeclarations {$synAst.setLocalVars($varDeclarations.synAst);}
       	(statement {$synAst.addStatement($statement.synAst);})*
       	RBRACE
    ;

argDeclarations returns [ArrayList<VarDeclaration> synAst]
    :	{$synAst = new ArrayList<>();} varDeclaration{$synAst.add($varDeclaration.synAst);}
        (COMMA varDeclaration{$synAst.add($varDeclaration.synAst);})* |
         {$synAst = new ArrayList<>();}
    ;

varDeclarations returns [ArrayList<VarDeclaration> synAst]
    : {$synAst = new ArrayList<>();}
    (varDeclaration SEMICOLON { $synAst.add($varDeclaration.synAst);})*
    ;

varDeclaration returns [VarDeclaration synAst]
    :	INT identifier
        {
            $synAst = new VarDeclaration($identifier.synAst, new IntType());
        }
    |   STRING identifier
            {
                $synAst = new VarDeclaration($identifier.synAst, new StringType());
            }
    |   BOOLEAN identifier
                {
                    $synAst = new VarDeclaration($identifier.synAst, new BooleanType());
                }
    |   INT identifier LBRACKET INTVAL RBRACKET
                    {
                        ArrayType type = new ArrayType($INTVAL.int);
                        $synAst = new VarDeclaration($identifier.synAst, type);
                    }
    ;

statement returns [Statement synAst]
    :	blockStmt
    | 	printStmt
    |  	assignStmt
    |  	forStmt
    |  	ifStmt
    |  	continueStmt
    |  	breakStmt
    |  	msgHandlerCall
    ;

blockStmt returns [Block synAst]
    : 	LBRACE (statement)* RBRACE
        {
        $synAst = new Block();
        $synAst.addStatement($statement.synAst);
        $synAst.setLine($LBRACE.getLine());
        }
    ;

printStmt returns [Statement synAst]
    : 	PRINT LPAREN expression RPAREN SEMICOLON
        {
        $synAst = new Print($expression.synAst);
        $synAst.setLine($PRINT.getLine());
        }
    ;

assignStmt returns [Assign synAst]
    :    assignment SEMICOLON {$synAst = $assignment.synAst;}
    ;

assignment returns [Assign synAst]
    :   orExpression ASSIGN expression {$synAst = new Assign($orExpression.synAst, $expression.synAst); $synAst.setLine($orExpression.synAst.getLine());}
    ;

forStmt returns [For synAst]
    : 	FOR LPAREN (Initialize = assignment)? SEMICOLON (Condition = expression)? SEMICOLON (Update = assignment)? RPAREN body= statement
        {
        $synAst = new For();
        $synAst.setInitialize($Initialize.synAst);
        $synAst.setCondition($Condition.synAst);
        $synAst.setUpdate($Update.synAst);
        $synAst.setBody($body.synAst);
        $synAst.setLine($FOR.getLine());
        }
    ;

ifStmt returns [Conditional synAst]
    :   IF LPAREN expression RPAREN statement elseStmt
        {
            $synAst = new Conditional($expression.synAst, $statement.synAst);
            $synAst.setElseBody($elseStmt.synAst);
        }
    ;

elseStmt returns [Statement synAst]
    : ELSE statement {$synAst = $statement.synAst;} | {$synAst = null;}
    ;

continueStmt returns [Continue synAst]
    : 	CONTINUE SEMICOLON
        {
        $synAst = new Continue();
        $synAst.setLine($CONTINUE.getLine());
        }
    ;

breakStmt returns [Break synAst]
    : 	BREAK SEMICOLON
        {
        $synAst = new Break();
        $synAst.setLine($BREAK.getLine());
        }
    ;

msgHandlerCall returns [MsgHandlerCall synAst]
    :   {Expression ins = null;}
    (
    identifier {ins = $identifier.synAst; ins.setLine($identifier.synAst.getLine());} |
        SELF {ins = new Self(); ins.setLine($SELF.getLine());} |
        SENDER {ins = new Sender(); ins.setLine($SENDER.getLine());}
     ) DOT
        name = identifier LPAREN expressionList RPAREN SEMICOLON
        {
            $synAst = new MsgHandlerCall(ins, $name.synAst);
            $synAst.setArgs($expressionList.synAst);
            $synAst.setLine(ins.getLine());
        }
    ;

expression returns [Expression synAst]
    :	orExpression (ASSIGN expression)?
    ;

orExpression returns [BinaryExpression synAst]
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

expressionList returns [ArrayList<Expression> synAst]
    :	{$synAst = new ArrayList();}(expression {$synAst.add($expression.synAst);}(COMMA expression{$synAst.add($expression.synAst);})* | )
    ;

identifier returns [Identifier synAst]
    :   IDENTIFIER {$synAst = new Identifier($IDENTIFIER.text);}
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