package project;

import java.awt.*;
import javax.swing.*;
import java.util.ArrayList;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.atan2;
import static java.lang.Math.ceil;
import static java.lang.Math.PI;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Visualizer extends JPanel {
    
    private ArrayList<Node> nodes;              // Расширяемый массив узлов
    private ArrayList<Connection> connections;  // Массив переходов
    private int node_diam;                      // Диаметр узлов
    public double min_dist;                     // Минимальное расстояние между узлами
    private int current_elem_index;             // Индекс активного элемента
    private int enter_index;                    // Индекс узла, являющегося входом
    private int offset_x, offset_y;             // Отклонение для перемещения вида
    private int font_size;                      // Кегель шрифта
    private Font font;                          // Шрифт
    private FontMetrics font_metrics;           // Размеры шрифта
    private String letter;                      // Строка для переходов
    private Point current_arrow;                // Конечная точка активного перехода
    
    public Visualizer(int diam) {
        nodes = new ArrayList<>();
        connections = new ArrayList<>();
        node_diam = diam;
        min_dist = node_diam * 1.5;
        current_elem_index = -1;
        enter_index = -1;
        offset_x = 0;
        offset_y = 0;
        font_size = 14;
        font = new Font(Font.MONOSPACED, Font.PLAIN, font_size);
        letter = "";
        current_arrow = null;
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
            if (enter_index == index) enter_index = -1;
            
            // Удаление соединений, связанных с узлом
            Node node = nodes.get(index);
            for (int i = connections.size()-1; i >= 0; i--) {
                Connection conn = connections.get(i);
                Node from = conn.getFrom();
                Node to = conn.getTo();
                if (node == from || node == to)
                    connections.remove(i);
            }
            
            // Убрать сам узел
            nodes.remove(index);
        }
    }   
    
    // Двигает узел (пока зажата кнопка мыши)
    public void moveElem(int x, int y) {
        if (current_elem_index == -1)
            current_elem_index = getElemIndexAt(x, y);
        if (current_elem_index != -1) {
            Node node = nodes.get(current_elem_index);
            node.set(x - offset_x, y - offset_y);
        }
    }
    
    // Фиксирует узел. Использовать после moveElem (когда кнопка мыши отпущена),
    // чтобы выбрать другой узел для перемещения
    public void fixElem(int x, int y) {
        if (current_elem_index != -1) {
            Node node = nodes.get(current_elem_index);
            node.set(x - offset_x, y - offset_y);
            nodes.set(current_elem_index, node); // Нужно ли?
            // Конец работы с активным узлом
            current_elem_index = -1;
        }
    }
    
    // В режиме добавления соединения при нажатии кнопки мыши
    public void addArrow(int x, int y) {
        if (current_elem_index == -1)
            current_elem_index = getElemIndexAt(x, y);
        if (current_elem_index != -1 && letter.length() != 0) {
            current_arrow = new Point(x - offset_x, y - offset_y);
        }
    }
    
    // В режиме добавления соединения при перемещении мыши
    public void moveArrow(int x, int y) {
        if (current_arrow != null) {
            current_arrow = new Point(x - offset_x, y - offset_y);
        }
    }
    
    // В режиме добавления соединения при отпускании мыши
    public void fixArrow(int x, int y) {
        if (current_arrow != null) {
            
            // Если кнопка мыши была отпущена на существующем узле
            int other_node_index = getElemIndexAt(x, y);
            if (other_node_index != -1 && other_node_index != current_elem_index) {
                Node node = nodes.get(current_elem_index);
                Node other_node = nodes.get(other_node_index);
                
                // Проверка на существование второго такого же соединения
                for (int i = connections.size()-1; i >= 0; i--) {
                    Connection conn = connections.get(i);
                    if (conn.getFrom() == node && conn.getWeight().equals(letter))
                        connections.remove(i);                    
                }                        
                
                // Подключение к существующему узлу
                Connection conn = new Connection(node, other_node, letter);
                connections.add(conn);
            }
            // Конец работы с активным узлом
            current_elem_index = -1;
            // Конец работы с активным переходом
            current_arrow = null;
        }
    }
    
    // Устанавливает вход в одном из узлов
    public void setEnter(int x, int y) {
        int index = getElemIndexAt(x, y);
        if (index != -1) {
            Node node;
            // Вход может быть только один, поэтому
            // если вход уже существует, убрать вход
            if (enter_index != -1) {
                node = nodes.get(enter_index);
                node.enter = false;
                nodes.set(enter_index, node);
            }
            // Установить новый вход
            node = nodes.get(index);
            node.enter = true;
            nodes.set(index, node);
            enter_index = index;
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
    
    public void setLetter(String s) {
        letter = s;
    }
    
    // Возвращает смещение
    public String getOffset() {
        return offset_x + " " + offset_y;
    }
    
    public String getConns() {
        String str = "";
        
        for (Connection conn : connections) {
            Node from = conn.getFrom();
            Node to = conn.getTo();
            Pattern p = Pattern.compile("@(.*)");  
            Matcher m1 = p.matcher(from.toString());
            Matcher m2 = p.matcher(to.toString());
            m1.find();
            m2.find();
            str += m1.group(1) + " -> " + m2.group(1) + "\n";
        }
        
        return str;
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
    
    // Возвращает true если точка ближе 
    // чем на минимальном расстоянии от одного из узлов
    private boolean tooClose(int x, int y) {
        for(int i = 0; i < nodes.size(); i++) {
            int node_x = nodes.get(i).getX();
            int node_y = nodes.get(i).getY();
            
            double distance = Math.hypot((x - node_x), (y - node_y));
            
            if (distance < min_dist) return true;
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
    
    // Рисует строку перехода на середине стрелки внутри прямоугольника
    private void drawConnLetter(String letter, Graphics g, int x1, int y1, int x2, int y2) {
        int mid_x = x1 + (x2-x1)/2;
        int mid_y = y1 + (y2-y1)/2;
                
        int desc = font_metrics.getDescent();
        int rect_width = font_metrics.stringWidth(letter) + desc;
        int rect_height = font_metrics.getHeight();
        int rect_x = mid_x - rect_width / 2;
        int rect_y = mid_y - rect_height / 2;
        
        g.fillRect(rect_x, rect_y, rect_width, rect_height);
        
        int letter_x = mid_x - rect_width / 2 + desc / 2;
        int letter_y = mid_y + rect_height - font_metrics.getDescent() - rect_height / 2;
        g.setColor(Color.WHITE);
        g.drawString(letter, letter_x, letter_y);
        g.setColor(Color.BLACK);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        
        // Размеры шрифта
        font_metrics = g.getFontMetrics(g.getFont());
        
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
            g.drawOval(x_top_left, y_top_left, node_diam, node_diam);
            
            // Отрисовка имени
            Pattern p = Pattern.compile(".*@(.*)$");  
            Matcher m = p.matcher(node.toString());
            m.find();
            g.setColor(Color.RED);
            g.drawString(m.group(1), x_top_left, y_top_left);
            g.setColor(Color.BLACK);
            
            // Отрисовка выходов
            if (node.exit)
                drawArrow(g, 
                        x + offset_x, y-node_diam/2 + offset_y, 
                        x + offset_x, y-node_diam*2 + offset_y);
        }
        
        // Отрисовка входа
        if (enter_index != -1) {
            node = nodes.get(enter_index);
            int x = node.getX();
            int y = node.getY();
            
            drawArrow(g, 
                    x-node_diam*2 + offset_x, y + offset_y, 
                    x-node_diam/2 + offset_x, y + offset_y);
        }
        
        // Отрисовка соединений
        for (Connection conn : connections) {
            Node from = conn.getFrom();
            Node to = conn.getTo();
            String weight = conn.getWeight();
            
            int from_x = from.getX();
            int from_y = from.getY();
            int to_x = to.getX();
            int to_y = to.getY();
            
            // Чтобы линия начиналась не из центра
            double alpha = atan2((to_x - from_x), (to_y - from_y));
            int dimin_x = (int)(sin(alpha) * node_diam);
            int dimin_y = (int)(cos(alpha) * node_diam);
            
            int x1 = from_x + dimin_x + offset_x;
            int y1 = from_y + dimin_y + offset_y;
            int x2 = to_x - dimin_x + offset_x;
            int y2 = to_y - dimin_y + offset_y;
            
            drawArrow(g, x1, y1, x2, y2);            
            drawConnLetter(weight, g, x1, y1, x2, y2);
        }
        
        // Отрисовка активного перехода
        if (current_arrow != null) {
            Node current_node = nodes.get(current_elem_index);
            
            int from_x = current_node.getX();
            int from_y = current_node.getY();
            int to_x = current_arrow.x;
            int to_y = current_arrow.y;
            
            // Чтобы линия начиналась не из центра
            double alpha = atan2((to_x - from_x), (to_y - from_y));
            int dimin_x = (int)(sin(alpha) * node_diam);
            int dimin_y = (int)(cos(alpha) * node_diam);
            
            int x1 = from_x + dimin_x + offset_x;
            int y1 = from_y + dimin_y + offset_y;
            int x2 = to_x - dimin_x + offset_x;
            int y2 = to_y - dimin_y + offset_y;
            
            drawArrow(g, x1, y1, x2, y2);            
            drawConnLetter(letter, g, x1, y1, x2, y2);
        }
    }
}
