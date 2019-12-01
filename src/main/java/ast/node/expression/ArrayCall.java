package ast.node.expression;

import visitor.Visitor;

import javax.annotation.Nonnull;

public class ArrayCall extends Expression {
    @Nonnull
    private Expression arrayInstance;
    @Nonnull
    private Expression index;

    public ArrayCall(@Nonnull Expression arrayInstance, @Nonnull Expression index) {
        this.arrayInstance = arrayInstance;
        this.index = index;
    }

    @Nonnull
    public Expression getArrayInstance() {
        return arrayInstance;
    }

    public void setArrayInstance(@Nonnull Expression arrayInstance) {
        this.arrayInstance = arrayInstance;
    }

    @Nonnull
    public Expression getIndex() {
        return index;
    }

    public void setIndex(@Nonnull Expression index) {
        this.index = index;
    }

    @Override
    public String toString() {
        return "ArrayCall";
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }}
