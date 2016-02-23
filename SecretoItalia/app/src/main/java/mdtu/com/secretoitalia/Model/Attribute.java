package mdtu.com.secretoitalia.Model;

/**
 * Created by koctyabondar on 9/10/15.
 */
public class Attribute {
    public String objectId;
    public String name;
    public int value;
    public int priority;

    public Attribute(int value ,String name , String objectId , int priority) {
        this.value = value;
        this.objectId = objectId;
        this.name = name;
        this.priority = priority;
    }
}
