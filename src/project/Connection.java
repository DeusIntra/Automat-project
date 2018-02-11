package project;


public class Connection {
    
    private Node from;
    private Node to;
    private String weight;
    
    public Connection(Node from, Node to, String weight) {
        this.from = from;
        this.to = to;
        this.weight = weight;
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
