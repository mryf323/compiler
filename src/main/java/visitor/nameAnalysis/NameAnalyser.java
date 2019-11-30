package visitor.nameAnalysis;

import symbolTable.SymbolTable;
import visitor.SymbolTableItemVisitor;

public class NameAnalyser {

    private SymbolTableItemVisitor<Boolean> visitor = new NameAnalyserSymbolTableItemVisitor();

    public boolean analyse(SymbolTable symbolTable) {
        return symbolTable.getSymbolTableItems().values().stream()
                .map(item -> item.accept(visitor))
                .reduce(true, (i,j)-> i && j);
    }
}
