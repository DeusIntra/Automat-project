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
        
        for (int i = 0; i < 9; i++) {
            ImageIcon icon = new ImageIcon("img\\" + String.valueOf(i+1) +  ".png");
            JButton button = new JButton(icon);
            button.setBounds((i/5)*icon.getIconWidth(), (i%5)*icon.getIconHeight(), icon.getIconWidth(), icon.getIconHeight());
            buttons.add(button);
            add(button);
            
            width = (i/5) * 2 * icon.getIconWidth();
            if (height < (i%5)*icon.getIconHeight()*2) 
                height += icon.getIconHeight();
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
    }
    
    public int height() {
        return height;
    }
    
    public int width() {
        return width;
    }
    
}
