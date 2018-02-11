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
    private final JButton enterModeBtn;
    private final JButton exitModeBtn;
    private final JButton offsetModeBtn;
    private final JLabel offsetLabel;
    private final JTextField letterSetter;
    private final JTextArea connectionsTA;
    
    /*
    0 - Ничего
    1 - Добавление узла
    2 - Удаление узла
    3 - Перемещение узла
    4 - Добавление / удаление перехода
    5 - Выбрать входной узел
    6 - Флаг распознавания (выход)
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
        
                
        connectionsTA = new JTextArea();
        connectionsTA.setBounds(480, 100, 300, 300);
        add(connectionsTA);
        
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
        
        // Настройка кнопки режима соединения узлов
        enterModeBtn = new JButton("Set enter");
        enterModeBtn.setBounds(350, 300, 100, 30);
        enterModeBtn.addActionListener((ActionEvent e) -> {mode = 5;});
        add(enterModeBtn);
        
        // Настройка кнопки режима соединения узлов
        exitModeBtn = new JButton("Toggle exit");
        exitModeBtn.setBounds(350, 340, 100, 30);
        exitModeBtn.addActionListener((ActionEvent e) -> {mode = 6;});
        add(exitModeBtn);
        
        // Настройка кнопки режима соединения узлов
        offsetModeBtn = new JButton("Offset");
        offsetModeBtn.setBounds(350, 380, 100, 30);
        offsetModeBtn.addActionListener((ActionEvent e) -> {mode = 7;});
        add(offsetModeBtn);
        
        // Надпись, показывающая смещение
        offsetLabel = new JLabel("Offset: 0 0");
        offsetLabel.setBounds(20, 350, 200, 20);
        add(offsetLabel);
        
        letterSetter = new JTextField();
        letterSetter.setBounds(350, 30, 200, 20);
        add(letterSetter);

    }
    
    private class VisMouseListener extends MouseInputAdapter {
        
        private int old_x, old_y, old_offset_x=0, old_offset_y=0;
        
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
                    connectionsTA.setText(vis.getConns());
                    break;
                    
                case 2: // Удаление узла
                    vis.removeElem(x, y);
                    vis.repaint();
                    connectionsTA.setText(vis.getConns());
                    break;
                    
                case 3: // Перемещение узла
                    updateNode(e);   
                    connectionsTA.setText(vis.getConns());
                    break;
                    
                case 4: // Добавление петли
                    // код код код
                    break;
                    
                case 5: // Установка входа
                    setEnter(e);
                    break;
                    
                case 6: // Установка выхода
                    setExit(e);
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
                case 3: // Перемещение узла
                    updateNode(e);
                    connectionsTA.setText(vis.getConns());
                    break;
                case 4: // Добавление перехода
                    addArrow(e);
                    connectionsTA.setText(vis.getConns());
                    break;
                case 7:
                    getOldCoords(e);
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
                    connectionsTA.setText(vis.getConns());
                    break;
                case 4: // Добавление перехода
                    moveArrow(e);
                    connectionsTA.setText(vis.getConns());
                    break;
                case 7:
                    setOffset(e);
                    connectionsTA.setText(vis.getConns());
                    break;
                default:
                    break;
            }
        }
        
        @Override
        public void mouseReleased(MouseEvent e) {
            
            switch(mode) {
                case 3: // Установка перемещаемого узла в выбранном положении
                    fixNode(e);
                    connectionsTA.setText(vis.getConns());
                    break;
                case 4: // Установка перехода
                    fixArrow(e);
                    connectionsTA.setText(vis.getConns());
                    break;
                case 7:
                    setOffset(e);
                    saveOffset(e);
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
            vis.setLetter(letterSetter.getText());
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
        
        private void setEnter(MouseEvent e) {
            int x = e.getX();
            int y = e.getY();
            
            vis.setEnter(x, y);
            vis.repaint();
        }
        
        private void setExit(MouseEvent e) {
            int x = e.getX();
            int y = e.getY();
            
            vis.setExit(x, y);
            vis.repaint();
        }
        
        private void getOldCoords(MouseEvent e) {
            old_x = e.getX();
            old_y = e.getY();
        }
        
        private void setOffset(MouseEvent e) {
            int new_x = e.getX();
            int new_y = e.getY();
            
            int offset_x = new_x - old_x + old_offset_x;
            int offset_y = new_y - old_y + old_offset_y;
            vis.setOffset(offset_x, offset_y);
            vis.repaint();
            offsetLabel.setText("Offset: " + vis.getOffset());
        }
        
        private void saveOffset(MouseEvent e) {
            int new_x = e.getX();
            int new_y = e.getY();
            
            old_offset_x += new_x - old_x;
            old_offset_y += new_y - old_y;
        }
    }
    
    
    public static void main(String[] args) {
        Automat frame = new Automat();
        frame.setVisible(true);
    }
}
