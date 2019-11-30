package ast.node.expression.values;

import ast.type.Type;
import visitor.Visitor;

import javax.annotation.Nonnull;

public class BooleanValue extends Value {
    private boolean constant;

    public BooleanValue(boolean constant, Type type) {
        this.constant = constant;
        this.type = type;
    }

    public boolean getConstant() {
        return constant;
    }

    public void setConstant(boolean constant) {
        this.constant = constant;
    }

    @Override
    public String toString() {
         return "BooleanValue " + constant;
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }}
