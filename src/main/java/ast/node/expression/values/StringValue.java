package ast.node.expression.values;

import ast.type.Type;
import visitor.Visitor;

import javax.annotation.Nonnull;

public class StringValue extends Value {
    @Nonnull
    private String constant;

    public StringValue(@Nonnull String constant, Type type) {
        this.constant = constant;
        this.type = type;
    }

    @Nonnull
    public String getConstant() {
        return constant;
    }

    public void setConstant(@Nonnull String constant) {
        this.constant = constant;
    }

    @Override
    public String toString() {
        return "StringValue " + constant;
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }}
