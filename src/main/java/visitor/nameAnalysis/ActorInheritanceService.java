package visitor.nameAnalysis;

import ast.node.declaration.ActorDeclaration;
import ast.node.declaration.handler.HandlerDeclaration;
import ast.node.expression.Identifier;
import symbolTable.SymbolTable;
import symbolTable.SymbolTableActorItem;
import symbolTable.itemException.ItemNotFoundException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class ActorInheritanceService {

    private static ActorInheritanceService instance;

    public static ActorInheritanceService getInstance() {
        if (instance == null)
            instance = new ActorInheritanceService();
        return instance;
    }
    private ActorInheritanceService(){

    }

    private Map<Identifier, List<SymbolTableActorItem>> childTransitiveParents = new HashMap<>();
    private List<Identifier> cyclicActors = new ArrayList<>();

    public SymbolTableActorItem findActor(String identifier) throws ItemNotFoundException {
        return (SymbolTableActorItem) SymbolTable.top.get(
                SymbolTableActorItem.STARTKEY + identifier
        );
    }

    private void calculateTransitiveParents(ActorDeclaration actor) {
        List<SymbolTableActorItem> result = new ArrayList<>();
        ActorDeclaration parent = actor;
        while (true){
                if(parent.getParentName() != null){
                    try {
                        SymbolTableActorItem parentSymbolTable = findActor(parent.getParentName().getName());
                        parent = parentSymbolTable.getActorDeclaration();
                        if (parent.getName().equals(actor.getName()))
                            cyclicActors.add(actor.getName());
                        else
                            result.add(parentSymbolTable);
                    } catch (ItemNotFoundException e) {
                        break;
                    }
                }
                else
                    break;

        }
        childTransitiveParents.putIfAbsent(actor.getName(), result);
    }

    public List<SymbolTableActorItem> transitiveParents(ActorDeclaration actor){
        if (!childTransitiveParents.containsKey(actor.getName()))
            calculateTransitiveParents(actor);
        return childTransitiveParents.get(actor.getName());
    }

    public boolean hasCycle(ActorDeclaration actor){

        if (!childTransitiveParents.containsKey(actor.getName()))
            calculateTransitiveParents(actor);
        return cyclicActors.contains(actor.getName());
    }

}
