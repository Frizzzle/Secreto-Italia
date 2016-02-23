package mdtu.com.secretoitalia.Model;

import java.util.ArrayList;

/**
 * Created by koctyabondar on 9/8/15.
 */
public class InventoryItem {
    public String name;
    public String objectId;
    public String url;
    public String url1;
    public String url2;
    public String price;
    public String finalPrice;
    public String secretoId;
    public String size;
    public String count = "1";



    public InventoryItem(InventoryItem another) {
        this.name = another.name;
        this.objectId = another.objectId;
        this.url = another.url;
        this.url1 = another.url1;
        this.url2 = another.url2;
        this.price = another.price;
        this.finalPrice = another.finalPrice;
        this.secretoId = another.secretoId;
        this.size = another.size;
        this.count = another.count;
    }
    public InventoryItem() {

    }
    public InventoryItem(String name , String objectId ,String url,String url1,String url2,String price,String finalPrice,String secretoId) {
        this.name = name;
        this.objectId = objectId;
        this.url = url;
        this.url1 = url1;
        this.url2 = url2;
        this.price = price;
        this.finalPrice = finalPrice;
        this.secretoId = secretoId;
    }
    public String getPrice() {
        double finPrice = Double.parseDouble(finalPrice.toString());
        if(finPrice == 0.0) {
            return price;
        }else {
            return finalPrice;
        }
    }
}
