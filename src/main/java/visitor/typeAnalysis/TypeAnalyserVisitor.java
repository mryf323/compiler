package visitor.typeAnalysis;

import ast.node.Main;
import ast.node.Program;
import ast.node.declaration.ActorDeclaration;
import ast.node.declaration.ActorInstantiation;
import ast.node.declaration.VarDeclaration;
import ast.node.declaration.handler.HandlerDeclaration;
import ast.node.expression.*;
import ast.node.expression.operators.BinaryOperator;
import ast.node.expression.values.BooleanValue;
import ast.node.expression.values.IntValue;
import ast.node.expression.values.StringValue;
import ast.node.statement.*;
import ast.type.NoType;
import ast.type.Type;
import ast.type.actorType.ActorType;
import ast.type.arrayType.ArrayType;
import ast.type.primitiveType.BooleanType;
import ast.type.primitiveType.IntType;
import ast.type.primitiveType.StringType;
import symbolTable.SymbolTable;
import symbolTable.SymbolTableActorItem;
import symbolTable.SymbolTableHandlerItem;
import symbolTable.SymbolTableMainItem;
import symbolTable.itemException.ItemNotFoundException;
import symbolTable.symbolTableVariableItem.SymbolTableActorVariableItem;
import symbolTable.symbolTableVariableItem.SymbolTableVariableItem;
import visitor.Visitor;
import visitor.nameAnalysis.ActorInheritanceService;
import visitor.typeAnalysis.rule.binary.*;
import visitor.typeAnalysis.rule.unary.MinusRule;
import visitor.typeAnalysis.rule.unary.MutatorOperatorRule;
import visitor.typeAnalysis.rule.unary.NotRule;

import java.util.List;
import java.util.stream.Collectors;

import static visitor.typeAnalysis.TypeAnalyserMessageSource.*;

public class TypeAnalyserVisitor implements Visitor<AnalysedType> {
    
    private final SymbolTable global;
    private ActorInheritanceService inheritanceService = ActorInheritanceService.getInstance();


    private ActorDeclaration currentActor;
    private boolean inLoop = false;
    private boolean inInitHandler = false;


    public TypeAnalyserVisitor(SymbolTable global) {
        this.global = global;
    }

    @Override
    public AnalysedType visit(Program program) {
        SymbolTable.push(this.global);
        program.getActors().forEach(i -> i.accept(this));
        program.getMain().accept(this);
        SymbolTable.pop();
        return null;
    }

    private AnalysedType visitActorId(Identifier identifier){
        SymbolTableActorItem item;
        try {
            item = (SymbolTableActorItem) SymbolTable.top
                    .get(SymbolTableActorItem.STARTKEY + identifier.getName());
            return new AnalysedType<>(new ActorType(item.getActorDeclaration()), true);
        } catch (ItemNotFoundException e) {
            return AnalysedType.NO_TYPE;
        }
    }

    @Override
    public AnalysedType visit(ActorDeclaration actorDec) { try {

        if (actorDec.getParentName() != null) {
            AnalysedType parentType = visitActorId(actorDec.getParentName());
            if (parentType.getType() instanceof NoType)
                System.out.printf(UNDECLARED_ACTOR,
                        actorDec.getParentName().getLine(), actorDec.getParentName().getName()
                );
        }

        SymbolTable symbolTable = ((SymbolTableActorItem)
                SymbolTable.top.get(SymbolTableActorItem.STARTKEY + actorDec.getName().getName())).getActorSymbolTable();

        SymbolTable.push(symbolTable);
        this.currentActor = actorDec;

        actorDec.getKnownActors().forEach(i -> i.accept(this));

        actorDec.getActorVars().forEach(i -> i.accept(this));

        if (actorDec.getInitHandler() != null) {
            inInitHandler = true;
            actorDec.getInitHandler().accept(this);
            inInitHandler = false;
        }
        actorDec.getMsgHandlers().forEach(i -> i.accept(this));
        SymbolTable.pop();
        this.currentActor = null;
        return null;
    } catch (ItemNotFoundException ignored) {} return null;}

