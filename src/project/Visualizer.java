package project;

import java.awt.Graphics;
import javax.swing.*;
import java.util.ArrayList;


public class Visualizer extends JPanel {
    
    private ArrayList<Node> nodes;  // Расширяемый массив узлов
    private ArrayList<int[]> connections;
    private final int node_diam;    // Диаметр узлов
    public final double MIN_DIST;   // Минимальное расстояние между узлами
    private int currentElemIndex;   // Индекс активного элемента
//    private int x_offset, y_offset; // Отклонение для перемещения вида
    
    public Visualizer(int diam) {
        nodes = new ArrayList<>();
        node_diam = diam;
        MIN_DIST = node_diam * 1.5;
        currentElemIndex = -1;
    }
    
    // Добавляет узел
    public void addElem(int x, int y) {
        // Не позволяет добавить элемент если расстояние
        // до другого элемента слишком маленькое
        if (!tooClose(x, y))
            // Добавление узла с центром в точке (x, y)
            this.nodes.add(new Node(x, y)); // minus offset
    }
    
    // Удаляет узел
    public void removeElem(int x, int y) {
        int index = getElemIndexAt(x, y);
        if (index != -1) nodes.remove(index);
    }   
    
    // Двигает узел
    public void moveElem(int x, int y) {
        if (currentElemIndex == -1)
            currentElemIndex = getElemIndexAt(x, y);
        if (currentElemIndex != -1) {
            Node node = nodes.get(currentElemIndex).set(x, y);
            nodes.set(currentElemIndex, node);
        }
    }
    
    // Фиксирует узел. Использовать после moveElem,
    // чтобы выбрать другой узел для перемещения
    public void fixElem(int x, int y) {
        if (currentElemIndex != -1) {
            Node node = nodes.get(currentElemIndex).set(x, y);
            nodes.set(currentElemIndex, node);
            currentElemIndex = -1;
        }
    }
    
    public void addArrow(int x, int y) {
        if (currentElemIndex == -1)
            currentElemIndex = getElemIndexAt(x, y);
    }
    
    // Возвращает индекс элемента, внутри которого есть точка (x, y)
    private int getElemIndexAt(int x, int y) {
                        
        for(int i = 0; i < nodes.size(); i++) {
            
            Node node = nodes.get(i);
            
            int node_x = node.getX();
            int node_y = node.getY();
            
            double distance = Math.hypot((x - node_x), (y - node_y));
            
            if (distance < node_diam / 2) return i;
        }
        return -1;
    }
    
    private boolean tooClose(int x, int y) {
        for(int i = 0; i < nodes.size(); i++) {
            int node_x = nodes.get(i).getX();
            int node_y = nodes.get(i).getY();
            
            double distance = Math.hypot((x - node_x), (y - node_y));
            
            if (distance < MIN_DIST) return true;
        }
        return false;
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        
        super.paintComponent(g); // Рисует панель
        
        // Рисует все узлы
        for(int i = 0; i < nodes.size(); i++) {
            int x = nodes.get(i).getX() - (node_diam / 2);
            int y = nodes.get(i).getY() - (node_diam / 2);
            
            g.drawOval(x, y, node_diam, node_diam); // plus offset
        }
        
    }
}
