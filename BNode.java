package btree;

import java.util.ArrayList;

class BNode<E extends Comparable<E>> {
    protected ArrayList<E> keys;
    protected ArrayList<BNode<E>> childs;
    protected int count;
    protected int idNode;
    private static int nodeCounter = 1;

    public BNode(int n) {
        this.keys = new ArrayList<>(n-1);
        this.childs = new ArrayList<>(n); 
        this.count = 0;
        for (int i = 0; i < n; i++) {
            this.keys.add(null);
        }
        for (int i = 0; i <= n; i++) { 
            this.childs.add(null);
        }
        this.idNode = nodeCounter++;
    }
    

    public ArrayList<E> getKeys() {
		return keys;
	}

	public void setKeys(ArrayList<E> keys) {
		this.keys = keys;
	}

	public ArrayList<BNode<E>> getChilds() {
		return childs;
	}



	public void setChilds(ArrayList<BNode<E>> childs) {
		this.childs = childs;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int getIdNode() {
		return idNode;
	}

	public void setIdNode(int idNode) {
		this.idNode = idNode;
	}

	public boolean nodeFull(int i) {
        return count == i-1;
    }

    public boolean nodeEmpty() {
        return count == 0;
    }

    public boolean searchNode(E key, int[] pos) {
        pos[0] = 0;
        while (pos[0] < count && key.compareTo(keys.get(pos[0])) > 0) {
            pos[0]++;
        }
        return pos[0] < count && key.compareTo(keys.get(pos[0])) == 0;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Node ID: ").append(idNode).append(" [");
        for (int i = 0; i < count; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(keys.get(i));
        }
        sb.append("]");
        return sb.toString();
    }
}