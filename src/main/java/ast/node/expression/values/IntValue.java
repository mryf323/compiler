package ast.node.expression.values;

import ast.type.Type;
import visitor.Visitor;

import javax.annotation.Nonnull;

public class IntValue extends Value {
    private int constant;

    public IntValue(int constant, Type type) {
        this.constant = constant;
        this.type = type;
    }

    public int getConstant() {
        return constant;
    }

    public void setConstant(int constant) {
        this.constant = constant;
    }

    @Override
    public String toString() {
        return "IntValue " + constant;
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }}
