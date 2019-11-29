package ast.node.statement;

import visitor.Visitor;

public class Break extends Statement {
	
	@Override
    public String toString() {
        return "Break";
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}