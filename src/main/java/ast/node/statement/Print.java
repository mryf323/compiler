package ast.node.statement;

import visitor.Visitor;
import ast.node.expression.Expression;

import javax.annotation.Nonnull;

public class Print extends Statement {
    @Nonnull
    private Expression arg;

    public Print(@Nonnull Expression arg) {
        this.arg = arg;
    }

    @Nonnull
    public Expression getArg() {
        return arg;
    }

    public void setArg(@Nonnull Expression arg) {
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
