package btree;

import java.io.IOException;

public class Main {

	public static void main(String[] args) {
		BTree<Integer> bTree = new BTree<>(3);
		BTree<Integer> bTree2 = new BTree<>(4);
        bTree.insert(10);
        bTree.insert(20);
        bTree.insert(5);
        bTree.insert(6);
        bTree.insert(12);
        bTree.insert(30);
        bTree.insert(7);
        bTree.insert(17);
        System.out.println(bTree.toString());
        
        try {
        	bTree.remove(20);
        	System.out.println(bTree.altBTree());
        	System.out.println(bTree2.building_Btree("D:/arbolB.txt"));
        }catch(IOException | ItemDuplicated | ItemNoFound e) {
        	System.out.println(e.getMessage());
        	
        }

        
	}

}
