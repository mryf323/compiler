package ast.node.statement;

import visitor.Visitor;

public class Continue extends Statement {

	@Override
    public String toString() {
        return "Continue";
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
