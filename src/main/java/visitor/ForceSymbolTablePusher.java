package visitor;

import ast.node.expression.Identifier;
import org.apache.commons.lang3.StringUtils;
import symbolTable.SymbolTable;
import symbolTable.SymbolTableItem;
import symbolTable.itemException.ItemAlreadyExistsException;

public abstract class ForceSymbolTablePusher<T extends SymbolTableItem> {

    private static String REDUNDANT_PREFIX = "#";

    public static String strip(String string){
        return StringUtils.strip(string, REDUNDANT_PREFIX);
    }
    public static boolean isUnified(String string){
        return string.startsWith(REDUNDANT_PREFIX);
    }

    protected Identifier unifyRedundantIdentifier(Identifier identifier) {
        return new Identifier(REDUNDANT_PREFIX + identifier.getName());
    }


    protected abstract T push();

    protected abstract T onExistence();

    public T forcePush(SymbolTable symbolTable) {
        T item = push();
        boolean success = false;
        do {
            try {
                symbolTable.put(item);
                success = true;
            } catch (ItemAlreadyExistsException e) {
                item = onExistence();
            }
        }while (!success);
        return item;
    }
}
