package ast.node.expression;

import visitor.Visitor;

public class Sender extends Expression {
    @Override
    public String toString() {
         return "Sender";
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