    @Override
    public AnalysedType visit(HandlerDeclaration handlerDec) {try{

        handlerDec.getArgs().forEach(i -> i.accept(this));

        SymbolTableHandlerItem symbolTable = (SymbolTableHandlerItem)
                SymbolTable.top.get(SymbolTableHandlerItem.STARTKEY + handlerDec.getName().getName());
        SymbolTable.push(symbolTable.getHandlerSymbolTable());

        handlerDec.getArgs().forEach(i -> i.accept(this));
        handlerDec.getLocalVars().forEach(i -> i.accept(this));
        handlerDec.getBody().forEach(i -> i.accept(this));
        SymbolTable.pop();
        return null;
    }catch (ItemNotFoundException ignored){}return null;}

    @Override
    public AnalysedType visit(VarDeclaration varDec) {

        AnalysedType varType = varDec.getType().accept(this);
        if (varDec.getType()instanceof ActorType && varType.getType() instanceof NoType)
            System.out.printf(UNDECLARED_ACTOR, varDec.getLine(),
                    ((ActorType)varDec.getType()).getName().getName()
            );

        return varType;
    }

    @Override
    public AnalysedType visit(Main mainActors) {try{

        SymbolTableMainItem symbolTable = (SymbolTableMainItem)
                SymbolTable.top.get(SymbolTableMainItem.STARTKEY + SymbolTableMainItem.NAME);
        SymbolTable.push(symbolTable.getMainSymbolTable());
        mainActors.getMainActors().forEach(ins -> ins.accept(this));
        SymbolTable.pop();
        return null;
    }catch (ItemNotFoundException ignored){}return null;}

    @Override
    public AnalysedType visit(ActorInstantiation actorInstantiation) {

        AnalysedType type = visit((VarDeclaration) actorInstantiation);

        if (type.getType() instanceof ActorType) { try {
            SymbolTableActorItem actorItem = (SymbolTableActorItem) SymbolTable.top.get(SymbolTableActorItem.STARTKEY +
                    ((ActorType) type.getType()).getName().getName());
            ActorDeclaration actorDeclaration = actorItem.getActorDeclaration();
/*            List<ActorDeclaration> parents = inheritanceService.transitiveParents(actorDeclaration)
                    .stream().map(SymbolTableActorItem::getActorDeclaration)
                    .collect(Collectors.collectingAndThen(Collectors.toList(), l -> {
                        Collections.reverse(l);
                        return l;
                    }));
            parents.add(actorDeclaration);

            List<AnalysedType> expectedKnownActors = parents.stream()
                    .flatMap(p -> p.getKnownActors().stream())
                    .map(dec -> new AnalysedType<>(dec.getType(), true))
                    .collect(Collectors.toList());*/

            List<AnalysedType> expectedKnownActors = actorDeclaration.getKnownActors()
                    .stream().map(dec -> new AnalysedType<>(dec.getType(), true))
                    .collect(Collectors.toList());

            List<AnalysedType> actualKnownActors = actorInstantiation.getKnownActors()
                    .stream().map(i -> i.accept(this))
                    .collect(Collectors.toList());

            if(!checkKnownActorArgs(expectedKnownActors, actualKnownActors))
                System.out.printf(KNOWN_ACTOR_MISMATCH, actorInstantiation.getLine());

            if (actorDeclaration.getInitHandler() != null) {
                MsgHandlerCall call = new MsgHandlerCall(actorInstantiation.getIdentifier(), new Identifier("initial"));
                actorInstantiation.getInitArgs().forEach(call::addArg);
                call.accept(this);
            } else if (actorInstantiation.getInitArgs().size() > 0) {
                actorInstantiation.getInitArgs().forEach(arg -> arg.accept(this));
                System.out.printf(MSG_HANDLER_ARG_MATCH, actorInstantiation.getLine());
            }
        }catch(ItemNotFoundException ignored){}}
        return null;

    }

