package project;


public class Connection {
    
    private final Node from;
    private final Node to;
    private String weight;
    public boolean has_reverse;
    
    public Connection(Node from, Node to, String weight) {
        this.from = from;
        this.to = to;
        this.weight = weight;
        has_reverse = false;
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
    
}
