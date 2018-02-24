package project;

import java.awt.*;
import java.awt.event.*;
import java.util.Stack;
import javax.swing.*;
import javax.swing.event.*;


public class Automat extends JFrame {
    
    private int contentPaneWidth;
    private int contentPaneHeight;    
    
    ViewSettingsDialog view_settings_dialog;
    private final GRMenu menu;
    private final GraphComponent graph;
    private final JPanel color_choose;
    private final JLabel outputLabel;
    private final JLabel color_label;
//    private final JLabel scale_label;
    private final JTextField letter_setter;
    private final JButton get_regular_btn;
    private final JTextField get_regular_TF;
    private final JMenuItem copy;
    private final JMenuItem copy_selected;
    private final JPopupMenu popup_menu;
    private final JFontChooser font_chooser;
//    private final JSlider scale_slider;
    
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
    10 - Выбор цвета узла или перехода
    */
    private byte mode;
    
    public Automat() {
        
        setTitle("Конечный автомат v0.98");
        Automat this_frame = this;
        mode = 0;        
        
        // Элементы выпадающего меню
        copy = new JMenuItem("Копировать");
        copy.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                get_regular_TF.selectAll();
                get_regular_TF.copy();
            }
        });
        copy_selected = new JMenuItem("Копировать выделенное");
        copy_selected.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                get_regular_TF.copy();
            }
        });
        popup_menu = new JPopupMenu();
        popup_menu.add(copy);
        popup_menu.add(copy_selected);
        
        // Настройка окна программы
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLayout(null);
        contentPaneWidth = 792;
        contentPaneHeight = 547;
        // Когда окно меняет размер, меняется размер и положение компонентов
        getRootPane().addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                contentPaneWidth = getContentPane().getWidth();
                contentPaneHeight = getContentPane().getHeight();
                
                if (contentPaneWidth - 160 > 300) {
                    if (contentPaneHeight - 120 > 300) {
                        graph.setSize(contentPaneWidth - 160, contentPaneHeight - 120);
                        get_regular_btn.setLocation(20, contentPaneHeight - 40);
                        get_regular_TF.setBounds(140, contentPaneHeight - 40, contentPaneWidth - 160, 20);
                        outputLabel.setLocation(20, contentPaneHeight - 80);

                    }
                    else {
                        graph.setSize(contentPaneWidth - 160, 300);
                        get_regular_TF.setSize(contentPaneWidth - 160, 20);
                        get_regular_btn.setLocation(20, 380);
                        outputLabel.setLocation(20, 340);
                    }
                }
                else {
                    if (contentPaneHeight - 120 > 300) {
                        graph.setSize(graph.getWidth(), contentPaneHeight - 120);
                        get_regular_btn.setLocation(20, contentPaneHeight - 40);
                        get_regular_TF.setLocation(140, contentPaneHeight - 40);
                        outputLabel.setLocation(20, contentPaneHeight - 80);
                    }
                    else {
                        graph.setSize(300, 300);
                        get_regular_btn.setLocation(20, 380);
                        get_regular_TF.setBounds(140, 380, 300, 20);
                        outputLabel.setLocation(20, 340);
                    }
                }
            }
        });
        
        // Диалоговое окно выбора шрифта, любезно предоставленное Masahiko SAWAI
        font_chooser = new JFontChooser();
        
        // Диалоговое окно настроек вида
        view_settings_dialog = new ViewSettingsDialog(this, "Настройки отображения");
        
        // Меню
        menu = new GRMenu(this);
        menu.choose_font.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int result = font_chooser.showDialog(this_frame);
                if (result == JFontChooser.OK_OPTION) {
                    Font font = font_chooser.getSelectedFont();
                    graph.setLetterFont(font);
                    graph.repaint();
                    view_settings_dialog.setGraphFont(font);
                } 
            }            
        });
        menu.view_settings.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                view_settings_dialog.setVisible(true);
            }
        });
        setJMenuBar(menu);
        
        // Панель кнопок рисования
        ButtonPanel bp = new ButtonPanel();
        bp.setBounds(20, 20, bp.width(), bp.height());
        for(int i = 0; i < bp.buttons.size(); i++) {
            JButton button = bp.buttons.get(i);
            button.addActionListener(new ButtonMode(i+1));
        }
        add(bp);
        
        // Строка перехода
        letter_setter = new JTextField();
        letter_setter.setBounds(20, 20 + bp.height() + 20, 100, 20);
        letter_setter.setText("a");
        add(letter_setter);
        
        // Надпись выбора цвета
        color_label = new JLabel("Выбрать цвет:");
        color_label.setBounds(20, 70 + bp.height(), 100, 20);
        add(color_label);
        
        // Выбор цвета
        color_choose = new JPanel();
        color_choose.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        color_choose.setBounds(20, 90 + bp.height(), 30, 30);
        color_choose.setBackground(Color.RED);
        color_choose.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent e) {
                Color color = JColorChooser.showDialog(this_frame, "Выбор цвета", color_choose.getBackground());
                color_choose.setBackground(color);
            }
        });
        add(color_choose);     
        
        // Настройка панели
        graph = new GraphComponent();
        graph.setBounds(140, 20, contentPaneWidth - 160, contentPaneHeight - 120);
        VisMouseListener listener = new VisMouseListener();
        graph.addMouseListener(listener);
        graph.addMouseMotionListener(listener);
        add(graph);
        
        // Кнопка получения регулярного выражения
        get_regular_btn = new JButton("Получить");
        get_regular_btn.setBounds(20, contentPaneHeight - 40, 100, 20);
        get_regular_btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    get_regular_TF.setText(graph.getRegular());
                    outputLabel.setText("Регулярное выражение получено");
                }
                catch (RuntimeException ex) {
                    outputLabel.setText("Ошибка: не указан вход или выход");
                }
            }
        });
        add(get_regular_btn);
        
        // Поле вывода регулярного выражения
        get_regular_TF = new JTextField();
        get_regular_TF.setEditable(false);
        get_regular_TF.setBackground(Color.WHITE);
        get_regular_TF.setBounds(140, contentPaneHeight - 40, contentPaneWidth - 160, 20);
        get_regular_TF.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() != 3) return;                
                popup_menu.show(this_frame, e.getX()+145, e.getY()+555);
            }
        });
        add(get_regular_TF);
        
        outputLabel = new JLabel();
        outputLabel.setBounds(20, contentPaneHeight - 80, contentPaneWidth - 40, 20);
        add(outputLabel);
        
