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
public class Person {
    String name;
    Vector<GroceryItem> items;
    private BigDecimal totalOwed;
    private boolean isTotalOwedCalced;
    
    Person(String givenName){
        name = givenName;
        totalOwed = new BigDecimal(0);
        items = new Vector();
        isTotalOwedCalced = false;
    }
    public void addItem(GroceryItem item){
        items.add(item);
    }
    public BigDecimal getTotalOwed(){
        if(isTotalOwedCalced){
            System.out.println("Plush (" + name + ") safe he think");
            return totalOwed;
        }
        else{
            System.out.println("Calculating total for " + name+ ". (S)he has " + items.size() + " items");
            for (int i = 0; i < items.size(); i++){
            //for each item they're in on, add to the total owed cost per person.
                System.out.println("Current item: " + items.elementAt(i).name);
                totalOwed = totalOwed.add(items.elementAt(i).costPerPerson);
            }
            isTotalOwedCalced = true;
            return totalOwed;
        }
    }
}
