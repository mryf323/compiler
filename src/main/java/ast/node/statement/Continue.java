package ast.node.statement;

import visitor.Visitor;

public class Continue extends Statement {

	@Override
    public String toString() {
        return "Continue";
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

}