//        scale_label = new JLabel("Масштаб: 100%");
//        scale_label.setBounds(20, 130 + bp.height(), 100, 20);
//        add(scale_label);
//        
//        scale_slider = new JSlider(25, 400, 100);
//        scale_slider.setBounds(15, 150 + bp.height(), 100, 20);
//        scale_slider.addChangeListener(new ScaleListener());
//        add(scale_slider);

    }

    // Вызывается после закрытия диалога настройки отображения
    public void updateGraph(int node_diam, double arc_height,
                            boolean show_name, boolean show_net,
                            int net_size, Color net_color) {
        graph.setNodeDiam(node_diam);
        graph.setArcHeight(arc_height);
        graph.show_name = show_name;
        graph.show_net = show_net;
        graph.setNetSize(net_size);
        graph.setNetColor(net_color);
        graph.repaint();
    }

//    private class ScaleListener implements ChangeListener {
//
//        @Override
//        public void stateChanged(ChangeEvent e) {
//            int scale = scale_slider.getValue();
//            scale_label.setText("Масштаб: " + scale + "%");
//            graph.setScale(scale / 100.0);
//            graph.repaint();
//        }
//    }
    
    private class VisMouseListener extends MouseInputAdapter {
        
        private int old_x, old_y, old_offset_x=0, old_offset_y=0;
        
        @Override
        public void mouseClicked(MouseEvent e) {
            // Принимает только клик левой кнопки мыши
            if(e.getButton() != 1) return;
            
            try {
                int x = e.getX();
                int y = e.getY();

                switch(mode) {
                    case 1: // Добавление узла
                        graph.addElem(x, y);
                        graph.repaint();
                        break;
                    case 2: // Удаление узла
                        graph.removeElem(x, y);
                        graph.repaint();
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
                        graph.setOffset(old_offset_x, old_offset_y);
                        graph.repaint();
    //                    offsetLabel.setText("Offset: 0 0");
                        break;
                    case 10: // Установка цвета узла
                        setNodeColor(e);
                        break;
                    default:
                        break;
                }
            }
            catch (Exception ex) {
                outputLabel.setText(ex.getMessage());
            }
        }
        
        @Override
        public void mousePressed(MouseEvent e) {
            
            // Принимает только нажатие левой кнопки мыши
            if(e.getButton() != 1) return;
            
            try {
                switch(mode) {
                    case 3: // Перемещение узла
                        updateNode(e);
                        break;
                    case 4: // Добавление перехода
                        String str = letter_setter.getText();
                        str = checkInput(str);
                        letter_setter.setText(str);
                        addArrow(e);
                        break;
                    case 6: // Удаление соединения
                        graph.chooseActiveNode(e.getX(), e.getY());
                        break;
                    case 9: // Инициализация смещения
                        getOldCoords(e);
                        break;
                    case 10: // Покраска перехода
                        graph.chooseActiveNode(e.getX(), e.getY());
                        break;
                    default:
                        break;
                }
            }
            catch (Exception ex) {
                outputLabel.setText(ex.getMessage());
            }
        }
        
        @Override
        public void mouseDragged(MouseEvent e) {
            
            try {
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
            catch (Exception ex) {
                outputLabel.setText(ex.getMessage());
            }
        }
        
        @Override
        public void mouseReleased(MouseEvent e) {
            
            try {
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
                    case 10: // Покраска перехода
                        setConnColor(e);
                        break;
                    default:
                        break;
                }
            }
            catch (Exception ex) {
                outputLabel.setText(ex.getMessage());
            }
        }
        
        private void updateNode(MouseEvent e) {
            int x = e.getX();
            int y = e.getY();
            graph.moveElem(x, y);
            graph.repaint();
        }
        
        private void fixNode(MouseEvent e) {
            int x = e.getX();
            int y = e.getY();
            graph.fixElem(x, y);
            graph.repaint();
        }
        
        private void addArrow (MouseEvent e) {
            int x = e.getX();
            int y = e.getY();
            graph.setLetter(letter_setter.getText());
            graph.addArrow(x, y);
            graph.repaint();
        }
        
        private void moveArrow (MouseEvent e) {
            int x = e.getX();
            int y = e.getY();
            graph.moveArrow(x, y);
            graph.repaint();
        }
        
        private void fixArrow (MouseEvent e) {
            int x = e.getX();
            int y = e.getY();
            graph.fixArrow(x, y);
            graph.repaint();
        }
        
        private void addLoop(MouseEvent e) {
            int x = e.getX();
            int y = e.getY();
            graph.setLetter(letter_setter.getText());
            graph.addLoop(x, y);
            graph.repaint();
        }
        
        private void removeLoop(MouseEvent e) {
            int x = e.getX();
            int y = e.getY();
            graph.removeLoop(x, y);
            graph.repaint();
        }
        
        private void removeTo(MouseEvent e) {
            int x = e.getX();
            int y = e.getY();
            graph.removeTo(x, y);
            graph.repaint();
        }
        
        private void setEnter(MouseEvent e) {
            int x = e.getX();
            int y = e.getY();
            
            graph.setEnter(x, y);
            graph.repaint();
        }
        
        private void setExit(MouseEvent e) {
            int x = e.getX();
            int y = e.getY();
            
            graph.setExit(x, y);
            graph.repaint();
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
            graph.setOffset(offset_x, offset_y);
            graph.repaint();
//            offsetLabel.setText("Offset: " + vis.getOffset());
        }
        
        private void saveOffset(MouseEvent e) {
            int new_x = e.getX();
            int new_y = e.getY();
            
            old_offset_x += new_x - old_x;
            old_offset_y += new_y - old_y;
        }
        
        private void setNodeColor(MouseEvent e) {
            int x = e.getX();
            int y = e.getY();
            Color color = color_choose.getBackground();
            graph.setNodeColor(color, x, y);
            graph.repaint();
        }
        
        private void setConnColor(MouseEvent e) {
            int x = e.getX();
            int y = e.getY();
            Color color = color_choose.getBackground();
            graph.setConnColor(color, x, y);
            graph.repaint();
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
