package project;

//import java.util.ArrayList;


public class Node {
    
//    private ArrayList<Connection> connections;    
    private int x, y;
    public boolean enter;
    public boolean exit;
    
    public Node(int x, int y) {
        this.x = x;
        this.y = y;
//        connections = new ArrayList<>();
        enter = false;
        exit = false;
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
    
//    public void connectTo(Node other) {
//        connections.add(other);
//    }
//    
//    public void disconnectFrom(int i) {
//        connections.remove(i);
//    }
//    
//    public void disconnectLast() {
//        if (!connections.isEmpty())
//            connections.remove(connections.size()-1);
//    }
//    
//    public Node[] getConnections() {
//        return connections.toArray(new Node[connections.size()]);
//    }

}