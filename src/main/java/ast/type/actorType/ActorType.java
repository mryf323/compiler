package ast.type.actorType;

import ast.type.Type;
import ast.node.declaration.ActorDeclaration;
import ast.node.expression.Identifier;
import visitor.Visitor;

public class ActorType extends Type {
    private ActorDeclaration actorDeclaration;
    private Identifier name;

    public ActorType(Identifier name) {
        this.name = name;
    }

    public ActorDeclaration getActorDeclaration() {
        return actorDeclaration;
    }

    public void setActorDeclaration(ActorDeclaration actorDeclaration) {
        this.actorDeclaration = actorDeclaration;
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
