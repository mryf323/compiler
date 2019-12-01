package ast.node.statement;

import visitor.Visitor;

import java.util.ArrayList;

public class Block extends Statement
{
    private ArrayList<Statement> statements = new ArrayList<>();

    public ArrayList<Statement> getStatements() {
        return statements;
    }

    public void addStatement(Statement statement) {
        this.statements.add(statement);
    }

    @Override
    public String toString() {
        return "Block";
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }}
