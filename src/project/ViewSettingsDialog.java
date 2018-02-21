package project;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


public class ViewSettingsDialog extends JDialog {
    
    // Переменные
    private int  node_diam;
    private int arc_height;
    private boolean show_name;
    private boolean show_name_demo;
    private boolean show_net;
    private boolean show_net_demo;
    private int net_size;
    private Color net_color;
    
    // Компоненты
    private final JLabel  
            node_diam_label,
            arc_height_label,
            net_size_label,
            net_color_label;
    
    private final JSlider 
            node_diam_slider, 
            arc_height_slider, 
            net_scale_slider;
    
    private final JCheckBox show_name_cb, show_net_cb;
    
    private final JPanel net_color_choose;
    
    private final JButton okBtn, canselBtn;
    
    private final GraphComponent graph_demo;
    
    public ViewSettingsDialog(Automat parent_frame, String title) {
        
        super(parent_frame, title);
        
        setSize(600, 330);
        setLayout(null);
        
        node_diam = 10;
        arc_height = 25;
        show_name = false;
        show_name_demo = false;
        show_net = true;
        show_net_demo = true;
        net_size = 80;
        net_color = new Color(0, 0, 0, (float)0.05);
        
        // Лейбелы
        node_diam_label = new JLabel("Диаметр вершины");
        node_diam_label.setBounds(20, 20, 130, 20);
        add(node_diam_label);
        
        arc_height_label = new JLabel("Высота дуги");
        arc_height_label.setBounds(20, 60, 130, 20);
        add(arc_height_label);
        
        net_size_label = new JLabel("Размер сетки");
        net_size_label.setBounds(20, 180, 130, 20);
        add(net_size_label);
        
        net_color_label = new JLabel("Цвет сетки");
        net_color_label.setBounds(20, 220, 130, 20);
        add(net_color_label);
        
        // Слайдеры
        node_diam_slider = new JSlider(5, 50, node_diam);
        node_diam_slider.setBounds(150, 23, 120, 20);
        node_diam_slider.addChangeListener(new SliderListener());
        add(node_diam_slider);
        
        arc_height_slider = new JSlider(10, 100, arc_height);
        arc_height_slider.setBounds(150, 63, 120, 20);
        arc_height_slider.addChangeListener(new SliderListener());
        add(arc_height_slider);
        
        net_scale_slider = new JSlider(10, 200, net_size);
        net_scale_slider.setBounds(150, 183, 120, 20);
        net_scale_slider.addChangeListener(new SliderListener());
        add(net_scale_slider);
        
        // Чекбоксы
        show_name_cb = new JCheckBox("Показать имя", show_name);
        show_name_cb.setBounds(20, 100, 280, 20);
        show_name_cb.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                show_name_demo = e.getStateChange() == 1;
                graph_demo.show_name = show_name_demo;
                graph_demo.repaint();
            }
        });
        add(show_name_cb);
        
        show_net_cb = new JCheckBox("Показать сетку", show_net);
        show_net_cb.setBounds(20, 140, 280, 20);
        show_net_cb.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                show_net_demo = e.getStateChange() == 1;
                graph_demo.show_net = show_net_demo;
                graph_demo.repaint();
            }
        });
        add(show_net_cb);
        
        // Выбор цвета сетки
        JDialog this_dialog = this;
        net_color_choose = new JPanel();
        net_color_choose.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        net_color_choose.setBounds(100, 215, 30, 30);
        net_color_choose.setBackground(net_color);
        net_color_choose.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent e) {
                Color color = JColorChooser.showDialog(this_dialog, "Выбор цвета сетки", net_color_choose.getBackground());
                net_color_choose.setBackground(color);
                graph_demo.setNetColor(color);
                graph_demo.repaint();
            }
        });
        add(net_color_choose);
        
        // Кнопки
        okBtn = new JButton("OK");
        okBtn.setBounds(20, 260, 70, 20);
        okBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                node_diam = node_diam_slider.getValue();
                arc_height = arc_height_slider.getValue();
                show_name = show_name_demo;
                show_net = show_net_demo;
                net_size = net_scale_slider.getValue();
                net_color = net_color_choose.getBackground();
                

                parent_frame.updateGraph(node_diam, arc_height / 10.0, show_name, show_net, net_size, net_color);
                setVisible(false);
            }
        });
        add(okBtn);
        
        canselBtn = new JButton("Отмена");
        canselBtn.setBounds(110, 260, 80, 20);
        canselBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        });
        add(canselBtn);
        
        // Демо графа
        graph_demo = new GraphComponent();
        graph_demo.setBounds(300, 20, 250, 250);
        graph_demo.addElem(50, 125);
        graph_demo.addElem(200, 125);
        graph_demo.setLetter("a");
        graph_demo.addArrow(50, 125);
        graph_demo.fixArrow(200, 125);
        graph_demo.addArrow(200, 125);
        graph_demo.fixArrow(50, 125);
        add(graph_demo);
        
        setVisible(false);
    }
    
    public void setGraphFont(Font font) {
        graph_demo.setLetterFont(font);
    }
    
    @Override
    public void setVisible(boolean b) {
        
        
        
        super.setVisible(b);
    }
    
    private class SliderListener implements ChangeListener {

        @Override
        public void stateChanged(ChangeEvent e) {
            JSlider slider = (JSlider)e.getSource();
                        
            if (slider.equals(node_diam_slider)) {
                int value = slider.getValue();
                graph_demo.setNodeDiam(value);
            }
            else if (slider.equals(arc_height_slider)) {
                double value = slider.getValue();
                graph_demo.setArcHeight(value / 10);
            }
            else if (slider.equals(net_scale_slider)) {
                int value = slider.getValue();
                graph_demo.setNetSize(value);
            }
            
            graph_demo.repaint();
        }
        
    }
    
}
