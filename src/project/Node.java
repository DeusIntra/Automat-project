package project;

import java.awt.Color;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Node {
       
    private int x, y;
    private Color color;
    private String name;
    public boolean enter;
    public boolean exit;
    
    public Node(int x, int y) {
        this.x = x;
        this.y = y;
        enter = false;
        exit = false;
        color = Color.WHITE;
        setName();
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
    
    private void setName() {
        Pattern pattern = Pattern.compile(".*@(.*)");
        Matcher matcher = pattern.matcher(this.toString());
        matcher.find();
        String str = matcher.group(1);
        name = str;
    }
    
    public String getName() {
        return name;
    }

}