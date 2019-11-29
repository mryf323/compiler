package ast.node.expression;

import visitor.Visitor;

public class Self extends Expression {
    @Override
    public String toString() {
        return "Self";
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
