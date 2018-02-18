package project;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;


public class GRMenu extends JMenuBar {
    
    private final JMenu file, view;
    public JMenuItem exit, view_settings;
    
    public GRMenu(JFrame f) {
        file = new JMenu("File");
        view = new JMenu("View");
        
        exit = new JMenuItem("Exit");
        exit.addActionListener(new ActionListener() {           
            @Override
            public void actionPerformed(ActionEvent e) {
                f.dispose();
            }
        });
        
        view_settings = new JMenuItem("Настройки рисования");
        
        // Меню "File"
        file.add(exit);
        add(file);
        
        // Меню "View"
        view.add(view_settings);
        add(view);
    }
    
}
