package project;

import java.awt.Graphics;
import javax.swing.*;
import java.util.ArrayList;


public class Visualizer extends JPanel {
    
    private ArrayList<Node> nodes;  // Расширяемый массив узлов    
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
    
    // Двигает узел (пока зажата кнопка мыши)
    public void moveElem(int x, int y) {
        if (currentElemIndex == -1)
            currentElemIndex = getElemIndexAt(x, y);
        if (currentElemIndex != -1) {
            Node node = nodes.get(currentElemIndex).set(x, y);
            nodes.set(currentElemIndex, node);
        }
    }
    
    // Фиксирует узел. Использовать после moveElem (когда кнопка мыши отпущена),
    // чтобы выбрать другой узел для перемещения
    public void fixElem(int x, int y) {
        if (currentElemIndex != -1) {
            Node node = nodes.get(currentElemIndex).set(x, y);
            nodes.set(currentElemIndex, node);
            // Конец работы с активным узлом
            currentElemIndex = -1;
        }
    }
    
    // В режиме добавления соединения при нажатии кнопки мыши
    public void addArrow(int x, int y) {
        if (currentElemIndex == -1)
            currentElemIndex = getElemIndexAt(x, y);
        if (currentElemIndex != -1) {
            Node node = nodes.get(currentElemIndex);
            // Соединение с "анонимным" узлом, которого не существует
            node.connectTo(new Node(x, y));
            nodes.set(currentElemIndex, node);
        }
    }
    
    // В режиме добавления соединения при перемещении мыши
    public void moveArrow(int x, int y) {
        if (currentElemIndex != -1) {
            Node node = nodes.get(currentElemIndex);
            // Удаление "анонимного" узла, подключение к другому "анонимному"
            node.disconnectLast();
            node.connectTo(new Node(x, y));
            nodes.set(currentElemIndex, node);
        }
    }
    
    public void fixArrow(int x, int y) {
        if (currentElemIndex != -1) {
            Node node = nodes.get(currentElemIndex);
            // Удаление "анонимного" узла происходит в любом случае
            node.disconnectLast();
            
            // Если кнопка мыши была отпущена на существующем узле
            int otherNodeIndex = getElemIndexAt(x, y);
            if (otherNodeIndex != -1) {
                //Подключение к существующему узлу
                Node otherNode = nodes.get(otherNodeIndex);
                node.connectTo(otherNode);
                nodes.set(currentElemIndex, node);
            }
            // Конец работы с активным узлом
            currentElemIndex = -1;
        }
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
            
            Node node  = nodes.get(i);
            
            // Центральная точка
            int x = node.getX();
            int y = node.getY();
            
            // Левый верхний угол квадрата, в который вписан узел
            int x_top_left = x - (node_diam / 2);
            int y_top_left = y - (node_diam / 2);
            
            // Отрисовка узла
            g.drawOval(x_top_left, y_top_left, node_diam, node_diam); // plus offset
            
            // Отрисовка соединений
            Node[] conns = node.getConnections();
            for (Node conn : conns) {
                int other_x = conn.getX();
                int other_y = conn.getY();
                
                g.drawLine(x, y, other_x, other_y);
            }
            
        }
        
    }
}
