import org.apache.log4j.Logger;
import workers.DrawerWorker;
import workers.ExecutorWorker;

public class Main {
    private static final Logger log = Logger.getLogger(Main.class);
    public static void main(String[] args) {
        // Check command line parameters
        if (args.length == 0) {
            System.err.println("Path to txt file isn't assigned. Use the first command line parameter.");
            System.err.println("Usage: test.jar PathToTXTFile");
            return;
        }

        final ExecutorWorker executorWorker = new ExecutorWorker(args[0]);
        try {
            executorWorker.start();
            DrawerWorker.draw(executorWorker.getCountOfCharsMap(), executorWorker.getSymbolsCount().longValue());
        } catch (Exception e) {
            log.error(e);
        }
    }
}
