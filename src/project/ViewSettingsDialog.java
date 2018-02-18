package project;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


public class ViewSettingsDialog extends JDialog {
    
    // Переменные
    public int  node_diam;
    public int arc_height;
    public int font_size;
    public Font font;
    public boolean show_net;
    public int net_size;
    public boolean show_name;
    
    // Компоненты
    JButton OkBtn;
    
    public ViewSettingsDialog(JFrame f, String title) {
        
        super(f, title);
        
        setSize(300, 300);
        setLayout(null);
        
        OkBtn = new JButton("OK");
        OkBtn.setBounds(200, 200, 20, 20);
        OkBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        });
        add(OkBtn);
        
        setVisible(false);
    }
    
    public void reset() {}
    
}
