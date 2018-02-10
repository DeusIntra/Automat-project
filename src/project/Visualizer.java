package project;

import java.awt.Graphics;
import javax.swing.*;
import java.util.ArrayList;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.atan2;
import static java.lang.Math.ceil;
import static java.lang.Math.PI;


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
    
    public void setEnter(int x, int y) {
        
    }
    
    public void setExit(int x, int y) {
        
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
    
    // Возвращает true если точка ближе 
    // чем на минимальном расстоянии от одного из узлов
    private boolean tooClose(int x, int y) {
        for(int i = 0; i < nodes.size(); i++) {
            int node_x = nodes.get(i).getX();
            int node_y = nodes.get(i).getY();
            
            double distance = Math.hypot((x - node_x), (y - node_y));
            
            if (distance < MIN_DIST) return true;
        }
        return false;
    }
    
    // Рисует стрелочку с началом в точке (x1, y1) и с концом в точке (x2, y2)
    private void drawArrow(Graphics g, int x1, int y1, int x2, int y2) {
        double alpha = atan2((x2 - x1), (y2 - y1));
        int wing_sin = (int)ceil(sin(PI/4 - alpha) * 7);
        int wing_cos = (int)ceil(cos(PI/4 - alpha) * 7);
        
        g.drawLine(x1, y1, x2, y2);
        g.drawLine(x2, y2, x2+wing_sin, y2-wing_cos);
        g.drawLine(x2, y2, x2-wing_cos, y2-wing_sin);
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
                
                // Чтобы линия начиналась не из центра
                double alpha = atan2((other_x - x), (other_y - y));
                int dimin_x = (int)ceil(sin(alpha) * node_diam);
                int dimin_y = (int)ceil(cos(alpha) * node_diam);
                
                int x1 = x+dimin_x;
                int y1 = y+dimin_y;
                int x2 = other_x-dimin_x;
                int y2 = other_y-dimin_y;
                
                drawArrow(g, x1, y1, x2, y2);
            }
            
        }
        
    }
}
