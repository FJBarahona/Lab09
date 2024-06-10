package Lab09AED;

import java.io.IOException;

public class Test {
    public static void main(String[] args) {
        try {
            BTree<Integer> bTree = BTree.building_Btree("D:/UCSM Ingenieria de sistemas/Semestre V/Algoritmos y Estructura de Datos/lab07/Lab09AED/arbolB.txt");

            System.out.println("Árbol BTree construido desde el archivo:");
            System.out.println(bTree);
        } catch (IOException e) {
            System.out.println("Error al leer el archivo: " + e.getMessage());
        } catch (ItemNoFound e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
