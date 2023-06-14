import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.ListIterator;

public class MemoryManager {
  private Integer initialSize;
  private ArrayList<MemoryBlock> freeBlocks;
  private ArrayList<MemoryBlock> allocatedBlocks;
  private ArrayList<Integer> idFails;
  private ArrayList<String> errors;

  public MemoryManager(Integer size) throws NumberFormatException, IOException {
    initialSize = size;
    freeBlocks = new ArrayList<>();
    freeBlocks.add(new MemoryBlock(null, 0, size));
    allocatedBlocks = new ArrayList<>();
    idFails = new ArrayList<>();
    errors = new ArrayList<>();
  }

  public void firstFit(Integer id, int size, int lineNum) {
    MemoryBlock blockToAllocate = null;
    for (MemoryBlock block : freeBlocks) {
      if (block.getSize() >= size) {
        if ((blockToAllocate == null) ||
            (blockToAllocate.getStart() > block.getStart())) {
          blockToAllocate = block;
        }
      }
    }
    tryAllocateBlock(id, size, lineNum, blockToAllocate);
  }

  public void bestFit(Integer id, int size, int lineNum) {
    MemoryBlock blockToAllocate = null;
    for (MemoryBlock block : freeBlocks) {
      if (block.getSize() >= size) {
        if ((blockToAllocate == null) ||
            (blockToAllocate.getSize() > block.getSize()) ||
            (blockToAllocate.getSize() == block.getSize() && blockToAllocate.getStart() > block.getStart())) {
          blockToAllocate = block;
        }
      }
    }

    tryAllocateBlock(id, size, lineNum, blockToAllocate);
  }

  public void worstFit(Integer id, int size, int lineNum) {
    MemoryBlock blockToAllocate = null;
    for (MemoryBlock block : freeBlocks) {
      if (block.getSize() >= size) {
        if (blockToAllocate == null ||
            (blockToAllocate.getSize() < block.getSize()) ||
            (blockToAllocate.getSize() == block.getSize() && blockToAllocate.getStart() > block.getStart())) {
          blockToAllocate = block;
        }
      }
    }
    tryAllocateBlock(id, size, lineNum, blockToAllocate);
  }

  private void tryAllocateBlock(Integer id, int size, int lineNum, MemoryBlock blockToAllocate) {
    if (blockToAllocate != null) {
      allocate(blockToAllocate, id, size);
      return;
    }

    errors.add("A;" + lineNum + ";" + getLargestFreeBlockSize());
    idFails.add(id);
  }

  public void allocate(MemoryBlock block, Integer id, int size) {
    MemoryBlock allocatedBlock = new MemoryBlock(id, block.getStart(), size);
    allocatedBlocks.add(allocatedBlock);

    int remainingSize = block.getSize() - allocatedBlock.getSize();
    if (remainingSize > 0) {
      MemoryBlock freeBlock = new MemoryBlock(null, allocatedBlock.getEnd() + 1, remainingSize);
      freeBlocks.add(freeBlock);
    }
    freeBlocks.remove(block);
  }

  public void deallocate(Integer id, int lineNum) {
    for (MemoryBlock block : allocatedBlocks) {
      if (block.getId() == id) {
        freeBlocks.add(block);
        block.setId(null);
        Collections.sort(freeBlocks, Comparator.comparingInt(MemoryBlock::getStart));
        ListIterator<MemoryBlock> iterator = freeBlocks.listIterator();
        MemoryBlock previous = null;
        while (iterator.hasNext()) {
          MemoryBlock current = iterator.next();
          if (previous != null && previous.getEnd() == current.getStart() - 1) {
            previous.setSize(previous.getSize() + current.getSize());
            iterator.remove();
            freeBlocks.remove(current);
          } else {
            previous = current;
          }
        }
        allocatedBlocks.remove(block);
        return;
      }
    }
    if (idFails.contains(id)) {
      errors.add("D;" + lineNum + ";" + "1");
    } else {
      errors.add("D;" + lineNum + ";" + "0");
    }
  }

  public void compact() throws NumberFormatException, IOException {
    Collections.sort(allocatedBlocks, Comparator.comparingInt(MemoryBlock::getStart));
    int size = 0;
    for (MemoryBlock allocatedBlock : allocatedBlocks) {
      allocatedBlock.setStart(size);
      size += allocatedBlock.getSize();
    }
    freeBlocks.clear();
    freeBlocks.add(new MemoryBlock(null, size, initialSize - size));
  }

  public String fragmentation() {
    freeBlocks.sort((a, b) -> b.getSize() - a.getSize());
    int largestBlock = getLargestFreeBlockSize();

    int freeSpace = 0;
    for (MemoryBlock block : freeBlocks) {
      freeSpace += block.getSize();
    }

    double fragmentation = 1 - ((double) largestBlock / freeSpace);
    return String.format("%.6f\n", fragmentation);
  }

  public int getLargestFreeBlockSize() {
    int largestBlockSize = 0;
    for (MemoryBlock block : freeBlocks) {
      if (block.getSize() > largestBlockSize) {
        largestBlockSize = block.getSize();
      }
    }
    return largestBlockSize;
  }

  public StringBuilder output() {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("Allocated blocks\n");
    Collections.sort(allocatedBlocks, Comparator.comparingInt(MemoryBlock::getId));
    for (MemoryBlock block : allocatedBlocks) {
      stringBuilder.append(block.getId() + ";" + block.getStart() + ";" + block.getEnd() + "\n");
    }

    stringBuilder.append("Free blocks\n");
    Collections.sort(freeBlocks, Comparator.comparingInt(MemoryBlock::getStart));
    for (MemoryBlock block : freeBlocks) {
      stringBuilder.append(block.getStart() + ";" + block.getEnd() + "\n");
    }

    stringBuilder.append("Fragmentation\n" + fragmentation());

    stringBuilder.append("Errors\n");

    if (errors.isEmpty()) {
      stringBuilder.append("None\n");
    } else {
      for (String error : errors) {
        stringBuilder.append(error + "\n");
      }
    }
    stringBuilder.append("\n");
    return stringBuilder;
  }
}