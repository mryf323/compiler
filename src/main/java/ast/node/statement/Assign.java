package ast.node.statement;

import visitor.Visitor;
import ast.node.expression.Expression;

import javax.annotation.Nonnull;

public class Assign extends Statement {
    @Nonnull
    private Expression lValue;
    @Nonnull
    private Expression rValue;

    public Assign(@Nonnull Expression lValue, @Nonnull Expression rValue) {
        this.lValue = lValue;
        this.rValue = rValue;
    }

    @Nonnull
    public Expression getlValue() {
        return lValue;
    }

    public void setlValue(@Nonnull Expression lValue) {
        this.lValue = lValue;
    }

    @Nonnull
    public Expression getrValue() {
        return rValue;
    }

    public void setrValue(@Nonnull Expression rValue) {
        this.rValue = rValue;
    }

    @Override
    public String toString() {
        return "Assign";
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }}