    @Override
    public AnalysedType visit(UnaryExpression unaryExpression) {

        AnalysedType analysedType = unaryExpression.getOperand().accept(this);
        switch (unaryExpression.getUnaryOperator()) {
            case not:
                AnalysedType apply = new NotRule(unaryExpression).apply(analysedType);
                unaryExpression.setType(apply.getType());
                return apply;
            case minus:
                AnalysedType apply1 = new MinusRule(unaryExpression).apply(analysedType);
                unaryExpression.setType(apply1.getType());
                return apply1;
            case postdec:
            case predec:
            case postinc:
            case preinc:
                AnalysedType apply2 = new MutatorOperatorRule(unaryExpression).apply(analysedType);
                unaryExpression.setType(apply2.getType());
                return apply2;
        }
        unaryExpression.setType(new NoType());
        return AnalysedType.NO_TYPE;
    }

    @Override
    public AnalysedType visit(BinaryExpression binaryExpression) {

        AnalysedType left = binaryExpression.getLeft().accept(this);
        AnalysedType right = binaryExpression.getRight().accept(this);
        AnalysedType result;
        switch (binaryExpression.getBinaryOperator()){
            case assign:
                result = new AssignmentRule(binaryExpression).apply(left, right);
                break;
            case eq:
            case neq:
                result = new EqualityRule(binaryExpression).apply(left, right);
                break;
            case and:
            case or:
                result = new AndOrRule(binaryExpression).apply(left, right);
                break;
            case gt:
            case lt:
                 result = new GtLtRule(binaryExpression).apply(left, right);
                 break;
            case sub:
            case add:
            case mult:
            case div:
            case mod:
                result = new ArithmeticRule(binaryExpression).apply(left, right);
                break;
            default:
                result = AnalysedType.NO_TYPE;
        }
        binaryExpression.setType(result.getType());
        return result;
    }

    @Override
    public AnalysedType visit(ArrayCall arrayCall) {

        AnalysedType instanceType = arrayCall.getArrayInstance().accept(this);
        AnalysedType indexType = arrayCall.getIndex().accept(this);

        if (instanceType.getType() instanceof ArrayType && indexType.getType() instanceof IntType) {
            arrayCall.setType(new IntType());
            return new AnalysedType<>(new IntType(), instanceType.islValue());
        }
        arrayCall.setType(new NoType());
        return AnalysedType.NO_TYPE;
    }

    @Override
    public AnalysedType visit(ActorVarAccess actorVarAccess) {
        AnalysedType analysedType = actorVarAccess.getSelf().accept(this);
        if (analysedType.getType() instanceof NoType) {
            actorVarAccess.setType(analysedType.getType());
            return analysedType;
        }
        String var = actorVarAccess.getVariable().getName();
        try {
            SymbolTableVariableItem item = (SymbolTableVariableItem) SymbolTable.top.getPreSymbolTable()
                    .get(SymbolTableActorVariableItem.STARTKEY + var);
            actorVarAccess.setType(item.getType());
            actorVarAccess.getVariable().setType(item.getType());
            return new AnalysedType<>(item.getType(), true);
        } catch (ItemNotFoundException ignored) {
            System.out.printf(UNDECLARED_VAR, actorVarAccess.getLine(), var);
            actorVarAccess.getVariable().setType(new NoType());
            actorVarAccess.setType(new NoType());
            return AnalysedType.NO_TYPE;
        }
    }

    @Override
    public AnalysedType visit(Identifier identifier) {

        try {
            SymbolTableVariableItem item = (SymbolTableVariableItem) SymbolTable.top
                    .get(SymbolTableVariableItem.STARTKEY + identifier.getName());
            identifier.setType(item.getType());
            return new AnalysedType<>(item.getType(), true);
        } catch (ItemNotFoundException ignored) {
            System.out.printf(UNDECLARED_VAR, identifier.getLine(), identifier.getName());
            identifier.setType(new NoType());
            return AnalysedType.NO_TYPE;
        }
    }

