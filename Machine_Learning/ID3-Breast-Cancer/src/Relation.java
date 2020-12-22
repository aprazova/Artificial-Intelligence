public class Relation {
    private String relationValue;
    private Node nextNode;

    Relation(Node node, String relation){
        this.nextNode = node;
        this.relationValue = relation;
    }

    public Node getNextNode() {
        return this.nextNode;
    }

    public boolean hasRelationValue(String relationValue){
        return this.relationValue.equals(relationValue);
    }
}
