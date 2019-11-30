package ast.node.statement;

import visitor.Visitor;
import ast.node.expression.Expression;

public class Print extends Statement {
    private Expression arg;

    public Print(Expression arg) {
        this.arg = arg;
    }

    public Expression getArg() {
        return arg;
    }

    public void setArg(Expression arg) {
        this.arg = arg;
    }

    @Override
    public String toString() {
        return "Print";
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
