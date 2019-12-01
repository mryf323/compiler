package ast.node.statement;

import visitor.Visitor;
import ast.node.expression.Expression;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Conditional extends Statement {
    @Nonnull
    private Expression expression;
    @Nonnull
    private Statement thenBody;
    @Nullable
    private Statement elseBody;

    public Conditional(@Nonnull Expression expression, @Nonnull Statement thenBody) {
        this.expression = expression;
        this.thenBody = thenBody;
    }

    @Nonnull
    public Expression getExpression() {
        return expression;
    }

    public void setExpression(@Nonnull Expression expression) {
        this.expression = expression;
    }

    @Nonnull
    public Statement getThenBody() {
        return thenBody;
    }

    public void setThenBody(@Nonnull Statement thenBody) {
        this.thenBody = thenBody;
    }

    @Nullable
    public Statement getElseBody() {
        return elseBody;
    }

    public void setElseBody(@Nullable Statement elseBody) {
        this.elseBody = elseBody;
    }

    @Override
    public String toString() {
        return "Conditional";
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }}
