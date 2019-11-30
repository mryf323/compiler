package ast.node.expression;

import visitor.Visitor;

public class Sender extends Expression {
    @Override
    public String toString() {
         return "Sender";
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }}
