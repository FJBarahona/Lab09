package btree;

public class ItemDuplicated extends Exception {
	public ItemDuplicated() {
		super("El elemento ya existe");
	}
	
}
