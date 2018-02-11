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
    private int enterIndex;         // Индекс узла, являющегося входом
    private int offset_x, offset_y; // Отклонение для перемещения вида
    
    public Visualizer(int diam) {
        nodes = new ArrayList<>();
        node_diam = diam;
        MIN_DIST = node_diam * 1.5;
        currentElemIndex = -1;
        enterIndex = -1;
        offset_x = 0;
        offset_y = 0;        
    }
    
    // Добавляет узел
    public void addElem(int x, int y) {
        // Не позволяет добавить элемент если расстояние
        // до другого элемента слишком маленькое
        if (!tooClose(x, y))
            // Добавление узла с центром в точке (x, y)
            this.nodes.add(new Node(x-offset_x, y-offset_y));
    }
    
    // Удаляет узел и соединения других узлов с ним
    public void removeElem(int x, int y) {
        int index = getElemIndexAt(x, y);
        if (index != -1) {
            // Убрать входной узел, если есть
            if (enterIndex == index) enterIndex = -1;
            
            // Убрать соединения других элементов с данным
            Node other_node;
            Node[] other_conns;
            // Проверка каждого элемента
            for(int i = 0; i < nodes.size(); i++) {
                other_node = nodes.get(i);
                other_conns = other_node.getConnections();
                
                // Проверка каждого соединения
                for (int j = 0; j < other_conns.length; j++) {
                    int check_x = other_conns[j].getX();
                    int check_y = other_conns[j].getY();
                    int check_index = getElemIndexAt(check_x, check_y);
                    
                    // Если проверяемый элемент совпадает с удаляемым
                    if (check_index == index) {
                        other_node.disconnectFrom(j);
                        nodes.set(i, other_node);
                    }
                }
            }
            
            // Убрать сам узел
            nodes.remove(index);
        }
    }   
    
    // Двигает узел (пока зажата кнопка мыши)
    public void moveElem(int x, int y) {
        if (currentElemIndex == -1)
            currentElemIndex = getElemIndexAt(x, y);
        if (currentElemIndex != -1) {
            Node node = nodes.get(currentElemIndex);
            node.set(x - offset_x, y - offset_y);
            nodes.set(currentElemIndex, node);
        }
    }
    
    // Фиксирует узел. Использовать после moveElem (когда кнопка мыши отпущена),
    // чтобы выбрать другой узел для перемещения
    public void fixElem(int x, int y) {
        if (currentElemIndex != -1) {
            Node node = nodes.get(currentElemIndex);
            node.set(x - offset_x, y - offset_y);
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
            node.connectTo(new Node(x - offset_x, y - offset_y));
            nodes.set(currentElemIndex, node);
        }
    }
    
    // В режиме добавления соединения при перемещении мыши
    public void moveArrow(int x, int y) {
        if (currentElemIndex != -1) {
            Node node = nodes.get(currentElemIndex);
            // Удаление "анонимного" узла, подключение к другому "анонимному"
            node.disconnectLast();
            node.connectTo(new Node(x - offset_x, y - offset_y));
            nodes.set(currentElemIndex, node);
        }
    }
    
    // В режиме добавления соединения при отпускании мыши
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
    
    // Устанавливает вход в одном из узлов
    public void setEnter(int x, int y) {
        int index = getElemIndexAt(x, y);
        if (index != -1) {
            Node node;
            // Вход может быть только один, поэтому
            // если вход уже существует, убрать вход
            if (enterIndex != -1) {
                node = nodes.get(enterIndex);
                node.enter = false;
                nodes.set(enterIndex, node);
            }
            // Установить новый вход
            node = nodes.get(index);
            node.enter = true;
            nodes.set(index, node);
            enterIndex = index;
        }
    }
    
    // Устанавливает/убирает выход в выбранном узле
    public void setExit(int x, int y) {
        int index = getElemIndexAt(x, y);
        if (index != -1) {
            Node node = nodes.get(index);
            node.exit = !node.exit;
            nodes.set(index, node);
        }
    }
    
    public void setOffset(int offset_x, int offset_y) {
        this.offset_x = offset_x;
        this.offset_y = offset_y;
    }
    
    // Возвращает индекс элемента, внутри которого есть точка (x, y)
    private int getElemIndexAt(int x, int y) {
                        
        for(int i = 0; i < nodes.size(); i++) {
            
            Node node = nodes.get(i);
            
            int node_x = node.getX() + offset_x;
            int node_y = node.getY() + offset_y;
            
            double distance = Math.hypot((x - node_x), (y - node_y));
            
            if (distance < node_diam / 2) return i;
        }
        return -1;
    }
    
    public String getOffset() {
        return "" + offset_x + " " + offset_y;
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
        int left_wing_sin = (int)ceil(sin(PI/6 - alpha) * 7);
        int left_wing_cos = (int)ceil(cos(PI/6 - alpha) * 7);
        
        int right_wing_sin = (int)ceil(sin(PI/6 + alpha) * 7);
        int right_wing_cos = (int)ceil(cos(PI/6 + alpha) * 7);
        
        // Основание стрелки
        g.drawLine(x1, y1, x2, y2);
        // Левое крыло
        g.drawLine(x2, y2, x2+left_wing_sin, y2-left_wing_cos);
        // Правое крыло
        g.drawLine(x2, y2, x2-right_wing_sin, y2-right_wing_cos);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        
        super.paintComponent(g); // Рисует панель
        
        // Рисует все узлы
        Node node;
        for(int i = 0; i < nodes.size(); i++) {
            
            node = nodes.get(i);
            
            // Центральная точка
            int x = node.getX();
            int y = node.getY();
            
            // Левый верхний угол квадрата, в который вписан узел
            int x_top_left = x - (node_diam / 2) + offset_x;
            int y_top_left = y - (node_diam / 2) + offset_y;
            
            // Отрисовка узла
            g.drawOval(x_top_left, y_top_left, node_diam, node_diam); // plus offset
            
            // Отрисовка соединений
            Node[] conns = node.getConnections();
            for (Node conn : conns) {
                int other_x = conn.getX();
                int other_y = conn.getY();
                
                // Чтобы линия начиналась не из центра
                double alpha = atan2((other_x - x), (other_y - y));
                int dimin_x = (int)(sin(alpha) * node_diam);
                int dimin_y = (int)(cos(alpha) * node_diam);
                
                int x1 = x+dimin_x + offset_x;
                int y1 = y+dimin_y + offset_y;
                int x2 = other_x-dimin_x + offset_x;
                int y2 = other_y-dimin_y + offset_y;
                
                drawArrow(g, x1, y1, x2, y2);
            }
            
            // Отрисовка выходов
            if (node.exit)
                drawArrow(g, 
                        x + offset_x, y-node_diam/2 + offset_y, 
                        x + offset_x, y-node_diam*2 + offset_y);            
        }
        
        // Отрисовка входа
        if (enterIndex != -1) {
            node = nodes.get(enterIndex);
            int x = node.getX();
            int y = node.getY();
            
            drawArrow(g, 
                    x-node_diam*2 + offset_x, y + offset_y, 
                    x-node_diam/2 + offset_x, y + offset_y);
        }     
    }
}
