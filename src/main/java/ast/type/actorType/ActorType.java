package ast.type.actorType;

import ast.node.declaration.ActorDeclaration;
import ast.node.expression.Identifier;
import ast.type.Type;
import visitor.Visitor;

public class ActorType extends Type {
    private Identifier name;

    public ActorType(Identifier name) {
        this.name = name;
    }

    public ActorType(ActorDeclaration actorDeclaration) {
        this.name = actorDeclaration.getName();
    }

    public Identifier getName() {
        return name;
    }

    public void setName(Identifier name) {
        this.name = name;
    }

    @Override
    public String toString() {
        //return classDeclaration.getName().getName();
        return this.name.getName();
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
