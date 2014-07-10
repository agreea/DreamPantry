/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dreampantrycalculator;

import java.math.BigDecimal;
import java.util.Vector;

/**
 *
 * @author Agree Ahmed
 */
public class GroceryItem {
    String name;
    int quantity;
    BigDecimal totalPrice;
    Vector<String> peopleIn;
    BigDecimal costPerPerson;
    
    GroceryItem(String givenName){
        name = givenName;
        peopleIn = new Vector();
    }
    public void addPerson(String person){
        peopleIn.add(person);
    }
    public void setTotalPrice(BigDecimal price){
        totalPrice = price;
    }
    public void setQuant(int quant){
        quantity = quant;
    }
}
