package workers;

import org.apache.log4j.Logger;
import services.IDone;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class Producer implements Runnable, IDone {
    private static final Logger log = Logger.getLogger(Producer.class);
    private final BlockingQueue<String> queueOfSymbols;
    private final String filePath;
    private BufferedReader bufferedReader;
    private FileReader fileReader;

    public Producer(BlockingQueue<String> q, String filePath) {
        this.queueOfSymbols = q;
        this.filePath = filePath;
    }

    @Override
    public AtomicBoolean getIsDone() {
        return isDone;
    }

    @Override
    public void run() {
        try {
            fileReader = new FileReader(filePath);
            bufferedReader = new BufferedReader(fileReader);
            String line;
            while ((line = bufferedReader.readLine()) != null ) {
                queueOfSymbols.put(line);
            }
        } catch (Exception e) {
            log.error(e);
        } finally {
            try {
                bufferedReader.close();
                fileReader.close();
            } catch (Exception e) {
                log.error(e);
            }
            isDone.set(true);
        }
    }
}