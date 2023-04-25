import java.util.ArrayList;

public class Memory {
  private ArrayList<Partition> partitions;
  private int initialMemorySize;

  Memory(int initialMemorySize) {
    this.initialMemorySize = initialMemorySize;
    Partition mainMemory = new Partition(0, initialMemorySize);
    partitions = new ArrayList<Partition>();
    partitions.add(mainMemory);
  }

  public ArrayList<Partition> gePartitions() {
    return partitions;
  }

  public int getInitialMemorySize() {
    return initialMemorySize;
  }

  public void setInitialMemorySize(int initialMemorySize) {
    this.initialMemorySize = initialMemorySize;

    Partition mainMemory = new Partition(0, initialMemorySize);
    partitions = new ArrayList<Partition>();

    partitions.add(mainMemory);

  }

  public void removeInitialMemorySize(int initialMemorySize) {
    this.initialMemorySize = initialMemorySize;

    Partition mainMemory = new Partition(0, initialMemorySize);
    partitions = new ArrayList<Partition>();

    partitions.add(mainMemory);

  }

  public int getLargest() {
    int largestPartition = 0;
    for (Partition partition : partitions) {
      if (partition.getID() == null) {
        if (partition.getSize() > largestPartition) {
          largestPartition = partition.getSize();
        }
      }
    }
    return largestPartition;
  }

  public double getExternalFragmentation() {
    return externalFragmentation();
  }

  public boolean Allocation(int id, int memoryToBeAllocated, String method) {
    try {
      if (method.equals("F")) {
        if (!firstFit(id, memoryToBeAllocated)) {
          return false;
        }
      } else if (method.equals("B")) {
        if (!bestFit(id, memoryToBeAllocated)) {
          return false;
        }
      } else if (method.equals("W")) {
        if (!worstFit(id, memoryToBeAllocated)) {
          return false;
        }
      }
    } catch (Exception ex) {
      System.out.println(ex);
    }

    return true;

  }

  public boolean deAllocation(Integer id) {
    for (Partition partition : partitions) {
      if (partition.getID() == id) {
        partition.setID(null);
        return true;
      }
    }
    return false;
  }

  private boolean firstFit(int id, int memoryToBeAllocated) {
    for (Partition partition : partitions) {
      if (partition.getID() == null) {
        if (partition.getSize() >= memoryToBeAllocated) {
          partition.setID(id); // Set id for the block
          Partition remainingPartition = partition.allocate(memoryToBeAllocated);
          if (remainingPartition != null && remainingPartition.getSize() > 0) {
            partitions.add(remainingPartition);
          }
          return true;
        }
      }

    }
    return false;
  }

  private boolean bestFit(int id, int memoryToBeAllocated) {
    int index = -1, min = initialMemorySize;
    Partition bestFitPartition = partitions.get(0);
    for (Partition partition : partitions) {
      if (partition.getID() == null) {

        if ((partition.getSize() - memoryToBeAllocated >= 0) && (partition.getSize() - memoryToBeAllocated <= min)) {
          min = partition.getSize() - memoryToBeAllocated;
          bestFitPartition = partition;

          index = partitions.indexOf(bestFitPartition);
        }
      }

    }
    bestFitPartition.setID(id);
    bestFitPartition.setSize(memoryToBeAllocated);

    if (index != -1) {
      Partition remainingMemory = partitions.get(index).allocate(memoryToBeAllocated);
      remainingMemory.setSize(min);
      partitions.add(remainingMemory);
      return true;
    }
    return false;

  }

  private boolean worstFit(int id, int memoryToBeAllocated) {
    int index = -1, min = 0;
    Partition worstPartition = partitions.get(0);
    for (Partition partition : partitions) {
      if (partition.getID() == null) {

        if ((partition.getSize() - memoryToBeAllocated > 0) && (partition.getSize() - memoryToBeAllocated >= min)) {
          min = partition.getSize() - memoryToBeAllocated;
          worstPartition = partition;

          index = partitions.indexOf(worstPartition);
        }
      }

    }
    worstPartition.setID(id);
    worstPartition.setSize(memoryToBeAllocated);

    if (index != -1) {
      Partition remainingMemory = partitions.get(index).allocate(memoryToBeAllocated);
      remainingMemory.setSize(min);
      partitions.add(remainingMemory);
      return true;
    }
    return false;
  }

  private double externalFragmentation() {
    double largestPartition = 0.0;
    double totalMemo = 0.0;
    for (Partition partition : partitions) {
      if (partition.getID() == null) {
        if (partition.getSize() > largestPartition) {
          largestPartition = partition.getSize();
        }
        totalMemo += partition.getSize();
      }
    }
    return (1 - (largestPartition / totalMemo));
  }

  /**
   * Move all allocated blocks towards the lowest address.
   * 
   */
  public void compactMemory() {
    Partition firstPartition = null;
    Partition remPartition = null;
    ArrayList<Partition> compacted = new ArrayList<>();
    // Compacting and calculating total size of partitions
    for (Partition partition : partitions) {
      if (partition.getID() != null && firstPartition == null) {
        firstPartition = partition;
        firstPartition.setStartingMemoryAddress(0);
        compacted.add(firstPartition);
      } else if (partition.getID() == null && remPartition == null) {
        remPartition = partition;
      } else if (partition.getID() == null && remPartition != null) {
        continue;
      } else {
        partition.setStartingMemoryAddress(firstPartition.getStartingMemoryAddress() + firstPartition.getSize());
        firstPartition = partition;
        compacted.add(firstPartition);
      }

    }
    if (remPartition != null) {
      remPartition.setStartingMemoryAddress(firstPartition.getStartingMemoryAddress() + firstPartition.getSize());
      remPartition.setSize(initialMemorySize - remPartition.getStartingMemoryAddress());
      compacted.add(remPartition);
    } else {
      remPartition = new Partition(firstPartition.getStartingMemoryAddress() + firstPartition.getSize(),
          (initialMemorySize - firstPartition.getSize() + firstPartition.getStartingMemoryAddress()));
      compacted.add(remPartition);
    }
    partitions = compacted;

  }

}
