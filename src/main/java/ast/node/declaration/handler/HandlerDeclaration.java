package ast.node.declaration.handler;

import ast.node.declaration.Declaration;
import ast.node.declaration.VarDeclaration;
import ast.node.expression.Identifier;
import ast.node.statement.Statement;

import java.util.ArrayList;
import java.util.List;

public abstract class HandlerDeclaration extends Declaration {
    Identifier name;
    private ArrayList<VarDeclaration> args = new ArrayList<>();
    private ArrayList<VarDeclaration> localVars = new ArrayList<>();
    private ArrayList<Statement> body = new ArrayList<>();

    public Identifier getName() {
        return name;
    }

    public void setName(Identifier name) {
        this.name = name;
    }

    public void setArgs(List<VarDeclaration> args) {
        this.args = new ArrayList<VarDeclaration>(args);
    }

    public ArrayList<VarDeclaration> getArgs() {
        return args;
    }

    public void addArg(VarDeclaration arg) {
        this.args.add(arg);
    }

    public void setBody(ArrayList<Statement> body) {
        this.body = body;
    }

    public ArrayList<Statement> getBody() {
        return body;
    }

    public void addStatement(Statement statement) {
        this.body.add(statement);
    }

    public void setLocalVars(ArrayList<VarDeclaration> localVars) {
        this.localVars = localVars;
    }

    public ArrayList<VarDeclaration> getLocalVars() {
        return localVars;
    }

    public void addLocalVar(VarDeclaration localVar) {
        this.localVars.add(localVar);
    }
}
