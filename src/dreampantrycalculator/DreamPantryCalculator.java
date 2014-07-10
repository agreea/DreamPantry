/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dreampantrycalculator;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import jxl.Cell;
import jxl.CellType;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import dreampantrycalculator.GroceryItem;
import dreampantrycalculator.Person;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Vector;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import java.util.Date;
import jxl.write.WritableCell;
import jxl.write.WritableSheet;
import jxl.write.Label;
import jxl.write.Number;
        
/**
 *This program takes an excel spread sheet .xls file as input in the following format:
 *[ITEM NAME][TOTAL PRICE][QUANTITY][PERSON-IN0][PERSON-IN1]...[PERSON-INN][EMPTYSPACE||ERROR]
 *And creates an itemized receipt of how much each person owes for the ingredients they purchased.
 * @author Agree Ahmed
 */
public class DreamPantryCalculator {

    /**
     * @param args the command line arguments
     */
    private static Vector<GroceryItem> items;
    private static Vector<Person> people;
    public static void main(String[] args) throws IOException, BiffException {
        items = new Vector();
        people = new Vector();
        // TODO code application logic here
        Scanner scan = new Scanner(System.in);
        System.out.println("Please enter receipt's file location: ");
        String workBookName = scan.nextLine();
        getFile(workBookName);
        constructPeopleList();
        //printItemsList();
        printPeopleList(workBookName);
    }

  public static void getFile(String inputFile) throws IOException, BiffException  {
    File inputWorkbook = new File(inputFile);
    Workbook w;
    try {
      w = Workbook.getWorkbook(inputWorkbook);
      // Get the first sheet
      Sheet sheet = w.getSheet(0);
      // Loop over first 10 column and lines
      for (int j = 0; j < sheet.getRows(); j++) {//reads it row-by-row.
        items.add(getItemFromFile(sheet, j));
        System.out.println("that was item " + items.size());
      }
    } catch (BiffException e) {
      e.printStackTrace();
    }
  }
  
  private static GroceryItem getItemFromFile(Sheet sheet, int row){
      GroceryItem newItem = new GroceryItem(sheet.getCell(0, row).getContents());
      if(sheet.getCell(0, row).getType() == CellType.EMPTY)//if the first column is empty, the row's blank
          return null;// so return null.
      for (int i = 1; i < sheet.getColumns(); i++) {//for each column in each row...
          Cell cell = sheet.getCell(i, row);
          CellType type = cell.getType();//get its type
          String contents = cell.getContents();
          //System.out.println(contents);
          //if you're at the end of the columns...
          if(type == CellType.EMPTY || i == sheet.getColumns()-1){
              System.out.println("Just added " + newItem.name + ". With " + newItem.peopleIn.size() + " people in. With total cost " + newItem.totalPrice);
              BigDecimal totalInPeople = new BigDecimal(newItem.peopleIn.size());
              newItem.costPerPerson = newItem.totalPrice.divide(totalInPeople, 2, 0);
              System.out.println(newItem.name +  " Cost per person is " + newItem.costPerPerson);
              //we've ended the current item's info. Find the cost per person by dividing
              //the total cost by the cost of people who are in.
              //and on to the next one.
              break;
          }
          if (type == CellType.LABEL) {//if it's a label...
              newItem.addPerson(contents);//then it must be a person. Add it to people who are in.
          }
            else if (type == CellType.NUMBER) {//if it's a number...
              BigDecimal number = new BigDecimal(contents);
              if(sheet.getCell(i-1, row).getType() == CellType.LABEL){
                  newItem.setTotalPrice(number);//set the new item's total price.
                  }
              else{//if the last cell wasn't a label but this cell IS a number...
                  newItem.setQuant(number.intValue());//set the item's quantity.
              }
          }
        }
      return newItem;
   }
  
