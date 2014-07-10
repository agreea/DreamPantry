/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dreampantrycalculator;

import java.io.File;
import java.io.IOException;
import jxl.Cell;
import jxl.CellType;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

/**
 *
 * @author Agree Ahmed
 */
public  class Reader {
  
  private static String inputFile;
  Reader(String fileName){
        inputFile = fileName;
    }

  public static void setInputFile(String inputFile) {
    inputFile = inputFile;
  }

  public static void read() throws IOException, BiffException  {
    File inputWorkbook = new File(inputFile);
    Workbook w;
    try {
      w = Workbook.getWorkbook(inputWorkbook);
      // Get the first sheet
      Sheet sheet = w.getSheet(0);
      // Loop over first 10 column and lines

      for (int j = 0; j < sheet.getColumns(); j++) {
        for (int i = 0; i < sheet.getRows(); i++) {
          Cell cell = sheet.getCell(j, i);
          CellType type = cell.getType();
          Boolean didTitle = false;
          if(type == CellType.ERROR) {
              didTitle = false;
          }
          if (type == CellType.LABEL) {
            if(!didTitle){ 
                didTitle = true;//You've hit the title      
            }
            System.out.println("I got a label "
                + cell.getContents());
          }
          if (type == CellType.NUMBER) {
            System.out.println("I got a number "
                + cell.getContents());
          }

        }
      }
    } catch (BiffException e) {
      e.printStackTrace();
    }
  }
}
