
import java.util.ArrayList;

/**
 * Public class App.
 * 
 */
public class App {
  private Memory memory;
  private ArrayList<String[]> instructionList;
  private ArrayList<String> errors = new ArrayList<>();
  private ArrayList<Integer> failedIDs = new ArrayList<>();
  private StringBuilder output = new StringBuilder();
  private StringBuilder IntermediateOutput = new StringBuilder();

  private StringBuilder allocatedBlocks = new StringBuilder();
  private StringBuilder freeBlocks = new StringBuilder();

  /**
   * Loads the data in to the Memory.
   * 
   */
  public void loadFile(String file) {
    instructionList = Load.loadFile(file); // Read the file and return the content

    String[] methods = { "F", "B", "W" }; // FIRST, BEST AND WORST FIT

    int saveInterMediateFileCount = 1;
    boolean intermediateCount = false;

    // -------------------------------------------------------------------------//
    int method = 0; // FIRST, BEST AND WORST FIT
    while (method < 3) {
      // GET INSTRUCTIONS
      for (String[] i : instructionList) {
        try {
          int initialMemorySize = Integer.parseInt(i[0].toString());
          // If instruction = number
          if (initialMemorySize == (int) initialMemorySize) {
            memory = new Memory(initialMemorySize);
            output = new StringBuilder();
            allocatedBlocks = new StringBuilder();
            freeBlocks = new StringBuilder();
            errors = new ArrayList<>();
            failedIDs = new ArrayList<>();
          }
        } catch (NumberFormatException nfe) {
          if (i[0].toString().equals("A")) {
            int memoryToBeAllocated = Integer.parseInt(i[2]);
            Integer blockId = Integer.parseInt(i[1]);
            if (method == 0) { // FIRST FIT ************************************
              if (!memory.Allocation(blockId, memoryToBeAllocated, methods[0])) {
                // ERRORS------------------
                errors.add("A;" + instructionList.indexOf(i) + ";" + memory.getLargest() + "\n");
                failedIDs.add(blockId);
              }
            }
            if (method == 1) { // BEST FIT $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
              if (!memory.Allocation(blockId, memoryToBeAllocated, methods[1])) {
                // ERRORS------------------
                errors.add("A;" + instructionList.indexOf(i) + ";" + memory.getLargest() + "\n");
                failedIDs.add(blockId);
              }
            }
            if (method == 2) { // WOrST FIT ####################################
              if (!memory.Allocation(blockId, memoryToBeAllocated, methods[2])) {
                // ERRORS------------------
                errors.add("A;" + instructionList.indexOf(i) + ";" + memory.getLargest() + "\n");
                failedIDs.add(blockId);
              }

            }
          } else if (i[0].toString().equals("D")) { // DEALOCATION
            Integer blockId = Integer.parseInt(i[1]);
            if (!memory.deAllocation(blockId)) {
              if (failedIDs.contains(blockId)) {
                errors.add("D;" + instructionList.indexOf(i) + ";1\n");
              } else {
                errors.add("D;" + instructionList.indexOf(i) + ";0\n");
              }
            }
          } else if (i[0].toString().equals("C")) { // COMPACT MEMORY
            // Move all allocated block towards the lowest address.
            memory.compactMemory();

          } else if (i[0].toString().equals("O")) { // INTERMEDIATE OUTPUT
            // SAVE TO INTERMEDIATE OUTPUT
            if (intermediateCount == false) {
              IntermediateOutput = new StringBuilder();
              allocatedBlocks = new StringBuilder();
              freeBlocks = new StringBuilder();
              intermediateCount = true;
              appendStringBuilder(freeBlocks, allocatedBlocks, IntermediateOutput, method);
              // Save
              Save.saveFile(IntermediateOutput, file, false, saveInterMediateFileCount);
            } else if (intermediateCount == true) {
              IntermediateOutput = new StringBuilder();
              allocatedBlocks = new StringBuilder();
              freeBlocks = new StringBuilder();
              saveInterMediateFileCount++; // increment
              appendStringBuilder(freeBlocks, allocatedBlocks, IntermediateOutput, method);
              // Save
              Save.saveFile(IntermediateOutput, file, false, saveInterMediateFileCount);
              saveInterMediateFileCount--; // increment
              intermediateCount = false;
            }
          }
        }
      }
      // SAVE TO FINAL OUTPUT
      output = new StringBuilder();
      allocatedBlocks = new StringBuilder();
      freeBlocks = new StringBuilder();
      appendStringBuilder(freeBlocks, allocatedBlocks, output, method);
      Save.saveFile(output, file, true, 0);
      // Move to the next method
      method++;

    }

  }

  /*
   * Stringbuilder Method
   * 
   */
  public void appendStringBuilder(StringBuilder freeB, StringBuilder allocatedB, StringBuilder output, int method) {
    // Adding to StringBuilder
    if (method == 0) {
      output.append("First fit\n");
    } else if (method == 1) {
      output.append("Best fit\n");
    } else if (method == 2) {
      output.append("Worst fit\n");
    }

    for (Partition partition : memory.gePartitions()) {
      if (partition.getID() != null) {
        allocatedB.append(partition.getID() + ";"
            + partition.getStartingMemoryAddress() + ";"
            + (partition.getStartingMemoryAddress() + partition.getSize() - 1) + "\n");
      } else {
        freeB.append(partition.getStartingMemoryAddress() + ";"
            + (partition.getStartingMemoryAddress() + partition.getSize() - 1) + "\n");

      }
    }
    output.append("Allocated blocks: \n");
    output.append(allocatedB);
    output.append("Free blocks: \n");
    output.append(freeB);
    output.append("Fragmentation: \n");
    output.append(String.format("%,.6f", memory.getExternalFragmentation()) + "\n");
    output.append("Errors:\n");

    if (errors.isEmpty()) {
      output.append("None\n");
    } else {
      for (String e : errors) {
        output.append(e);
      }
    }
    output.append("\n");

  }

}