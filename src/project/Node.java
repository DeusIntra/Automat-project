package project;

import java.awt.Color;


public class Node {
       
    private int x, y;
    private Color color;
//    private String name;
    public boolean enter;
    public boolean exit;
    
    public Node(int x, int y) {
        this.x = x;
        this.y = y;
        enter = false;
        exit = false;
        color = Color.BLACK;
    }
    
    public int getX() {
        return this.x;
    }
    
    public int getY() {
        return this.y;        
    }
    
    public void set(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    public Color getColor() {
        return color;
    }
    
    public void setColor(Color c) {
        color = c;
    }

}