
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Public class Load.
 * 
 */
public class Load {

  /**
   * Reads the file and returns ArrayList of instructions.
   * 
   */
  public static ArrayList<String[]> loadFile(String file) {
    ArrayList<String[]> instructionList = new ArrayList<>();
    // Read the file
    try {
      File inputFile = new File("src/main/resources/" + file);
      Scanner myReader = new Scanner(inputFile, "UTF-8");

      while (myReader.hasNextLine()) {
        String data = myReader.nextLine();
        // Add instructions to the instructionList
        instructionList.add(data.split(";"));
      }
      myReader.close();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    return instructionList;
  }

}
