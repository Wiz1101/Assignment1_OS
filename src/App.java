import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class App {

  public static void main(String[] args) throws IOException {
    String fileName = "3-madoutputter.in"; // TODO: change this !
    ArrayList<String> operations = readOperations(fileName);
    ArrayList<StringBuilder> outputs = new ArrayList<>();
    StringBuilder finalOutput = new StringBuilder();

    FitManager("First", operations, outputs, finalOutput);
    FitManager("Best", operations, outputs, finalOutput);
    FitManager("Worst", operations, outputs, finalOutput);

    String outputName = clearOutputs(fileName);
    writeIntermediateOutputs(outputName, outputs);
    writeOutput(outputName, finalOutput.toString());
  }

  private static void writeIntermediateOutputs(String fileName, ArrayList<StringBuilder> outputs) throws IOException {
    for (StringBuilder stringBuilder : outputs) {
      int num = outputs.indexOf(stringBuilder) + 1;
      String strNum = Integer.toString(num);
      String fileOut = fileName + strNum;
      writeOutput(fileOut, stringBuilder.toString());
    }
  }

  /**
   * 
   * @param fit First/Best/Worst
   */
  private static MemoryManager FitManager(String fit, ArrayList<String> operations, ArrayList<StringBuilder> outputs,
      StringBuilder finalOutput) throws NumberFormatException, IOException {

    int intermediateCounter = 0;
    MemoryManager memManager = new MemoryManager(Integer.parseInt(operations.get(0)));

    for (int i = 1; i < operations.size(); i++) {
      String[] args = operations.get(i).split(";");
      if (args[0].equals("A")) {
        Integer id = Integer.parseInt(args[1]);
        int size = Integer.parseInt(args[2]);
        switch (fit) {
          case "First":
            memManager.firstFit(id, size, i);
            break;
          case "Best":
            memManager.bestFit(id, size, i);
            break;
          case "Worst":
            memManager.worstFit(id, size, i);
            break;
          default:
            break;
        }
      } else if (args[0].equals("D")) { // if command is D
        Integer id = Integer.parseInt(args[1]);
        memManager.deallocate(id, i);
      } else if (args[0].equals("C")) { // if command is C
        memManager.compact();
      } else if (args[0].equals("O")) { // if command is O
        try {
          outputs.get(intermediateCounter).append(fit + " fit\n");
          outputs.get(intermediateCounter).append(memManager.output());
        } catch (Exception e) {
          outputs.add(new StringBuilder());
          outputs.get(intermediateCounter).append(fit + " fit\n");
          outputs.get(intermediateCounter).append(memManager.output());
        }
        intermediateCounter++;
      }
    }
    finalOutput.append(fit + " fit\n");
    finalOutput.append(memManager.output());
    return memManager;
  }

  private static ArrayList<String> readOperations(String path) {
    ArrayList<String> operations = new ArrayList<>();
    try {
      File file = new File(path);
      Scanner fileReader = new Scanner(file);
      while (fileReader.hasNextLine()) {
        String instruction = fileReader.nextLine();
        operations.add(instruction);
      }
      fileReader.close();
    } catch (FileNotFoundException e) {
      System.out.println("No such file.");
    }
    return operations;
  }

  private static void writeOutput(String fileName, String output) throws IOException {
    BufferedWriter writer = new BufferedWriter(new FileWriter("outputs/" + fileName));
    writer.write(output);
    writer.close();
  }

  private static String clearOutputs(String fileName) {
    File parentFolder = new File("outputs");
    String outPutName = "";
    for (File file : parentFolder.listFiles()) {
      outPutName = fileName.replace(".in", ".out");
      String path = file.getPath().replace("\\", "/");
      if (path.contains(outPutName)) {
        file.delete();
      }
    }
    return outPutName;

  }

}