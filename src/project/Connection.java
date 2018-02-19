package project;

import java.awt.Color;


public class Connection {
    
    private final Node from;
    private final Node to;
    private String weight;
    private Color color;
    public boolean has_reverse;
    
    public Connection(Node from, Node to, String weight) {
        this.from = from;
        this.to = to;
        this.weight = weight;
        has_reverse = false;
        color = Color.BLACK;
    }
    
    public Connection(Node from, Node to) {
        this.from = from;
        this.to = to;
        this.weight = "";
    }
    
    public void setWeight(String weight) {
        this.weight = weight;
    }
    
    public Node getFrom() {
        return from;
    }
    
    public Node getTo() {
        return to;
    }
    
    public String getWeight() {
        return weight;
    }
    
    public Color getColor() {
        return color;
    }
    
    public void setColor(Color c) {
        color = c;
    }
    
}
