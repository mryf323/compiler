grammar acton;

@header {
package parsers;
import ast.node.declaration.handler.*;
import ast.node.*;
import ast.*;
import ast.node.expression.*;
import ast.node.expression.values.*;
import ast.node.statement.*;
import ast.node.declaration.*;
import ast.type.*;
import ast.type.arrayType.*;
import ast.type.primitiveType.*;
import ast.node.expression.operators.BinaryOperator;
import ast.node.expression.operators.UnaryOperator;
import ast.node.expression.Identifier;
import ast.type.actorType.*;
import java.util.*;
import parsers.actonLexer;
import parsers.actonParser;
}
program returns [Program p]
    : {$p = new Program();}
      (dec = actorDeclaration { $p.addActor($dec.synAst); })+
      main = mainDeclaration {$p.setMain($main.synAst);}
    ;

actorDeclaration returns [ActorDeclaration synAst]
     :

        ACTOR name = identifier {$synAst = new ActorDeclaration($name.synAst);}
        (EXTENDS parent = identifier {$synAst.setParentName($parent.synAst);})? LPAREN queue = INTVAL RPAREN
        {
            $synAst.setQueueSize($queue.int);
            $synAst.setLine($ACTOR.line);
        }
        LBRACE

        (KNOWNACTORS
        LBRACE
            (
                actorType = identifier actorName = identifier SEMICOLON
                {
                    VarDeclaration known = new VarDeclaration($actorName.synAst, new ActorType($actorType.synAst));
                    known.setLine($actorType.start.getLine());
                    $synAst.addKnownActor(known);
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
        {$synAst = new Main(); $synAst.setLine($MAIN.line);}
    	LBRACE
        (actorInstantiation{$synAst.addActorInstantiation($actorInstantiation.synAst);})*
    	RBRACE
    ;

actorInstantiation returns [ActorInstantiation synAst]
    :

        type = identifier name = identifier
        {$synAst = new ActorInstantiation(new ActorType($type.synAst), $name.synAst);}
     	LPAREN (identifier{$synAst.addKnownActor($identifier.synAst);}(COMMA identifier{$synAst.addKnownActor($identifier.synAst);})* | ) RPAREN
     	COLON LPAREN expressionList RPAREN SEMICOLON
     	{

        $synAst.setInitArgs($expressionList.synAst);
        $synAst.setLine($LPAREN.line);
     	}
    ;

initHandlerDeclaration returns [InitHandlerDeclaration synAst]
    :	MSGHANDLER INITIAL LPAREN args = argDeclarations RPAREN
        {
        $synAst=new InitHandlerDeclaration(new Identifier($INITIAL.text));
        $synAst.setArgs($args.synAst);
        $synAst.setLine($MSGHANDLER.line);
        }
     	LBRACE
     	varDeclarations {$synAst.setLocalVars($varDeclarations.synAst);}
        (statement {$synAst.addStatement($statement.synAst);})*
     	RBRACE
    ;

msgHandlerDeclaration returns [MsgHandlerDeclaration synAst]
    :	MSGHANDLER identifier LPAREN args = argDeclarations  RPAREN
        {
            $synAst = new MsgHandlerDeclaration($identifier.synAst);
            $synAst.setArgs($args.synAst);
            $synAst.setLine($MSGHANDLER.line);
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
                $synAst.setLine($INT.line);
            }
    |   STRING identifier
            {
                $synAst = new VarDeclaration($identifier.synAst, new StringType());
                $synAst.setLine($STRING.line);
            }
    |   BOOLEAN identifier
            {
                $synAst = new VarDeclaration($identifier.synAst, new BooleanType());
                $synAst.setLine($BOOLEAN.line);
            }
    |   INT identifier LBRACKET INTVAL RBRACKET
            {
                ArrayType type = new ArrayType($INTVAL.int);
                type.setLine($INT.line);
                $synAst = new VarDeclaration($identifier.synAst, type);
                $synAst.setLine($INT.line);
            }
    ;

statement returns [Statement synAst]
    :
    	blockStmt {$synAst = $blockStmt.synAst;}
    | 	printStmt {$synAst = $printStmt.synAst;}
    |  	assignStmt {$synAst = $assignStmt.synAst;}
    |  	forStmt {$synAst = $forStmt.synAst;}
    |  	ifStmt {$synAst = $ifStmt.synAst;}
    |  	continueStmt {$synAst = $continueStmt.synAst;}
    |  	breakStmt {$synAst = $breakStmt.synAst;}
    |  	msgHandlerCall {$synAst = $msgHandlerCall.synAst;}
    ;

blockStmt returns [Block synAst]

    :   { $synAst = new Block();} 	LBRACE (statement {$synAst.addStatement($statement.synAst);})* RBRACE
        {

        $synAst.setLine($LBRACE.line);
        }
    ;

printStmt returns [Print synAst]
    : 	PRINT LPAREN expression RPAREN SEMICOLON
        {
        $synAst = new Print($expression.synAst);
        $synAst.setLine($PRINT.line);
        }
    ;

assignStmt returns [Assign synAst]
    :    assignment SEMICOLON {$synAst = $assignment.synAst;}
    ;

assignment returns [Assign synAst]
    :   orExpression ASSIGN expression
    {
        $synAst = new Assign($orExpression.synAst, $expression.synAst);
        $synAst.setLine($orExpression.start.getLine());
    }
    ;

forStmt returns [For synAst]
    :
        {$synAst = new For();}
        FOR LPAREN (Initialize = assignment {$synAst.setInitialize($Initialize.synAst);})?
        SEMICOLON (Condition = expression{$synAst.setCondition($Condition.synAst);})?
        SEMICOLON (Update = assignment{$synAst.setUpdate($Update.synAst);})?
        RPAREN body= statement
        {
        $synAst.setBody($body.synAst);
        $synAst.setLine($FOR.line);
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
        $synAst.setLine($CONTINUE.line);
        }
    ;

breakStmt returns [Break synAst]
    : 	BREAK SEMICOLON
        {
        $synAst = new Break();
        $synAst.setLine($BREAK.line);
        }
    ;

msgHandlerCall returns [MsgHandlerCall synAst]
    :   {Expression ins = null;}
    (
    identifier {ins = $identifier.synAst; } |
        SELF {ins = new Self(); ins.setLine($SELF.line);} |
        SENDER {ins = new Sender(); ins.setLine($SENDER.line);}
    ) DOT
        name = identifier LPAREN expressionList RPAREN SEMICOLON
        {
            $synAst = new MsgHandlerCall(ins, $name.synAst);
            $synAst.setArgs($expressionList.synAst);
            $synAst.setLine($identifier.start.getLine());
        }
    ;

expression returns [Expression synAst]
    :	orExpression {$synAst = $orExpression.synAst;}
    (ASSIGN expression {$synAst = new BinaryExpression($orExpression.synAst, $expression.synAst, BinaryOperator.assign);})?
    ;

orExpression returns [Expression synAst]
    :	left = andExpression {$synAst = $left.synAst;}
     (
     {BinaryOperator op = null; int line;}
     OR {op = BinaryOperator.or; line = $OR.line;}
     right = andExpression
        {
             $synAst = new BinaryExpression($left.synAst,$right.synAst,op);
             $synAst.setLine(line);
        }
      )*
    ;

andExpression returns [Expression synAst]
    :	left =  equalityExpression {$synAst = $left.synAst;}
    (
    {BinaryOperator op = null; int line;}
    AND {op = BinaryOperator.and; line = $AND.line;}
    right = equalityExpression
        {
             $synAst = new BinaryExpression($left.synAst,$right.synAst,op);
             $synAst.setLine(line);
        }
    )*
    ;

equalityExpression returns [Expression synAst]
    :	left = relationalExpression {$synAst = $left.synAst;} (
     {BinaryOperator op = null; int line;}
     (
            EQ {op = BinaryOperator.eq; line = $EQ.line;}|
           NEQ {op = BinaryOperator.neq; line = $NEQ.line;}
     )
       right =  relationalExpression
        {
             $synAst = new BinaryExpression($left.synAst,$right.synAst,op);
             $synAst.setLine(line);
        }
     )*
    ;

relationalExpression returns [Expression synAst]
    : left = additiveExpression {$synAst = $left.synAst;} (
        {BinaryOperator op = null; int line;}
        (
            LT {op = BinaryOperator.lt; line = $LT.line;}
            | GT {op = BinaryOperator.gt; line = $GT.line;}
        )
        right = additiveExpression
                    {
                        $synAst = new BinaryExpression($left.synAst,$right.synAst,op);
                        $synAst.setLine(line);
                    }
    )*
    ;

additiveExpression returns [Expression synAst]
    : left = multiplicativeExpression {$synAst = $left.synAst;} (
    {BinaryOperator op = null; int line;}
    (
        PLUS {op = BinaryOperator.add; line = $PLUS.line;}
        | MINUS {op = BinaryOperator.sub; line = $MINUS.line;}
    )
    right = multiplicativeExpression
            {
                $synAst = new BinaryExpression($left.synAst,$right.synAst,op);
                $synAst.setLine(line);
            }
     )*
    ;

multiplicativeExpression returns [Expression synAst]
    : left = preUnaryExpression {$synAst = $left.synAst;}
    (
        {BinaryOperator op = null; int line;}
        (
            MULT {op = BinaryOperator.mult; line = $MULT.line;} |
            DIV {op = BinaryOperator.div; line = $DIV.line;} |
            PERCENT {op = BinaryOperator.mod; line = $PERCENT.line;}
        )
         right = preUnaryExpression
        {
            $synAst = new BinaryExpression($left.synAst,$right.synAst,op);
            $synAst.setLine(line);
        }
    )*
    ;

preUnaryExpression returns [Expression synAst]
    :   val = NOT uExpr1 =  preUnaryExpression
        {
            $synAst = new UnaryExpression(UnaryOperator.not, $uExpr1.synAst);
            $synAst.setLine($val.line);
        }
    |   val = MINUS uExpr2 = preUnaryExpression
        {
            $synAst = new UnaryExpression(UnaryOperator.minus, $uExpr2.synAst);
            $synAst.setLine($val.line);
        }
    |   val = PLUSPLUS uExpr3 = preUnaryExpression
        {
            $synAst = new UnaryExpression(UnaryOperator.preinc, $uExpr3.synAst);
            $synAst.setLine($val.line);
        }
    |   val = MINUSMINUS uExpr4 = preUnaryExpression
        {
            $synAst = new UnaryExpression(UnaryOperator.predec, $uExpr4.synAst);
            $synAst.setLine($val.line);
        }
    |   postUnaryExpression
    {
        $synAst = $postUnaryExpression.synAst;
    }
    ;

postUnaryExpression returns [Expression synAst]
    :   operand = otherExpression {UnaryOperator op = null;} (postUnaryOp {op = $postUnaryOp.synAst;})?
        {
            if (op != null)
                $synAst = new UnaryExpression(op, $operand.synAst);
            else
                $synAst = $operand.synAst;
        }
    ;

postUnaryOp returns[UnaryOperator synAst]
    :  	PLUSPLUS  {$synAst=UnaryOperator.postinc;} |
        MINUSMINUS {$synAst=UnaryOperator.postdec;}
    ;

otherExpression returns [Expression synAst]
    :    LPAREN ex = expression RPAREN {$synAst = $ex.synAst; }
    |    identifier {$synAst = $identifier.synAst; }
    |    arrayCall {$synAst = $arrayCall.synAst; }
    |    actorVarAccess {$synAst = $actorVarAccess.synAst; }
    |    value {$synAst = $value.synAst; }
    |    SENDER {$synAst= new Sender(); $synAst.setLine($SENDER.line); }
    ;

arrayCall returns [ArrayCall synAst]
    :  {Expression ins = null;}  (identifier {ins = $identifier.synAst;} | actorVarAccess {ins = $actorVarAccess.synAst;}) LBRACKET ex = expression RBRACKET {$synAst = new ArrayCall(ins, $ex.synAst); $synAst.setLine(ins.getLine());}
    ;

actorVarAccess returns [ActorVarAccess synAst]
    :   SELF DOT id = identifier {$synAst = new ActorVarAccess($id.synAst);}
    ;

expressionList returns [ArrayList<Expression> synAst]
    :	{$synAst = new ArrayList();}(expression {$synAst.add($expression.synAst);}(COMMA expression{$synAst.add($expression.synAst);})* | )
    ;

identifier returns [Identifier synAst]
    :   IDENTIFIER {$synAst = new Identifier($IDENTIFIER.text); $synAst.setLine($IDENTIFIER.line);}
    ;

value returns [Value synAst]
    :   INTVAL {$synAst = new IntValue($INTVAL.int, new IntType()); $synAst.setLine($INTVAL.line);}
     | STRINGVAL {$synAst = new StringValue($STRINGVAL.text, new StringType()); $synAst.setLine($STRINGVAL.line);}
     | TRUE {$synAst = new BooleanValue(true, new BooleanType()); $synAst.setLine($TRUE.line);}
     | FALSE {$synAst = new BooleanValue(false, new BooleanType()); $synAst.setLine($FALSE.line);}
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