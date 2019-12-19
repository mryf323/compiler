package visitor.typeAnalysis;

import ast.node.declaration.ActorDeclaration;
import symbolTable.SymbolTable;
import symbolTable.SymbolTableActorItem;
import symbolTable.SymbolTableHandlerItem;
import symbolTable.SymbolTableItem;
import symbolTable.itemException.ItemNotFoundException;
import visitor.nameAnalysis.ActorInheritanceService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class InheritedSymbolTableFiller {

    private final SymbolTable global;

    private static final ActorInheritanceService inheritanceService = ActorInheritanceService.getInstance();

    public InheritedSymbolTableFiller(SymbolTable global) {
        this.global = global;
    }

    public void fill(List<ActorDeclaration> actorDeclarations) { try {
        SymbolTable.push(global);
        for (ActorDeclaration actorDec : actorDeclarations) {
            SymbolTable symbolTable = ((SymbolTableActorItem)
                    SymbolTable.top.get(SymbolTableActorItem.STARTKEY + actorDec.getName().getName())).getActorSymbolTable();

            Map<String, SymbolTableItem> transitiveItems = inheritanceService.transitiveParents(actorDec)
                    .stream().flatMap(parent ->
                            parent.getActorSymbolTable().getSymbolTableItems().entrySet().stream()
                                    .filter(item ->
                                            !(item.getValue() instanceof SymbolTableHandlerItem) ||
                                                    !((SymbolTableHandlerItem) item.getValue())
                                                            .getHandlerDeclaration().getName().getName().equals("initial")
                                    )
                    ).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

            symbolTable.getSymbolTableItems().putAll(transitiveItems);

            SymbolTable.push(symbolTable);
        }
        SymbolTable.pop();
    } catch (ItemNotFoundException ignored) {}}
}
