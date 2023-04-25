
public class Partition {
  private int startingMemoryAddress;
  private int partitionSize;
  private Integer id;

  Partition(int pStartingMemoryAddress, int pPartitionSize) {
    this.startingMemoryAddress = pStartingMemoryAddress;
    this.partitionSize = pPartitionSize;
  }

  public int getSize() {
    return partitionSize;
  }

  public void setSize(int partitionSize) {
    this.partitionSize = partitionSize;
  }

  public Integer getID() {
    return id;
  }

  public void setID(Integer id) {
    this.id = id;
  }

  public int getStartingMemoryAddress() {
    return startingMemoryAddress;
  }

  public void setStartingMemoryAddress(int startingMemoryAddress) {
    this.startingMemoryAddress = startingMemoryAddress;
  }

  public Partition allocate(int length) {
    if (partitionSize - length >= 0) {
      int remainingMemory = this.partitionSize - length;
      this.partitionSize = length;
      return new Partition(this.getStartingMemoryAddress() + length, remainingMemory);
    }
    return null;
  }
}
