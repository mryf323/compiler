package ast.node.statement;

import ast.node.expression.Expression;
import visitor.Visitor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class For extends Statement {
    @Nullable
    private Assign initialize;
    @Nullable
    private Expression condition;
    @Nullable
    private Assign update;
    @Nonnull
    private Statement body;

    @Nullable
    public Assign getInitialize() {
        return initialize;
    }

    public void setInitialize(@Nullable Assign initialize) {
        this.initialize = initialize;
    }

    @Nullable
    public Expression getCondition() {
        return condition;
    }

    public void setCondition(@Nullable Expression condition) {
        this.condition = condition;
    }

    @Nullable
    public Assign getUpdate() {
        return update;
    }

    public void setUpdate(@Nullable Assign update) {
        this.update = update;
    }

    @Nonnull
    public Statement getBody() {
        return body;
    }

    public void setBody(@Nonnull Statement body) {
        this.body = body;
    }

    @Override
    public String toString() {
         return "For";
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
