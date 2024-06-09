public class Main {
    public static void main(String[] args) {
        BTree<Integer> bTree = new BTree<>(4);

        // Claves para insertar en el árbol
        int[] keys = {31, 19, 12, 41, 57, 63, 3, 10, 13, 16, 22, 25, 28, 33, 38, 40, 49, 52, 55, 60, 62, 67, 70, 72};

        // Insertar las claves en el árbol
        for (int key : keys) {
            bTree.insert(key);
        }

        // Mostrar el árbol
        System.out.println(bTree.toString());
    }
}
