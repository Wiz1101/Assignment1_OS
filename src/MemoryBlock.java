public class MemoryBlock {
  private Integer id;
  private int start;
  private int size;

  public MemoryBlock(Integer id, int start, int size) {
    this.size = size;
    this.id = id;
    this.start = start;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public void setStart(int start) {
    this.start = start;
  }

  public void setSize(int size) {
    this.size = size;
  }

  public Integer getId() {
    return id;
  }

  public int getStart() {
    return start;
  }

  public int getEnd() {
    return start + size - 1;
  }

  public int getSize() {
    return size;
  }
}