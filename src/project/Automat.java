package project;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.MouseInputAdapter;


public class Automat extends JFrame {
    
    private int frameWidth;
    private int frameHeight;    
    
    private final Visualizer vis;
    private final JButton defaultModeBtn;
    private final JButton addModeBtn;
    private final JButton removeModeBtn;
    private final JButton dragModeBtn;
    private final JButton connectModeBtn;
//    private final JButton enterModeBtn;
//    private final JButton exitModeBtn;
//    private final JButton offsetModeBtn;
    
    /*
    0 - Ничего
    1 - Добавление узла
    2 - Удаление узла
    3 - Перемещение узла
    4 - Добавление / удаление перехода
    5 - Выбрать входной узел
    6 - Флаг распознавания
    7 - Перемещение по полю
    */
    private byte mode;
    
    public Automat() {
        mode = 0;
        frameWidth = 800;
        frameHeight = 600;
        
        // Настройка окна
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(frameWidth, frameHeight);
        setLayout(null);
        
        // Настройка панели
        vis = new Visualizer(10);
        vis.setBounds(20, 30, 300, 300);
        vis.setBackground(Color.WHITE);
        VisMouseListener listener = new VisMouseListener();
        vis.addMouseListener(listener);
        vis.addMouseMotionListener(listener);
        add(vis);
        
        // Настройка кнопки обычного режима
        defaultModeBtn = new JButton("Default");
        defaultModeBtn.setBounds(350, 100, 100, 30);
        defaultModeBtn.addActionListener((ActionEvent e) -> {mode = 0;});
        add(defaultModeBtn);
        
        // Настройка кнопки режима добавления узла
        addModeBtn = new JButton("Add");
        addModeBtn.setBounds(350, 140, 100, 30);
        addModeBtn.addActionListener((ActionEvent e) -> {mode = 1;});
        add(addModeBtn);
        
        // Настройка кнопки режима удаления узла
        removeModeBtn = new JButton("Remove");
        removeModeBtn.setBounds(350, 180, 100, 30);
        removeModeBtn.addActionListener((ActionEvent e) -> {mode = 2;});
        add(removeModeBtn);
        
        // Настройка кнопки режима перемещения узла
        dragModeBtn = new JButton("Drag");
        dragModeBtn.setBounds(350, 220, 100, 30);
        dragModeBtn.addActionListener((ActionEvent e) -> {mode = 3;});
        add(dragModeBtn);
        
        // Настройка кнопки режима соединения узлов
        connectModeBtn = new JButton("Connect");
        connectModeBtn.setBounds(350, 260, 100, 30);
        connectModeBtn.addActionListener((ActionEvent e) -> {mode = 4;});
        add(connectModeBtn);
    }
    
    private class VisMouseListener extends MouseInputAdapter {
        
        @Override
        public void mouseClicked(MouseEvent e) {
            
            // Принимает только клик левой кнопки мыши
            if(e.getButton() != 1) return;
            
            int x = e.getX();
            int y = e.getY();
            
            switch(mode) {
                case 1: // Добавление узла
                    vis.addElem(x, y);
                    vis.repaint();
                    break;
                    
                case 2: // Удаление узла
                    vis.removeElem(x, y);
                    vis.repaint();
                    break;
                    
                case 3: // Перемещение узла
                    updateNode(e);      
                    break;
                default:
                    break;
            }
        }
        
        @Override
        public void mousePressed(MouseEvent e) {
            
            // Принимает только нажатие левой кнопки мыши
            if(e.getButton() != 1) return;
            
            switch(mode) {
                case 3:
                    updateNode(e);
                    break;
                case 4:
                    addArrow(e);
                    break;
                case 7:
                    break;
                default:
                    break;
            }
        }
        
        @Override
        public void mouseDragged(MouseEvent e) {
            switch(mode) {
                case 3:
                    updateNode(e);
                    break;
                case 4:
                    moveArrow(e);
                    break;
                case 7:
                    break;
                default:
                    break;
            }
        }
        
        @Override
        public void mouseReleased(MouseEvent e) {
            switch(mode) {
                case 3:
                    fixNode(e);
                    break;
                case 4:
                    fixArrow(e);
                    break;
                case 7:
                    break;
                default:
                    break;
            }
        }
        
        private void updateNode(MouseEvent e) {
            int x = e.getX();
            int y = e.getY();
            vis.moveElem(x, y);
            vis.repaint();
        }
        
        private void fixNode(MouseEvent e) {
            int x = e.getX();
            int y = e.getY();
            vis.fixElem(x, y);
            vis.repaint();
        }
        
        private void addArrow (MouseEvent e) {
            int x = e.getX();
            int y = e.getY();
            vis.addArrow(x, y);
            vis.repaint();
        }
        
        private void moveArrow (MouseEvent e) {
            int x = e.getX();
            int y = e.getY();
            vis.moveArrow(x, y);
            vis.repaint();
        }
        
        private void fixArrow (MouseEvent e) {
            int x = e.getX();
            int y = e.getY();
            vis.fixArrow(x, y);
            vis.repaint();
        }
    }
    
    
    public static void main(String[] args) {
        Automat frame = new Automat();
        frame.setVisible(true);
    }
}
