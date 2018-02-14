package project;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.MouseInputAdapter;


public class Automat extends JFrame {
    
    private int frameWidth;
    private int frameHeight;    
    
    private final Visualizer vis;
    private JButton defaultModeBtn;
    private JButton addModeBtn;
    private JButton removeModeBtn;
    private JButton dragModeBtn;
    private JButton connectModeBtn;
    private JButton addLoopModeBtn;
    private JButton disconnectModeBtn;
    private JButton enterModeBtn;
    private JButton exitModeBtn;
    private JButton offsetModeBtn;
    private final JLabel offsetLabel;
    private final JTextField letterSetter;
    
    /*
    0 - Ничего
    1 - Добавление узла
    2 - Удаление узла
    3 - Перемещение узла
    4 - Добавление перехода
    5 - Добавление петли
    6 - Удаление перехода
    7 - Выбрать входной узел
    8 - Флаг распознавания (выход)
    9 - Перемещение по полю
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
        addButton(defaultModeBtn, "Default", 0, 350, 100, 100, 30);
        
        // Настройка кнопки режима добавления узла
        addButton(addModeBtn, "Add", 1, 350, 140, 100, 30);
        
        // Настройка кнопки режима удаления узла
        addButton(removeModeBtn, "Remove", 2, 350, 180, 100, 30);
        
        // Настройка кнопки режима перемещения узла
        addButton(dragModeBtn, "Drag", 3, 350, 220, 100, 30);
        
        // Настройка кнопки режима соединения узлов
        addButton(connectModeBtn, "Connect", 4, 350, 260, 100, 30);
        
        // Настройка кнопки режима добавления петли
        addButton(addLoopModeBtn, "Add loop", 5, 350, 300, 100, 30);
                
        // Настройка кнопки режима удаления соединения
        addButton(disconnectModeBtn, "Disconnect", 6, 350, 340, 100, 30);
        
        // Настройка кнопки режима выбора входа
        addButton(enterModeBtn, "Set enter", 7, 350, 380, 100, 30);
        
        // Настройка кнопки режима выбора флага распознавания (выход)
        addButton(exitModeBtn, "Toggle exit", 8, 350, 420, 100, 30);
        
        // Настройка кнопки режима смещения
        addButton(offsetModeBtn, "Offset", 9, 350, 460, 100, 30);
        
        // Надпись, показывающая смещение
        offsetLabel = new JLabel("Offset: 0 0");
        offsetLabel.setBounds(20, 350, 200, 20);
        add(offsetLabel);
        
        // Строка перехода
        letterSetter = new JTextField();
        letterSetter.setBounds(350, 30, 200, 20);
        letterSetter.setText("a");
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
                    break;
                case 2: // Удаление узла
                    vis.removeElem(x, y);
                    vis.repaint();
                    break;
                case 3: // Перемещение узла
                    updateNode(e);
                    break;
                case 5: // Добавление петли
                    addLoop(e);
                    break;
                case 6: // Убрать петлю
                    removeLoop(e);
                    break;
                case 7: // Установка входа
                    setEnter(e);
                    break;
                case 8: // Установка выхода
                    setExit(e);
                    break;
                case 9: // Установка смещения в начальное положение
                    old_offset_x = 0;
                    old_offset_y = 0;
                    vis.setOffset(old_offset_x, old_offset_y);
                    vis.repaint();
                    offsetLabel.setText("Offset: 0 0");
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
                    break;
                case 4: // Добавление перехода
                    addArrow(e);
                    break;
                case 6: // Удаление соединения
                    removeFrom(e);
                    break;
                case 9: // Инициализация смещения
                    getOldCoords(e);
                    break;
                default:
                    break;
            }
        }
        
        @Override
        public void mouseDragged(MouseEvent e) {

            switch(mode) {
                case 3: // Перемещение узла
                    updateNode(e);
                    break;
                case 4: // Добавление перехода
                    moveArrow(e);
                    break;
                case 6: // Удаление перехода
                    break;
                case 9:
                    setOffset(e);
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
                    break;
                case 4: // Установка перехода
                    fixArrow(e);
                    break;
                case 6: // Удаление перехода
                    removeTo(e);
                    break;
                case 9:
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
        
        private void addLoop(MouseEvent e) {
            int x = e.getX();
            int y = e.getY();
            vis.setLetter(letterSetter.getText());
            vis.addLoop(x, y);
            vis.repaint();
        }
        
        private void removeLoop(MouseEvent e) {
            int x = e.getX();
            int y = e.getY();
            vis.removeLoop(x, y);
            vis.repaint();
        }
        
        private void removeFrom(MouseEvent e) {
            int x = e.getX();
            int y = e.getY();
            vis.removeFrom(x, y);
        }
        
        private void removeTo(MouseEvent e) {
            int x = e.getX();
            int y = e.getY();
            vis.removeTo(x, y);
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
    
    private class ButtonMode implements ActionListener {
        byte action;
        ButtonMode(int mode) {
            action = (byte)mode;
        }
        
        @Override
        public void actionPerformed(ActionEvent e) {
            mode = action;
        }
    }
    
    private void addButton(JButton btn, String text, int mode, int x, int y, int width, int height) {
        btn = new JButton(text);
        btn.setBounds(x, y, width, height);
        btn.addActionListener(new ButtonMode(mode));
        add(btn);
    }
        
    public static void main(String[] args) {
        Automat frame = new Automat();
        frame.setVisible(true);
    }
}
