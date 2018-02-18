package project;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;


public class GRMenu extends JMenuBar {
    
    public JMenu file, view;
    public JMenuItem exit;
    
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
        
        // Меню "File"
        file.add(exit);
        add(file);
        
        // Меню "View"
        add(view);
    }
    
}
