package ast.node.expression;

import visitor.Visitor;

import javax.annotation.Nonnull;

public class ActorVarAccess extends Expression {
    @Nonnull
    private Identifier variableName;
    private Self self;

    public ActorVarAccess(@Nonnull Identifier variableName, Self self) {
        this.variableName = variableName;
        this.self = self;
    }

    public Identifier getVariable() {
        return variableName;
    }

    public void setVariable(Identifier variableName) {
        this.variableName = variableName;
    }

    public Self getSelf() {
        return self;
    }

    @Override
    public String toString() {
         return "ActorVarAccess";
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }}
