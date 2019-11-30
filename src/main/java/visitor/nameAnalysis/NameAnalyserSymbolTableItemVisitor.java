package visitor.nameAnalysis;

import ast.node.declaration.VarDeclaration;
import symbolTable.SymbolTable;
import symbolTable.SymbolTableActorItem;
import symbolTable.SymbolTableHandlerItem;
import symbolTable.SymbolTableMainItem;
import symbolTable.symbolTableVariableItem.SymbolTableActorVariableItem;
import symbolTable.symbolTableVariableItem.SymbolTableHandlerArgumentItem;
import symbolTable.symbolTableVariableItem.SymbolTableKnownActorItem;
import symbolTable.symbolTableVariableItem.SymbolTableLocalVariableItem;
import visitor.ForceSymbolTablePusher;
import visitor.SymbolTableItemVisitor;

public class NameAnalyserSymbolTableItemVisitor implements SymbolTableItemVisitor<Boolean> {

    private ActorInheritanceService inheritanceService = ActorInheritanceService.getInstance();

    private SymbolTableActorItem actor;

    private NameAnalyserVisitor astVisitor = new NameAnalyserVisitor();

    private boolean mapReduceSymbolTable(SymbolTable symbolTable, boolean redInit) {
        return symbolTable.getSymbolTableItems().values().stream()
                .map(i -> i.accept(this))
                .reduce(redInit, (i, j) -> i && j);
    }

    @Override
    public Boolean visit(SymbolTableActorItem item) {
        boolean correct = item.getActorDeclaration().accept(astVisitor);
        SymbolTable.push(item.getActorSymbolTable());
        actor = item;
        correct &= mapReduceSymbolTable(SymbolTable.top, correct);
        SymbolTable.pop();
        return correct;
    }

    @Override
    public Boolean visit(SymbolTableHandlerItem item) {
        boolean correct = true;
        String name = item.getHandlerDeclaration().getName().getName();
        if (ForceSymbolTablePusher.isUnified(name)) {
            name = ForceSymbolTablePusher.strip(name);
            correct = false;
        }
        boolean transitiveRedefinition = inheritanceService.transitiveParents(actor.getActorDeclaration())
                .stream().anyMatch(p -> p.getActorSymbolTable().containsKey(item.getKey()));
        if (transitiveRedefinition && !name.equals("initial"))
            correct = false;
        if (!correct)
            System.out.printf(
                    "%d:ErrorItemMessage: Redefinition of msghandler %s%n",
                    item.getHandlerDeclaration().getName().getLine(), name
            );

        SymbolTable.push(item.getHandlerSymbolTable());
        correct &= mapReduceSymbolTable(SymbolTable.top, correct);
        correct &= item.getHandlerDeclaration().getBody().stream()
                .map(i -> i.accept(astVisitor))
                .reduce(correct, (i, j) -> i && j);
        SymbolTable.pop();
        return correct;
    }

    @Override
    public Boolean visit(SymbolTableMainItem item) {

        boolean correct = item.getProgramMain().accept(astVisitor);
        SymbolTable.push(item.getMainSymbolTable());
        correct &= item.getProgramMain().getMainActors().stream()
                .map(ins -> ins.accept(astVisitor))
                .reduce(correct, (i, j) -> i && j);
        SymbolTable.pop();
        return correct;
    }


    private boolean visitMembers(VarDeclaration varDec, String itemKey) {

        boolean correct = true;
        String name = varDec.getIdentifier().getName();
        if (ForceSymbolTablePusher.isUnified(name)) {
            name = ForceSymbolTablePusher.strip(name);
            correct = false;
        }
        boolean transitiveRedefinition = inheritanceService.transitiveParents(actor.getActorDeclaration())
                .stream().anyMatch(p -> p.getActorSymbolTable().containsKey(itemKey));
        if (transitiveRedefinition) {
            correct = false;
        }
        if (!correct)
            System.out.printf(
                    "%d:ErrorItemMessage: Redefinition of variable %s%n",
                    varDec.getIdentifier().getLine(), name
            );
        return correct;
    }

    @Override
    public Boolean visit(SymbolTableActorVariableItem item) {
        return visitMembers(item.getVarDeclaration(), item.getKey());
    }

    @Override
    public Boolean visit(SymbolTableHandlerArgumentItem item) {
        return item.getVarDeclaration().accept(astVisitor);
    }

    @Override
    public Boolean visit(SymbolTableKnownActorItem item) {
        return visitMembers(item.getVarDeclaration(), item.getKey());
    }

    @Override
    public Boolean visit(SymbolTableLocalVariableItem item) {
        return item.getVarDeclaration().accept(astVisitor);
    }

}
