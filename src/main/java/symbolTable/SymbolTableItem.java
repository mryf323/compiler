package symbolTable;

import visitor.SymbolTableItemVisitor;

public abstract class SymbolTableItem {
	protected String name;

	public SymbolTableItem() {
	}

	public abstract String getKey();
	public abstract <T> T accept(SymbolTableItemVisitor<T> visitor);
}