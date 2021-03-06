package ast.node.declaration;

import ast.node.declaration.handler.InitHandlerDeclaration;
import ast.node.declaration.handler.MsgHandlerDeclaration;
import ast.node.expression.Identifier;
import visitor.Visitor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ActorDeclaration extends Declaration {
    private ArrayList<VarDeclaration> knownActors = new ArrayList<>();
    private ArrayList<VarDeclaration> actorVars = new ArrayList<>();
    private ArrayList<MsgHandlerDeclaration> msgHandlers = new ArrayList<>();

    @Nullable
    private InitHandlerDeclaration initHandler;
    private Identifier name;
    @Nullable
    private Identifier parentName;
    private int queueSize;

    public ActorDeclaration(Identifier name){
        this.name = name;
    }

    public void setName(Identifier name) {
        this.name = name;
    }

    public List<VarDeclaration> getKnownActors() {
        return knownActors;
    }

    public void addKnownActor(VarDeclaration knownActor){
        this.knownActors.add(knownActor);
    }

    public void setActorVars(ArrayList<VarDeclaration> actorVars) {
        this.actorVars = actorVars;
    }

    public ArrayList<VarDeclaration> getActorVars() {
        return actorVars;
    }

    public void addActorvar(VarDeclaration actorVar){
        this.actorVars.add(actorVar);
    }

    public ArrayList<MsgHandlerDeclaration> getMsgHandlers() {
        return this.msgHandlers;
    }

    public void addMsgHandler(MsgHandlerDeclaration msgHandler){
        this.msgHandlers.add(msgHandler);
    }

    @Nullable
    public InitHandlerDeclaration getInitHandler() {
        return initHandler;
    }

    public void setInitHandler(@Nullable InitHandlerDeclaration initHandler) {
        this.initHandler = initHandler;
    }

    public Identifier getName() {
        return name;
    }

    @Nullable
    public Identifier getParentName() {
        return parentName;
    }

    public void setParentName(@Nonnull Identifier name) {
        this.parentName = name;
    }

    public int getQueueSize() {
        return queueSize;
    }

    public void setQueueSize(int size) {
        this.queueSize = size;
    }

    @Override
    public String toString() {
        return "ActorDeclaration";
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }

}
