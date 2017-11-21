package workers;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;

public class Producer implements Runnable {
    private final BlockingQueue<String> queueOfSymbols;
    private FileReader fileReader;
    private BufferedReader bufferedReader;
    private String filePath;

    public Producer(BlockingQueue<String> q, String filePath) {
        this.queueOfSymbols = q;
        this.filePath = filePath;
    }

    public void run() {
        try {
            bufferedReader = new BufferedReader(fileReader = new FileReader(filePath));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                queueOfSymbols.put(line);
            }

            // Add a POISON_PILL
            for (int i = 0; i < ExecutorWorker.availableProcessors; i++) {
                queueOfSymbols.put(ExecutorWorker.POISON_PILL);
            }
        } catch (IOException e) {
            System.err.println("IOException: " + e);
        } catch (InterruptedException e) {
            System.err.println("InterruptedException: " + e);
        } finally {
            try {
                bufferedReader.close();
                fileReader.close();
            } catch (Exception e) {
                e.fillInStackTrace();
            }
        }
    }
}
