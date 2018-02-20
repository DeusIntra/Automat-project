package project;

import java.util.ArrayList;
import javax.swing.*;


public class ButtonPanel extends JPanel {
    
    private int width;
    private int height;
    
    public ArrayList<JButton> buttons;
    
    public ButtonPanel() {
        setLayout(null);
        buttons = new ArrayList<>();
        width = 0;
        height = 0;
        
        for (int i = 0; i < 10; i++) {
            ImageIcon icon = new ImageIcon("icons\\" + String.valueOf(i+1) +  ".png");
            JButton button = new JButton(icon);
            button.setBounds(
                    (i/5)*icon.getIconWidth() + (i/5)*2, (i%5)*icon.getIconHeight() + (i%5)*2, 
                    icon.getIconWidth(), icon.getIconHeight());
            buttons.add(button);
            add(button);
            
            if (width < (i/5+1) * icon.getIconWidth() + (i/5)*2)
                width = (i/5+1) * icon.getIconWidth() + (i/5)*2;
            if (height < (i%5+1) * icon.getIconHeight() + (i%5)*2) 
                height = (i%5+1) * icon.getIconHeight() + (i%5)*2;
        }
        
        buttons.get(0).setToolTipText("Добавить вершину");
        buttons.get(1).setToolTipText("Удалить вершину");
        buttons.get(2).setToolTipText("Переместить вершину");
        buttons.get(3).setToolTipText("Добавить переход");
        buttons.get(4).setToolTipText("Добавить петлю");
        buttons.get(5).setToolTipText("Удалить переход");
        buttons.get(6).setToolTipText("Выбрать вход");
        buttons.get(7).setToolTipText("Установить выход");
        buttons.get(8).setToolTipText("Перемещение по полотну");
        buttons.get(9).setToolTipText("Установить цвет вершины");
    }
    
    public int height() {
        return height;
    }
    
    public int width() {
        return width;
    }
    
}
