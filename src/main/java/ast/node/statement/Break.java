package ast.node.statement;

import visitor.Visitor;

public class Break extends Statement {
	
	@Override
    public String toString() {
        return "Break";
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }}