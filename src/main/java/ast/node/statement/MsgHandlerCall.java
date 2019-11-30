package ast.node.statement;

import ast.node.expression.Expression;
import ast.node.expression.Identifier;
import visitor.Visitor;

import javax.annotation.Nonnull;
import java.util.ArrayList;

public class MsgHandlerCall extends Statement {
    @Nonnull
    private Expression instance;
    @Nonnull
    private Identifier msgHandlerName;
    private ArrayList<Expression> args = new ArrayList<>();

    public MsgHandlerCall(@Nonnull Expression instance, @Nonnull Identifier msgHandlerName) {
        this.instance = instance;
        this.msgHandlerName = msgHandlerName;
    }

    @Nonnull
    public Expression getInstance() {
        return instance;
    }

    public void setInstance(@Nonnull Expression instance) {
        this.instance = instance;
    }

    @Nonnull
    public Identifier getMsgHandlerName() {
        return msgHandlerName;
    }

    public void setMsgHandlerName(@Nonnull Identifier msgHandlerName) {
        this.msgHandlerName = msgHandlerName;
    }

    public void setArgs(ArrayList<Expression> args) {
        this.args = args;
    }

    public ArrayList<Expression> getArgs() {
        return args;
    }

    public void addArg(Expression arg) {
        this.args.add(arg);
    }


    @Override
    public String toString() {
         return "MsgHandlerCall";
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }}
