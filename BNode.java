import java.util.ArrayList;

public class BNode<E extends Comparable<E>> {
    protected ArrayList<E> keys;
    protected ArrayList<BNode<E>> childs;
    protected int count;
    protected static int idCounter = 0; // Atributo estático para llevar la cuenta de los nodos creados
    protected int idNode; // Identificador del nodo

    public BNode(int n) {
        this.keys = new ArrayList<E>(n);
        this.childs = new ArrayList<BNode<E>>(n);
        this.count = 0;
        this.idNode = idCounter++; // Asignamos el id y luego incrementamos el contador
        for (int i = 0; i < n; i++) {
            this.keys.add(null);
            this.childs.add(null);
        }
    }

    // Verifica si el nodo está lleno
    public boolean nodeFull() {
        return this.count == this.keys.size();
    }

    public boolean nodeFull(int n) {
        return this.count == n;
    }

    // Verifica si el nodo está vacío
    public boolean nodeEmpty() {
        return this.count == 0;
    }

    // Busca una clave en el nodo actual
    public boolean searchNode(E key) {
        return this.keys.contains(key);
    }

    public boolean searchNode(E key, int[] pos) {
        for (int i = 0; i < this.count; i++) {
            if (this.keys.get(i).compareTo(key) == 0) {
                pos[0] = i;
                return true;
            } else if (this.keys.get(i).compareTo(key) > 0) {
                pos[0] = i;
                return false;
            }
        }
        pos[0] = this.count;
        return false;
    }

    // Devuelve las claves encontradas en el nodo y el idNode
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("idNode: ").append(this.idNode).append(", keys: [");
        for (int i = 0; i < this.count; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(this.keys.get(i));
        }
        sb.append("]");
        return sb.toString();
    }
}