    @Override
    public AnalysedType visit(Self self) {
        if (currentActor != null) {
            self.setType(new ActorType(currentActor));
            return new AnalysedType<>(new ActorType(currentActor), false);
        }
        System.out.printf(SELF_OUT_OF_ACTOR, self.getLine());
        self.setType(new NoType());
        return AnalysedType.NO_TYPE;
    }

    @Override
    public AnalysedType visit(Sender sender) {
        if (inInitHandler)
            System.out.printf(NO_SENDER_IN_INITIAL, sender.getLine());
        ActorType type = new ActorType(new Identifier("Actor"));
        sender.setType(type);
        return new AnalysedType<>(type, false);
    }

    @Override
    public AnalysedType visit(BooleanValue value) {
        value.setType(new BooleanType());
        return new AnalysedType<>(new BooleanType(), false);
    }

    @Override
    public AnalysedType visit(IntValue value) {
        value.setType(new IntType());
        return new AnalysedType<>(new IntType(), false);
    }

    @Override
    public AnalysedType visit(StringValue value) {
        value.setType(new StringType());
        return new AnalysedType<>(new StringType(), false);
    }

    @Override
    public AnalysedType visit(Block block) {
        block.getStatements().forEach(i -> i.accept(this));
        return null;
    }

    @Override
    public AnalysedType visit(Conditional conditional) {

        AnalysedType type = conditional.getExpression().accept(this);

        if (!(type.getType() instanceof NoType) && !(type.getType() instanceof BooleanType))
            System.out.printf(BOOL_CONDITION, conditional.getExpression().getLine());

        conditional.getThenBody().accept(this);

        if (conditional.getElseBody() != null)
            conditional.getElseBody().accept(this);

        return null;
    }

    @Override
    public AnalysedType visit(For loop) {

        if (loop.getInitialize() != null)
            loop.getInitialize().accept(this);

        if (loop.getCondition() != null) {
            AnalysedType type = loop.getCondition().accept(this);
            if (!(type.getType() instanceof NoType) && !(type.getType() instanceof BooleanType))
                System.out.printf(BOOL_CONDITION, loop.getCondition().getLine());
        }

        if (loop.getUpdate() != null)
            loop.getUpdate().accept(this);

        inLoop = true;
        loop.getBody().accept(this);
        inLoop = false;

        return null;
    }

    @Override
    public AnalysedType visit(Break breakLoop) {
        if (!inLoop)
            System.out.printf(BREAK_WITHOUT_LOOP, breakLoop.getLine());
        return null;
    }

    @Override
    public AnalysedType visit(Continue continueLoop) {
        if (!inLoop)
            System.out.printf(CONTINUE_WITHOUT_LOOP, continueLoop.getLine());
        return null;
    }

    private boolean checkKnownActorArgs(List<AnalysedType> expectedArgs, List<AnalysedType> actualArgs) {

        boolean match = actualArgs.size() == expectedArgs.size();
        if (!match)
            return false;
        for (int i = 0; i < expectedArgs.size(); i++) {
            Type lhsType = expectedArgs.get(i).getType();
            Type rhsType = actualArgs.get(i).getType();

            if (lhsType instanceof ActorType && rhsType instanceof ActorType) {
                    match &= inheritanceService.isAssignable((ActorType) lhsType, (ActorType) rhsType);
            } else if (!(rhsType instanceof NoType) && !(lhsType instanceof NoType))
                match = false;
        }
        return match;
    }
    private boolean checkMsgHandlerArgs(List<AnalysedType> expectedArgs, List<AnalysedType> actualArgs) {

        boolean match = actualArgs.size() == expectedArgs.size();
        if (!match)
            return false;
        for (int i = 0; i < expectedArgs.size(); i++) {
            Type lhsType = expectedArgs.get(i).getType();
            Type rhsType = actualArgs.get(i).getType();

            if (lhsType instanceof ActorType || rhsType instanceof ActorType)
                match = false;
            else if (!rhsType.getClass().isAssignableFrom(lhsType.getClass()))
                if (!(lhsType instanceof NoType) &&!(rhsType instanceof NoType))
                    match = false;
        }
        return match;
    }


