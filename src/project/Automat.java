package project;

import java.awt.*;
import java.awt.event.*;
import java.util.Stack;
import javax.swing.*;
import javax.swing.event.MouseInputAdapter;


public class Automat extends JFrame {
    
    private int frameWidth;
    private int frameHeight;    
    
    private final GRMenu menu;
    private final Visualizer vis;
//    private JButton defaultModeBtn;
//    private JButton addModeBtn;
//    private JButton removeModeBtn;
//    private JButton dragModeBtn;
//    private JButton connectModeBtn;
//    private JButton addLoopModeBtn;
//    private JButton disconnectModeBtn;
//    private JButton enterModeBtn;
//    private JButton exitModeBtn;
//    private JButton offsetModeBtn;
    private final JLabel offsetLabel;
    private final JTextField letterSetter;
    private final JButton getRegularBtn;
    private final JTextField getRegularTF;
    
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
        
        // Меню
        menu = new GRMenu(this);
        setJMenuBar(menu);
        
        // Панель кнопок рисования
        ButtonPanel bp = new ButtonPanel();
        bp.setBounds(470, 100, bp.width(), bp.height());
        for(int i = 0; i < bp.buttons.size(); i++) {
            JButton button = bp.buttons.get(i);
            button.addActionListener(new ButtonMode(i+1));
        }
        bp.setBackground(Color.red);
        add(bp);
        
        // Настройка панели
        vis = new Visualizer(10);
        vis.setBounds(20, 30, 300, 300);
        vis.setBackground(Color.WHITE);
        VisMouseListener listener = new VisMouseListener();
        vis.addMouseListener(listener);
        vis.addMouseMotionListener(listener);
        add(vis);
        
        // Надпись, показывающая смещение
        offsetLabel = new JLabel("Offset: 0 0");
        offsetLabel.setBounds(20, 350, 200, 20);
        add(offsetLabel);
        
        // Строка перехода
        letterSetter = new JTextField();
        letterSetter.setBounds(350, 30, 200, 20);
        letterSetter.setText("a");
        add(letterSetter);
        
        getRegularTF = new JTextField();
        getRegularTF.setBounds(20, 390, 200, 20);
        add(getRegularTF);
        
        // Кнопка получения регулярного выражения
        getRegularBtn = new JButton("Get regular");
        getRegularBtn.setBounds(570, 30, 100, 30);
        getRegularBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    getRegularTF.setText(vis.getRegular());
                }
                catch (RuntimeException ex) {System.out.println("Ошибка");}
            }
        });
        add(getRegularBtn);

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
                    String str = letterSetter.getText();
                    str = checkInput(str);
                    letterSetter.setText(str);
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
        
        // Проверка и исправление введенной строки
        private String checkInput(String str) {
            Stack<Character> brackets = new Stack<>();
            String output = "";
            
            // Проверка скобок на адекватность (удаляются только закрывающие)
            for(int i = 0; i < str.length(); i++) {
                char ch = str.charAt(i);
                switch(ch) {
                    case '(':
                        // Не должно быть также скобок без символов внутри "()"
                        if (i < str.length()-1 && str.charAt(i+1) == ')') i++;
                        else {
                            brackets.push(ch);
                            output += ch;
                        }
                        break;
                    case ')': // Игнорирует все закрывающие скобки без пары
                        if (!brackets.empty()) {
                            brackets.pop();
                            output += ch;
                        }
                        break;
                    default:
                        output += ch;
                        break;
                }
            }
            
            // Если есть открывающие скобки без пары, необходимо их убрать
            if (!brackets.empty()) {
                brackets.clear();
                str = output.trim();
                output = "";
                
                // Проверка скобок на адекватность (только открывающие)
                for(int i = str.length()-1; i >= 0; i--) {
                    char ch = str.charAt(i);
                    switch(ch) {
                        case ')':
                            brackets.push(ch);
                            output = ch + output;
                            break;
                        case '(': // Игнорирует все открывающие скобки без пары
                            if (!brackets.empty()) {
                                brackets.pop();
                                output = ch + output;
                            }
                            break;
                        default:
                            output = ch + output;
                            break;
                    }
                }
            }
            
            // Проверка адекватности символов '|' и '*'
            str = output.trim(); // Пробелы могут находиться только внутри скобок
            output = "";
            for (int i = 0; i < str.length(); i++) {
                char ch = str.charAt(i);
                switch (ch) {
                    case '|':
                        // Если '|' не в начале или в конце строки
                        if (i != 0 && i != str.length()-1)
                            // Поскольку i не в начале и не в конце строки,
                            // то перед и после i обязательно есть символ.
                            // Если '|' не сразу перед ')' и не сразу после '(',
                            // а также если перед и после него не стоит '|' или '*'
                            if (str.charAt(i-1) != '|' &&
                                str.charAt(i+1) != '|' &&
                                str.charAt(i-1) != '*' &&
                                str.charAt(i+1) != '*' &&
                                str.charAt(i-1) != '(' &&
                                str.charAt(i+1) != ')')
                                output += ch;
                        break;
                    case '*':
                        // Если '*' не в начале и не в конце строки и после ')'
                        if (i != 0 && i != str.length()-1)
                            // Если перед '*' стоит ')'
                            // и после него не стоит '*' или '|'
                            if (str.charAt(i-1) == ')' &&
                                str.charAt(i+1) != '*' &&
                                str.charAt(i+1) != '|')
                                output += ch;
                        break;
                    default:
                        output += ch;
                        break;
                }
            }
            
            return output.trim();
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
          
    public static void main(String[] args) {
        Automat frame = new Automat();
        frame.setVisible(true);
    }
}
