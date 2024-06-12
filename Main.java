public class Main {
    public static void main(String[] args) {
        BTree<Integer> bTree = new BTree<>(3);
        try {
            bTree.insert(5);
            bTree.insert(6);
            bTree.insert(12);
            bTree.insert(7);
            bTree.insert(17);
            
            // Probando el método search
            System.out.println("Search for key 7: " + bTree.search(7));
            System.out.println("Search for key 17: " + bTree.search(17));
            System.out.println("Search for key 20: " + bTree.search(20));
            
            System.out.println("Before removal:");
            System.out.println(bTree);
            
            // Eliminando algunas claves
            bTree.remove(7);
            bTree.remove(12);
            bTree.remove(20); // Intentando eliminar una clave que no está en el árbol
            
            System.out.println("After removal:");
            System.out.println(bTree);
        } catch (ItemDuplicated e) {
            System.out.println(e.getMessage());
        }
    }
}