  //Takes a cell's contents and returns only the numbers (with decimal points) and letters.
  private static String getCleanedCell(String givenString){
      String cleaned = ""; //cleaned string
      for(int i = 0; i < givenString.length(); i++){
          char c = givenString.charAt(i);
          if(Character.isLetter(c) || Character.isDigit(c) || c == '.'){
              //if the current character is a letter or number...
              cleaned += c;//add the char to the cleaned string
          }
      }
      return cleaned;
  }
  //Runs through the items list and finds what people are in on what items.
  //if a person is on an item, it adds that item to the person's list.
  private static void constructPeopleList(){
      GroceryItem gItem;
      boolean foundEm;//did you find the person
      for (int i = 0; i < items.size(); i++){//items loop I is for ITEMS
          gItem = items.elementAt(i);
          for(int pi = 0; pi < gItem.peopleIn.size(); pi++){//(peopleIn on Item) loop J is for PEOPLEIN
              foundEm = false;//since you just started comparing the name of the person in to the names on your list
              //you haven't foundEm yet.
              if(people.isEmpty()){//if the Vector is empty
                  Person human = new Person(gItem.peopleIn.elementAt(pi));//create the first person from the first name
                  human.addItem(gItem);//then add the item they're in on to them.
                  people.add(human);//then add human
                  foundEm = true;
              }
              else {//if it's not empty, check the name in the grocery items against 
                  for(int k = 0; k < people.size(); k++){//total people loop. K is for TOTAL people (special K and Total cereal)
                  //if there's a match between the person who's in (PI) and a person on our list (K)...
                  if (gItem.peopleIn.elementAt(pi) == people.elementAt(k).name){
                      people.elementAt(k).addItem(gItem);//add the item to the person's list.
                      System.out.println(people.elementAt(k).name + " purchased " + gItem.name);
                      foundEm = true;
                  }
                }
              }
              if(!foundEm){//if you still haven't found the person
                  Person newPerson = new Person (gItem.peopleIn.elementAt(pi));
                  //create a new one and add their item to them and add the person to the list.
                  newPerson.addItem(gItem);
                  people.add(newPerson);
              }
          }
      }//once you finish adding everyone's items to their list
      getTotalDebts();
  }
  private static void getTotalDebts(){
      for (int i = 0; i < people.size(); i++){
          people.elementAt(i).getTotalOwed();
      }
  }
  public static void printItemsList(){
      for (int i = 0; i < items.size(); i++){
          System.out.println("======");
          System.out.println(items.elementAt(i).name + ' ');//print item name
          for (int j = 0; j < items.elementAt(i).peopleIn.size(); j++){//print all names-in
              System.out.println(items.elementAt(i).peopleIn.elementAt(j));
          }
          System.out.println("Total price: " + items.elementAt(i).totalPrice);
          System.out.println("Quantity: " + items.elementAt(i).quantity);
      }
  }
  //Prints a list of what each person has purchased and thus owes.
  //Also creates an xls file where each sheet is an itemized receipt for a person.
  public static void printPeopleList(String workBookName) throws IOException{
  //Get the date, set the file path as the receipt folder plus the basics of the date.
  Date d = new Date();
  String directoryAddress = "\\Users\\lenovo\\Documents\\GEORGETOWN\\Receipts\\";
  String date = d.toString().replace(':', '.');
  String fileName = directoryAddress + date + ".xls";
  System.out.println(fileName);
  try {
    File file = new File(fileName);
    Path fileDest = Paths.get(fileName);
    if(!file.exists()){//determine the file and if it doesn't exist, make it.
            Files.createFile(fileDest);
        }    
    WritableWorkbook workbook = Workbook.createWorkbook(file);
    for (int i = 0; i < people.size(); i++){//for each person...
        Person currentPerson = people.elementAt(i);
        workbook.createSheet(currentPerson.name, i);
        Label l;
        Number n;
        int totalItems = currentPerson.items.size();
        WritableSheet mySheet = workbook.getSheet(i);//make them their own sheet.
        WritableCell cell = mySheet.getWritableCell(0, 0);
        for(int row = 0; row < totalItems; row++){//each item gets a row
            GroceryItem currentItem = currentPerson.items.elementAt(row);
            l = new Label(0, row, currentItem.name);
            cell = (WritableCell) l;
            mySheet.addCell(cell);
            n = new Number(1, row, currentItem.costPerPerson.doubleValue());
            cell = (WritableCell) n;
            mySheet.addCell(cell);
        }
        cell = mySheet.getWritableCell(0,totalItems);
        Label last = new Label(0, totalItems, "Total");
        cell = (WritableCell) last;
        mySheet.addCell(cell);
        Number total = new Number (1, totalItems, currentPerson.getTotalOwed().doubleValue());
        cell = (WritableCell) total;
        mySheet.addCell(cell);
        
    }
    workbook.write();
    workbook.close();
}
catch (WriteException e) {

}

      for (int i = 0; i < people.size(); i++){
          System.out.println("=======");
          System.out.println("Person: " + people.elementAt(i).name);
          for (int j = 0; j < people.elementAt(i).items.size(); j++){
              GroceryItem grocery = people.elementAt(i).items.elementAt(j);
              System.out.println(grocery.name + " " + grocery.costPerPerson);
          }
          System.out.println("~~~~~~ Total Owed: " + people.elementAt(i).getTotalOwed());
      }
  }
}
