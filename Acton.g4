grammar Acton;
@members{
    void print(String str){
        System.out.println(str);
    }
}

acton: (actorDeclaration)+ (main);

actorDeclaration:
    ACTOR ID (inherite)? 'LPAR' INTEGER RPAR LEFT_CURLY_BRACKET (actorBody) RIGHT_CURLY_BRACKET;

inherite:
    EXTENDS ID;

actorBody:
    (knownActors) (actorVars) (messageHandler)*;

knownActors:
     KNOWN_ACTOR LEFT_CURLY_BRACKET (knownActorDeclarationStatment)* RIGHT_CURLY_BRACKET;

knownActorDeclarationStatment: ID ID END_OF_STATEMENT;
actorVars: ACTOR_VARIABLE LEFT_CURLY_BRACKET (actorVar)*;
actorVar: type ID END_OF_STATEMENT;
type: (INT | BOOLEAN | STRING | intArray);
intArray: INT LEFT_SQUARE_BRACKET INT RIGHT_SQUARE_BRACKET;

messageHandler: MSG_HANDLER ID LPAR (argDeclrations)? RPAR LEFT_CURLY_BRACKET handlerBody RIGHT_CURLY_BRACKET;
argDeclrations: (type ID) (',' type ID)*;
handlerBody: (actorVar)*(handlerBlock)*;
handlerBlock:
    LEFT_CURLY_BRACKET (blockAndStatment) RIGHT_CURLY_BRACKET |
    LEFT_CURLY_BRACKET RIGHT_CURLY_BRACKET;
blockAndStatment: statment (blockAndStatment) |
                  handlerBlock blockAndStatment |
                  statment |
                  handlerBlock;

statment:
    assignment |
    ifThen




// Parser
ACTOR: 'actor';
LPAR: '(';
RPAR: ')';
LEFT_CURLY_BRACKET: '{';
RIGHT_CURLY_BRACKET: '}';
INTEGER: [0-9]+;
EXTENDS: 'extends';
KNOWN_ACTOR: 'knownactors';
ACTOR_VARIABLE: 'actorvars';
END_OF_STATEMENT: ';';
INT: 'int';
BOOLEAN: 'boolean';
STRING: 'string';
MSG_HANDLER: 'msghandler';
INIT: 'initial';


ID: [a-zA-Z _][a-zA-Z_0-9]*;