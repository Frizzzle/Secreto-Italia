package mdtu.com.secretoitalia.Model;

/**
 * Created by koctyabondar on 9/8/15.
 */
public class Category  {
    public String name;
    public String objectId;
    public String categoryId;
    public String subcategoryId;
    public Boolean finalLevel;

    public Category(String objectId ,String name , String categoryId,String subcategoryId,Boolean finalLevel) {
        this.objectId = objectId;
        this.name = name;
        this.categoryId = categoryId;
        this.subcategoryId = subcategoryId;
        this.finalLevel = finalLevel;
    }

}
