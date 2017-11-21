package workers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ExecutorWorker {
    public static int availableProcessors = Runtime.getRuntime().availableProcessors();
    public static final String POISON_PILL = new String(); // Add it to queue end
    private BlockingQueue<String> queueOfSymbols = new ArrayBlockingQueue<String>(10);
    private ConcurrentHashMap<Character, AtomicInteger> countOfCharsMap = new ConcurrentHashMap<Character, AtomicInteger>(); // ConcurrentHashMap: <Symbol, CountOfSymbols>
    private AtomicInteger symbolsCount = new AtomicInteger(0);
    private String filePath;
    private ExecutorService readerService = Executors.newSingleThreadExecutor();
    private ExecutorService symbolsAnalyzer = Executors.newFixedThreadPool(ExecutorWorker.availableProcessors);

    public ExecutorWorker(String filename) {
        this.filePath = filename;
    }

    public AtomicInteger getSymbolsCount() {
        return symbolsCount;
    }

    public ConcurrentHashMap<Character, AtomicInteger> getCountOfCharsMap() {
        return countOfCharsMap;
    }

    public void start() throws IOException {
        File file = new File(filePath);
        if (!file.canRead()) {
            throw new IOException("Can't open a file for reading!");
        }

        Producer producer = new Producer(this.queueOfSymbols, this.filePath);
        Consumer consumer = new Consumer(this.queueOfSymbols, this.countOfCharsMap);
        List<Future<AtomicInteger>> workerList = new ArrayList<Future<AtomicInteger>>();

        readerService.submit(producer);
        for (int i = 0; i < ExecutorWorker.availableProcessors; i++) {
            Future<AtomicInteger> worker = symbolsAnalyzer.submit(consumer);
            workerList.add(worker);
        }

        for (Future<AtomicInteger> future: workerList) {
            try {
                 symbolsCount = future.get(1, TimeUnit.MINUTES);
            } catch (Exception e) {
                System.err.println(e.getMessage());
            } finally {
                readerService.shutdown();
                symbolsAnalyzer.shutdown();
            }
        }
    }
}