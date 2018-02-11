package project;


public class Node {
       
    private int x, y;
    public boolean enter;
    public boolean exit;
    
    public Node(int x, int y) {
        this.x = x;
        this.y = y;
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

}