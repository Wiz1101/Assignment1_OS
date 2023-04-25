import java.io.File;

public class Main {
  public static void main(String[] args) {
    // Delete outputs
    File folder = new File("src/main/resources/outputs");
    for (File f : folder.listFiles()) {
      f.delete();
    }
    /*
     *  ~~~~~ FILES ~~~~~~
     * 1-warmup.in
     * 2-firsterrors.in
     * 3-madoutputter.in
     */
    String file = "1-warmup.in"; // FIXME: Change this!
    App app = new App();
    app.loadFile(file);

  }
}
