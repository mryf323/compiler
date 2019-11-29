package ast.node.declaration;

import java.util.ArrayList;

import ast.node.expression.Expression;
import ast.node.expression.Identifier;
import ast.type.Type;
import visitor.Visitor;

public class ActorInstantiation extends VarDeclaration {

    private ArrayList<Identifier> knownActors = new ArrayList<>();
    private ArrayList<Expression> initArgs = new ArrayList<>();

    public ActorInstantiation(Type type, Identifier name){
        super(name, type);
    }

    public ArrayList<Identifier> getKnownActors() {
        return knownActors;
    }

    public void addKnownActor(Identifier actor) {
        this.knownActors.add(actor);
    }

    public void setInitArgs(ArrayList<Expression> initArgs) {
        this.initArgs = initArgs;
    }

    public ArrayList<Expression> getInitArgs() {
        return initArgs;
    }

    public void addInitArg(Expression arg) {
        this.initArgs.add(arg);
    }

    @Override
    public String toString() {
        return "ActorInstantiation";
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
