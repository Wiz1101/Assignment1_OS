
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * Public class Save.
 * 
 */
public class Save {

  /**
   * Saves the data about the members.
   * 
   */
  public static void saveFile(StringBuilder output, String file, boolean isFinal, int count) {
    //
    String fileName = file.replace(".in", "");
    String fileNameOut;
    if (!isFinal) {
      fileNameOut = String.format("src/main/resources/outputs/%s.out", fileName);
      fileNameOut += count;
      writeToFile(fileNameOut, output, isFinal);

    } else {
      fileNameOut = String.format("src/main/resources/outputs/%s.out", fileName);
      writeToFile(fileNameOut, output, isFinal);

    }
  }

  private static void writeToFile(String fileNameOut, StringBuilder output, boolean isFinal) {
    File outputFile = new File(fileNameOut);
    //
    try {
      FileOutputStream fileStream = new FileOutputStream(outputFile, true);
      OutputStreamWriter writer = new OutputStreamWriter(fileStream, "UTF-8");

      writer.write(output.toString());
      if (!isFinal) {
        System.out.println("Successfully wrote to the intermediate file.");
      } else {
        System.out.println("Successfully wrote to the final file.");
      }
      writer.close();

    } catch (IOException e) {
      System.out.println("An error occurredRe.");
      e.printStackTrace();
    }

  }

}