    @Override
    public AnalysedType visit(MsgHandlerCall msgHandlerCall) {

        Expression ins = msgHandlerCall.getInstance();
        Type instanceType = ins.accept(this).getType();
        if (!(instanceType instanceof NoType) && !(instanceType instanceof ActorType)) {
            if (ins instanceof Identifier)
                System.out.printf(NON_CALLABLE_VAR, ins.getLine(), ((Identifier) ins).getName());
            else
                System.out.printf(NON_CALLABLE_VAR, ins.getLine(), "AN_EXPR");

        } else if (instanceType instanceof ActorType) {
            try {
                SymbolTableActorItem actorItem = (SymbolTableActorItem) SymbolTable.top.get(SymbolTableActorItem.STARTKEY +
                        ((ActorType) instanceType).getName().getName());

                SymbolTableHandlerItem handler = (SymbolTableHandlerItem) actorItem.getActorSymbolTable().get(
                        SymbolTableHandlerItem.STARTKEY + msgHandlerCall.getMsgHandlerName().getName()
                );
                List<AnalysedType> actualArgs = msgHandlerCall.getArgs()
                            .stream().map(arg -> arg.accept(this)).collect(Collectors.toList());

                    List<AnalysedType> expectedArgs = handler.getHandlerDeclaration().getArgs()
                            .stream().map(arg -> new AnalysedType<>(arg.getType(), true))
                            .collect(Collectors.toList());

                boolean argMatch = checkMsgHandlerArgs(expectedArgs, actualArgs);
                if (!argMatch)
                    System.out.printf(MSG_HANDLER_ARG_MATCH, msgHandlerCall.getLine());
            } catch (ItemNotFoundException e) {
                System.out.printf(
                        UNDECLARED_HANDLER,
                        msgHandlerCall.getLine(), msgHandlerCall.getMsgHandlerName().getName(),
                        ((ActorType) instanceType).getName().getName()
                );
            }

        }
        return null;
    }

    @Override
    public AnalysedType visit(Print print) {
        AnalysedType argType = print.getArg().accept(this);
        if (argType.getType() instanceof ActorType)
            System.out.printf(PRINT_UNSUPPORTED_TYPE, print.getArg().getLine());
        return null;
    }

    @Override
    public AnalysedType visit(Assign assign) {
        AnalysedType lType = assign.getlValue().accept(this);
        AnalysedType rType = assign.getrValue().accept(this);
        BinaryExpression assignExpr = new BinaryExpression(
                assign.getlValue(),
                assign.getrValue(),
                BinaryOperator.assign
        );
        assignExpr.setLine(assign.getLine());
        return new AssignmentRule(assignExpr).apply(lType, rType);
    }

    @Override
    public AnalysedType visit(ArrayType arrayType) {
        return new AnalysedType<>(arrayType, false);
    }

    @Override
    public AnalysedType visit(ActorType actorType) {
        return visitActorId(actorType.getName());
    }

    @Override
    public AnalysedType visit(StringType stringType) {
        return new AnalysedType<>(stringType, false);
    }

    @Override
    public AnalysedType visit(IntType intType) {
        return new AnalysedType<>(intType, false);
    }

    @Override
    public AnalysedType visit(BooleanType booleanType) {
        return new AnalysedType<>(booleanType, false);
    }

    @Override
    public AnalysedType visit(NoType noType) {
        return AnalysedType.NO_TYPE;
    }

}
