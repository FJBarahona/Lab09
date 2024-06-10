package Lab09AED;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class BTree<E extends Comparable<E>> {
    private BNode<E> root;
    private int order;
    private boolean up;
    private BNode<E> newChild;

    private final int MIN_DEGREE; // Definición de MIN_DEGREE

    public BTree(int order) {
        this.order = order;
        this.MIN_DEGREE = order / 2; // Inicialización de MIN_DEGREE
        this.root = null;
    }

    public boolean isEmpty() {
        return this.root == null;
    }

    public void insert(E key) throws ItemDuplicated {
        up = false;
        E median;
        BNode<E> newRoot;
        try {
            median = push(this.root, key);
            if (up) {
                newRoot = new BNode<>(this.order);
                newRoot.count = 1;
                newRoot.keys.set(0, median);
                newRoot.childs.set(0, this.root);
                newRoot.childs.set(1, newChild);
                this.root = newRoot;
            }
        } catch (ItemDuplicated e) {
            throw new ItemDuplicated();
        }
    }

    private E push(BNode<E> current, E key) throws ItemDuplicated {
        int[] pos = new int[1];
        E median;
        if (current == null) {
            up = true;
            newChild = null;
            return key;
        } else {
            boolean found;
            found = current.searchNode(key, pos);
            if (found) {
                throw new ItemDuplicated();
            }
            median = push(current.childs.get(pos[0]), key);
            if (up) {
                if (current.nodeFull()) {
                    median = divideNode(current, median, pos[0]);
                } else {
                    up = false;
                    putNode(current, median, newChild, pos[0]);
                }
            }
            return median;
        }
    }

    private void putNode(BNode<E> current, E key, BNode<E> rightChild, int k) {
        for (int i = current.count - 1; i >= k; i--) {
            current.keys.set(i + 1, current.keys.get(i));
            current.childs.set(i + 2, current.childs.get(i + 1));
        }
        current.keys.set(k, key);
        current.childs.set(k + 1, rightChild);
        current.count++;
    }

    private E divideNode(BNode<E> current, E key, int k) {
        BNode<E> rightChild = newChild;
        int medianIndex = (order - 1) / 2;
        newChild = new BNode<>(order);
        for (int i = medianIndex + 1; i < order - 1; i++) {
            newChild.keys.set(i - medianIndex - 1, current.keys.get(i));
            newChild.childs.set(i - medianIndex, current.childs.get(i + 1));
        }
        newChild.count = order - 1 - medianIndex - 1;
        current.count = medianIndex;

        // Insert key based on its value (even if greater than median)
        if (k > medianIndex) {
            putNode(newChild, key, rightChild, k - medianIndex - 1);
        } else {
            putNode(current, key, rightChild, k);
        }

        E median = current.keys.get(medianIndex);
        current.keys.set(medianIndex, null); // Optional: clear the median key
        return median;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-8s %-15s %-10s %-10s%n", "Id.Nodo", "Claves Nodo", "Id.Padre", "Id.Hijos"));
        writeTree(root, sb, -1);
        return sb.toString();
    }

    private void writeTree(BNode<E> current, StringBuilder sb, int parentId) {
        if (current == null)
            return;
        int currentId = current.idNode;
        sb.append(String.format("%-8d %-15s %-10s %-10s%n", currentId, keysToString(current),
                parentId == -1 ? "--" : parentId, getChildIds(current)));
        for (int i = 0; i <= current.count; i++) {
            writeTree(current.childs.get(i), sb, currentId);
        }
    }

    private String keysToString(BNode<E> node) {
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        for (int i = 0; i < node.count; i++) {
            if (i > 0)
                sb.append(", ");
            sb.append(node.keys.get(i));
        }
        sb.append(")");
        return sb.toString();
    }

    private String getChildIds(BNode<E> node) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i <= node.count; i++) {
            if (i > 0)
                sb.append(", ");
            BNode<E> child = node.childs.get(i);
            if (child != null)
                sb.append(child.idNode);
        }
        sb.append("]");
        return sb.toString();
    }

    public boolean search(E key) {
        int[] pos = new int[1];
        BNode<E> node = search(root, key, pos);
        if (node != null) {
            System.out.println(key + " se encuentra en el nodo " + node.idNode + " en la posicion " + pos[0]);
            return true;
        } else {
            return false;
        }
    }

    private BNode<E> search(BNode<E> current, E key, int[] pos) {
        if (current == null)
            return null;
        pos[0] = 0;
        while (pos[0] < current.count && key.compareTo(current.keys.get(pos[0])) > 0) {
            pos[0]++;
        }
        if (pos[0] < current.count && key.compareTo(current.keys.get(pos[0])) == 0) {
            return current;
        }
        return search(current.childs.get(pos[0]), key, pos);
    }












    public static BTree<Integer> building_Btree(String fileName) throws IOException, ItemNoFound {
        BufferedReader reader = new BufferedReader(new FileReader(fileName));
        BTree<Integer> bTree = null;

        try {
            // Leer el orden del árbol desde la primera línea del archivo
            int order = Integer.parseInt(reader.readLine());
            
            bTree = new BTree<>(order);

            // Leer el resto de líneas del archivo
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                int level = Integer.parseInt(parts[0]);
                int idNode = Integer.parseInt(parts[1]);
                String[] keysStr = parts[2].split(",");
                Integer[] keys = new Integer[keysStr.length];
                for (int i = 0; i < keysStr.length; i++) {
                    keys[i] = Integer.parseInt(keysStr[i]);
                }
                bTree.insertFromBuilding(level, idNode, keys);
            }
        } finally {
            if (reader != null) {
                reader.close();
            }
        }

        return bTree;
    }

    private void insertFromBuilding(int level, int idNode, E[] keys) throws ItemNoFound {
        BNode<E> node = new BNode<>(order);
        node.count = keys.length;
        for (int i = 0; i < keys.length; i++) {
            node.keys.set(i, keys[i]);
        }
        node.idNode = idNode;
    
        if (level == 0) {
            if (root == null) {
                root = node;
            } else {
                throw new ItemNoFound();
            }
        } else {
            BNode<E> parent = findParentNode(root, level - 1, idNode);
            if (parent != null) {
                int pos = findPosition(parent, keys[0]);
                if (pos <= parent.count) {
                    putNode(parent, keys[0], node, pos);
                }
            } else {
                throw new ItemNoFound();
            }
        }
    }

    private BNode<E> findParentNode(BNode<E> current, int targetLevel, int targetId) {
        if (current == null || current.childs.get(0) == null) {
            return null;
        }
    
        System.out.println("Current Node ID: " + current.idNode + ", Target ID: " + targetId);
    
        if (current.childs.get(0).idNode == targetId) {
            System.out.println("Found parent: " + current.idNode);
            return current;
        }
    
        for (BNode<E> child : current.childs) {
            if (child != null) {
                BNode<E> parent = findParentNode(child, targetLevel, targetId);
                if (parent != null) {
                    return parent;
                }
            }
        }
    
        return null;
    }
    
















    public void remove(E key) {
        root = remove(root, key);
    }

    private BNode<E> remove(BNode<E> current, E key) {
        if (current == null)
            return null;

        int pos = findPosition(current, key);

        // Si la clave está en este nodo
        if (pos < current.count && key.compareTo(current.keys.get(pos)) == 0) {
            if (current.childs.get(0) == null) {
                removeFromLeaf(current, pos);
            } else {
                replaceAndRemove(current, pos);
            }
        } else {
            BNode<E> child = current.childs.get(pos);
            if (child != null) {
                current.childs.set(pos, remove(child, key));
            }
        }

        // Realizar redistribución o fusión si es necesario
        if (current != null) {
            if (current.childs.get(0) != null) {
                current = redistributeOrMerge(current, pos);
            }
        }

        return current;
    }

    private int findPosition(BNode<E> node, E key) {
        int pos = 0;
        while (pos < node.count && node.keys.get(pos) != null && key.compareTo(node.keys.get(pos)) > 0) {
            pos++;
        }
        return pos;
    }

    private void removeFromLeaf(BNode<E> node, int pos) {
        for (int i = pos + 1; i < node.count; ++i) {
            node.keys.set(i - 1, node.keys.get(i));
        }

        node.count--;
    }

    private void replaceAndRemove(BNode<E> node, int pos) {
        E pred = getPredecessor(node, pos);
        node.keys.set(pos, pred);
        node.childs.set(pos, remove(node.childs.get(pos), pred));
    }

    private E getPredecessor(BNode<E> node, int pos) {
        BNode<E> current = node.childs.get(pos);
        while (current.childs.get(current.count) != null) {
            current = current.childs.get(current.count);
        }
        return current.keys.get(current.count - 1);
    }

    private BNode<E> redistributeOrMerge(BNode<E> node, int pos) {
        BNode<E> child = node.childs.get(pos);
        BNode<E> leftSibling = pos > 0 ? node.childs.get(pos - 1) : null;
        BNode<E> rightSibling = pos < node.count ? node.childs.get(pos + 1) : null;

        if (leftSibling != null && leftSibling.count > MIN_DEGREE) {
            // Redistribución con el hermano izquierdo
            rotateFromLeft(node, leftSibling, child, pos - 1);
        } else if (rightSibling != null && rightSibling.count > MIN_DEGREE) {
            // Redistribución con el hermano derecho
            rotateFromRight(node, child, rightSibling, pos);
        } else if (leftSibling != null) {
            // Fusion con el hermano izquierdo
            mergeNodes(node, leftSibling, child, pos - 1);
        } else {
            // Fusión con el hermano derecho
            mergeNodes(node, child, rightSibling, pos);
        }

        return node;
    }

    private void rotateFromLeft(BNode<E> parent, BNode<E> leftSibling, BNode<E> child, int pos) {
        // Mover la clave del padre al hijo
        child.keys.add(0, parent.keys.get(pos));
        parent.keys.set(pos, leftSibling.keys.get(leftSibling.count - 1));

        // Mover el último hijo del hermano izquierdo al hijo
        if (child.childs.get(0) != null) {
            child.childs.add(0, leftSibling.childs.get(leftSibling.count));
        }

        // Ajustar el recuento
        child.count++;
        leftSibling.count--;

        // Mover las claves y los hijos del hermano izquierdo
        leftSibling.keys.remove(leftSibling.count);
        if (leftSibling.childs.get(0) != null) {
            leftSibling.childs.remove(leftSibling.count + 1);
        }
    }

    private void rotateFromRight(BNode<E> parent, BNode<E> child, BNode<E> rightSibling, int pos) {
        // Mover la clave del padre al hijo
        child.keys.add(parent.keys.get(pos));
        parent.keys.set(pos, rightSibling.keys.get(0));

        // Mover el primer hijo del hermano derecho al hijo
        if (child.childs.get(0) != null) {
            child.childs.add(rightSibling.childs.get(0));
        }

        // Ajustar el recuento
        child.count++;
        rightSibling.count--;

        // Mover las claves y los hijos del hermano derecho
        rightSibling.keys.remove(0);
        if (rightSibling.childs.get(0) != null) {
            rightSibling.childs.remove(0);
        }
    }

    private void mergeNodes(BNode<E> parent, BNode<E> leftChild, BNode<E> rightChild, int pos) {
        // Mover la clave del padre al hijo izquierdo
        leftChild.keys.add(parent.keys.get(pos));

        // Mover las claves y los hijos del hijo derecho al hijo izquierdo
        leftChild.keys.addAll(rightChild.keys);
        if (leftChild.childs.get(0) != null) {
            leftChild.childs.addAll(rightChild.childs);
        }

        // Eliminar el hijo derecho
        parent.keys.remove(pos);
        parent.childs.remove(rightChild);

        // Ajustar el recuento
        leftChild.count += rightChild.count + 1;
    }

}