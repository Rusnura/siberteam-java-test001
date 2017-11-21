package workers;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;

public class Producer implements Runnable {
    private final BlockingQueue<String> queueOfSymbols;
    private final String filePath;
    private BufferedReader bufferedReader;
    private FileReader fileReader;

    public Producer(BlockingQueue<String> q, String filePath) {
        this.queueOfSymbols = q;
        this.filePath = filePath;
    }

    @Override
    public void run() {
        try {
            fileReader = new FileReader(filePath);
            bufferedReader = new BufferedReader(fileReader);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                queueOfSymbols.put(line);
            }
        } catch (IOException e) {
            System.err.println("IOException: " + e);
        } catch (InterruptedException e) {
            System.err.println("InterruptedException: " + e);
        } finally {
            try {
                // Add a POISON_PILL
                for (int i = 0; i < ExecutorWorker.availableProcessors; i++) {
                    queueOfSymbols.put(ExecutorWorker.POISON_PILL);
                }
                bufferedReader.close();
                fileReader.close();
            } catch (Exception e) {
                e.fillInStackTrace();
            }
        }
    }
}