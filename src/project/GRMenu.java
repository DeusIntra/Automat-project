package project;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;


public class GRMenu extends JMenuBar {
    
    private final JMenu file, view;
    public JMenuItem exit, choose_font, view_settings;
    private final JMenuItem empty; // Пустое поле
    
    public GRMenu(final JFrame f) {
        file = new JMenu("File");
        view = new JMenu("View");
        
        exit = new JMenuItem("Exit");
        exit.addActionListener(new ActionListener() {           
            @Override
            public void actionPerformed(ActionEvent e) {
                f.dispose();
            }
        });
        
        empty = new JMenuItem();
        choose_font = new JMenuItem("Выбрать шрифт");
        view_settings = new JMenuItem("Настройки отображения");
        
        // Меню "File"
        file.add(empty);
        file.add(exit);
        add(file);
        
        // Меню "View"
        view.add(choose_font);
        view.add(view_settings);
        add(view);
    }
    
}
