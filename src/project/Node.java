package project;

import java.util.ArrayList;


public class Node {
    
    private ArrayList<Node> connections;    
    private int x, y;
//    private boolean enter;
//    private boolean exit;
    
    public Node(int x, int y) {
        this.x = x;
        this.y = y;
        connections = new ArrayList<>();
    }
    
    public int getX() {
        return this.x;
    }
    
    public int getY() {
        return this.y;        
    }
    
    public Node set(int x, int y) {
        this.x = x;
        this.y = y;
        return this;
    }
    
    public void connectTo(Node other) {
        connections.add(other);
    }
    
    public void disconnectFrom(int i) {
        connections.remove(i);
    }
    
    public Object getConnections() {
        return connections.toArray();
    }
    
}