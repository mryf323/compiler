package visitor;

import symbolTable.SymbolTableActorItem;
import symbolTable.SymbolTableHandlerItem;
import symbolTable.SymbolTableMainItem;
import symbolTable.symbolTableVariableItem.*;

public interface SymbolTableItemVisitor<T> {

    T visit(SymbolTableActorItem item);
    T visit(SymbolTableHandlerItem item);
    T visit(SymbolTableMainItem item);
    T visit(SymbolTableActorVariableItem item);
    T visit(SymbolTableHandlerArgumentItem item);
    T visit(SymbolTableKnownActorItem item);
    T visit(SymbolTableLocalVariableItem item);
}
