package workers;

import org.apache.log4j.Logger;
import services.Indicator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Random;
import java.util.concurrent.BlockingQueue;

public class Producer implements Runnable {
    private static final Logger log = Logger.getLogger(Producer.class);
    private final BlockingQueue<String> queueOfSymbols;
    private final String filePath;
    private BufferedReader bufferedReader;
    private FileReader fileReader;
    private Indicator indicator;

    public Producer(BlockingQueue<String> q, String filePath, Indicator indicator) {
        this.queueOfSymbols = q;
        this.filePath = filePath;
        this.indicator = indicator;
    }

    @Override
    public void run() {
        try {
            fileReader = new FileReader(filePath);
            bufferedReader = new BufferedReader(fileReader);
            String line;
            while ((line = bufferedReader.readLine()) != null ) {
                queueOfSymbols.put(line);
                System.out.println("Ok");
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
            this.indicator.setIsDone();
        }
    }
}