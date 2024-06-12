package btree;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BTree<E extends Comparable<E>> {
    private BNode<E> root;
    private int orden;
    private boolean up;
    private BNode<E> nDes;

    public BTree(int orden) {
        this.orden = orden;
        this.root = null;
    }

    public boolean isEmpty() {
        return this.root == null;
    }

    public void insert(E cl) {
        up = false;
        E mediana;
        BNode<E> pnew;
        mediana = push(this.root, cl);
        if (up) {
            pnew = new BNode<E>(this.orden);
            pnew.count = 1;
            pnew.keys.set(0, mediana);
            pnew.childs.set(0, this.root);
            pnew.childs.set(1, nDes);
            this.root = pnew;
        }
    }

    private E push(BNode<E> current, E cl) {
        int pos[] = new int[1];
        E mediana;
        if (current == null) {
            up = true;
            nDes = null;
            return cl;
        } else {
            boolean fl;
            fl = current.searchNode(cl, pos);
            if (fl) {
                System.out.println("Item duplicado\n");
                up = false;
                return null;
            }
            mediana = push(current.childs.get(pos[0]), cl);
            if (up) {
                if (current.nodeFull(this.orden)) {
                    mediana = dividedNode(current, mediana, pos[0]);
                } else {
                    up = false;
                    putNode(current, mediana, nDes, pos[0]);
                }
            }
            return mediana;
        }
    }

    private void putNode(BNode<E> current, E cl, BNode<E> rd, int k) {
        int i;
        for (i = current.count - 1; i >= k; i--) {
            current.keys.set(i + 1, current.keys.get(i));
            current.childs.set(i + 2, current.childs.get(i + 1));
        }
        current.keys.set(k, cl);
        current.childs.set(k + 1, rd);
        current.count++;
    }

    private E dividedNode(BNode<E> current, E cl, int k) {
        BNode<E> rd = nDes;
        int i, posMdna;
        posMdna = (k <= this.orden / 2) ? this.orden / 2 : this.orden / 2 + 1;
        nDes = new BNode<E>(this.orden);
        for (i = posMdna; i < this.orden - 1; i++) {
            nDes.keys.set(i - posMdna, current.keys.get(i));
            nDes.childs.set(i - posMdna + 1, current.childs.get(i + 1));
        }
        nDes.count = (this.orden - 1) - posMdna;
        current.count = posMdna;
        if (k <= this.orden / 2) {
            putNode(current, cl, rd, k);
        } else {
            putNode(nDes, cl, rd, k - posMdna);
        }
        E median = current.keys.get(current.count - 1);
        nDes.childs.set(0, current.childs.get(current.count));
        current.count--;
        return median;
    }


    
    public boolean search(E key) throws ItemNoFound {
        return search(root, key);
    }

    private boolean search(BNode<E> node, E key) throws ItemNoFound {
        int i = 0;
        while (i < node.getKeys().size() && key.compareTo(node.getKeys().get(i)) > 0) {
            i++;
        }
        if (i < node.getKeys().size() && key.compareTo(node.getKeys().get(i)) == 0) {
            System.out.println(key + " se encuentra en el nodo " + node.getIdNode() + " en la posición " + (i + 1));
            return true;
        } else if (node.getChilds().isEmpty()) {
            throw new ItemNoFound();
        } else {
            return search(node.getChilds().get(i), key);
        }
    }
    
    public static <E extends Comparable<E>> BTree<E> building_Btree(String filename) throws IOException, ItemDuplicated, ItemNoFound {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            int order = Integer.parseInt(reader.readLine().trim());
            BTree<E> bTree = new BTree<>(order);

            List<BNode<E>> nodes = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                int level = Integer.parseInt(parts[0].trim());
                int id = Integer.parseInt(parts[1].trim());
                List<E> keys = new ArrayList<>();
                for (int i = 2; i < parts.length; i++) {
                    keys.add((E) Integer.valueOf(parts[i].trim()));
                }
                BNode<E> node = new BNode<>(order);
                node.idNode = id;
                node.keys.addAll(keys);
                node.count = keys.size();
                nodes.add(node);

                if (level == 0) {
                    bTree.root = node;
                }
            }

            // Connect nodes according to levels
            for (BNode<E> node : nodes) {
                if (node.idNode != 0) {
                    int parentId = (node.idNode - 1) / order;
                    BNode<E> parent = findNodeById(nodes, parentId);
                    if (parent == null) {
                        throw new IllegalArgumentException("Nodo padre no encontrado para el nodo con ID " + node.idNode);
                    }
                    parent.childs.set(parent.childs.indexOf(null), node);
                }
            }

            // Verifying the tree structure and properties
            if (!bTree.verifyTreeProperties(bTree.root, order)) {
                throw new ItemNoFound();
            }

            return bTree;
        }
    }

    private static <E extends Comparable<E>> BNode<E> findNodeById(List<BNode<E>> nodes, int id) {
        for (BNode<E> node : nodes) {
            if (node.idNode == id) {
                return node;
            }
        }
        return null;
    }

    private boolean verifyTreeProperties(BNode<E> node, int order) {
        if (node == null) {
            return true;
        }
        if (node.count > order - 1 || (node != root && node.count < Math.ceil(order / 2.0) - 1)) {
            return false;
        }
        for (int i = 0; i <= node.count; i++) {
            if (node.childs.get(i) != null && !verifyTreeProperties(node.childs.get(i), order)) {
                return false;
            }
        }
        return true;
    }
    
    public void remove(E key) throws ItemNoFound {
        if (root == null) {
            throw new ItemNoFound();
        }
        remove(root, key);
        if (root.count == 0) {
            if(root.childs.get(0) == null) {
            	root=null;
            }else {
            	root = root.childs.get(0);
            }
        }
    }
    
    private boolean remove(BNode<E> node, E key) throws ItemNoFound {
        int pos[] = new int[1];
        boolean found = node.searchNode(key, pos);

        if (found) {
            // Si el nodo es una hoja
            if (node.childs.get(pos[0]) == null) {
                removeKey(node, pos[0]);
            } else { // Si el nodo no es una hoja
                E pred = getPredecessor(node, pos[0]);
                node.keys.set(pos[0], pred);
                return remove(node.childs.get(pos[0]), pred); // Se elimina el predecesor
            }
        } else {
            // Si el nodo es una hoja y no se encontró la clave
            if (node.childs.get(pos[0]) == null) {
                throw new ItemNoFound(); // La clave no existe en el árbol
            } else { // La clave podría estar en el árbol
                boolean isDeleted = remove(node.childs.get(pos[0]), key); // Búsqueda recursiva
                if (node.childs.get(pos[0]).count < (orden - 1) / 2) {
                    fix(node, pos[0]); // Arreglar si el nodo hijo tiene menos claves de las permitidas
                }
                return isDeleted;
            }
        }
        return true;
    }

    private void removeKey(BNode<E> node, int index) {
        // Eliminar la clave del nodo y desplazar las claves restantes a la izquierda
        for (int i = index; i < node.count - 1; i++) {
            node.keys.set(i, node.keys.get(i + 1));
        }
        node.keys.set(node.count - 1, null); // Limpiar el último elemento
        node.count--; // Decrementar el contador de claves
    }

    private E getPredecessor(BNode<E> node, int index) {
        // Obtener el predecesor de una clave (la clave más grande en el subárbol izquierdo)
        BNode<E> current = node.childs.get(index);
        while (current.childs.get(current.count) != null) {
            current = current.childs.get(current.count);
        }
        return current.keys.get(current.count - 1);
    }

    private void fix(BNode<E> parent, int index) {
        if (index > 0 && parent.childs.get(index - 1).count > (orden - 1) / 2) {
            // Si hay un hermano izquierdo con suficientes claves, realizar redistribución con el izquierdo
            borrowFrontLeft(parent, index);
        } else if (index < parent.count && parent.childs.get(index + 1).count > (orden - 1) / 2) {
            // Si hay un hermano derecho con suficientes claves, realizar redistribución con el derecho
            borrowFrontRight(parent, index);
        } else {
            // Si no se puede redistribuir, realizar una fusión
            if (index > 0) {
                merge(parent, index - 1); // Fusión con el hermano izquierdo
            } else {
                merge(parent, index); // Fusión con el hermano derecho
            }
        }
    }

    private void borrowFrontLeft(BNode<E> parent, int index) {
        BNode<E> left = parent.childs.get(index - 1);
        BNode<E> current = parent.childs.get(index);
        // Mover todas las claves y hijos del nodo actual una posición a la derecha
        for (int i = current.count - 1; i >= 0; i--) {
            current.keys.set(i + 1, current.keys.get(i));
        }
        current.keys.set(0, parent.keys.get(index - 1)); // Mover clave del padre al nodo actual
        parent.keys.set(index - 1, left.keys.get(left.count - 1)); // Mover clave del hermano izquierdo al padre
        left.keys.set(left.count - 1, null); // Limpiar la clave movida en el hermano izquierdo
        if (left.childs.get(left.count) != null) {
            // Mover todos los hijos del nodo actual una posición a la derecha
            for (int i = current.count; i >= 0; i--) {
                current.childs.set(i + 1, current.childs.get(i));
            }
            current.childs.set(0, left.childs.get(left.count)); // Mover el hijo correspondiente del hermano izquierdo
            left.childs.set(left.count, null); // Limpiar el hijo movido en el hermano izquierdo
        }
        current.count++; // Incrementar el contador de claves del nodo actual
        left.count--; // Decrementar el contador de claves del hermano izquierdo
    }

    private void borrowFrontRight(BNode<E> parent, int index) {
        BNode<E> right = parent.childs.get(index + 1);
        BNode<E> current = parent.childs.get(index);

        // Mover la primera clave del hermano derecho al nodo padre
        current.keys.set(current.count, parent.keys.get(index));
        parent.keys.set(index, right.keys.get(0));

        // Mover todas las claves del hermano derecho una posición a la izquierda
        for (int i = 1; i < right.count; i++) {
            right.keys.set(i - 1, right.keys.get(i));
        }
        right.keys.set(right.count - 1, null); // Limpiar el último elemento

        // Mover el primer hijo del hermano derecho al último lugar del nodo actual
        if (right.childs.get(0) != null) {
            current.childs.set(current.count + 1, right.childs.get(0));
            for (int i = 1; i <= right.count; i++) {
                right.childs.set(i - 1, right.childs.get(i));
            }
            right.childs.set(right.count, null); // Limpiar el último hijo
        }

        // Actualizar los contadores
        current.count++;
        right.count--;
    }

    private void merge(BNode<E> parent, int index) {
        BNode<E> leftNode = parent.childs.get(index);
        BNode<E> rightNode = parent.childs.get(index + 1);

        // Mover la clave del padre al final del nodo izquierdo
        leftNode.keys.set(leftNode.count, parent.keys.get(index));

        // Mover todas las claves y hijos del nodo derecho al nodo izquierdo
        for (int i = 0; i < rightNode.count; i++) {
            leftNode.keys.set(leftNode.count + 1 + i, rightNode.keys.get(i));
        }
        if (rightNode.childs.get(0) != null) {
            for (int i = 0; i <= rightNode.count; i++) {
                leftNode.childs.set(leftNode.count + 1 + i, rightNode.childs.get(i));
            }
        }

        // Mover todas las claves y hijos del padre una posición a la izquierda
        for (int i = index + 1; i < parent.count; i++) {
            parent.keys.set(i - 1, parent.keys.get(i));
        }
        for (int i = index + 2; i <= parent.count; i++) {
            parent.childs.set(i - 1, parent.childs.get(i));
        }

        // Actualizar los contadores
        leftNode.count += rightNode.count + 1;
        parent.count--;

        // Limpiar las referencias en el nodo derecho
        for (int i = 0; i < rightNode.count; i++) {
            rightNode.keys.set(i, null);
        }
        for (int i = 0; i <= rightNode.count; i++) {
            rightNode.childs.set(i, null);
        }
        rightNode.count = 0;
    }
    
    public int altBTree()throws ItemNoFound{
    	return altBTree(root);
    }
    
    public int altBTree(BNode<E> nod)throws ItemNoFound {
    	if(nod==null) {
    		throw new ItemNoFound();
    	}
    	if(nod.childs.get(0)==null) {
    		return 1;
    	}else {
    		return 1+altBTree(nod.childs.get(0));
    	}
    }



    public String toString() {
        String str = "Node ID\t Nodes \t\t ID padre\n";
        if(!isEmpty())
            str += toStringRec(this.root,-1);
        return str;
    }

    private String toStringRec(BNode<E> current, int parentId){
        String str = "";
        if(current != null){
            str += current.toString()+"\t\t"+parentId+"\t";
            str += writeTree(current) + "\n";
            for (int i =0; i<= current.count; i++){
                str += toStringRec(current.childs.get(i),current.idNode);
            }
        }
        return str;
    }
    private String writeTree(BNode<E> node) {
        String children = "";
        for (int i = 0; i <= node.count; i++) {
            if (node.childs.get(i) != null) {
                children += node.childs.get(i).idNode;
                if (i < node.count) children += ", ";
            }
        }
        return children;
    }
}
