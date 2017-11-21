package workers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ExecutorWorker {
    public static int availableProcessors = Runtime.getRuntime().availableProcessors();
    public static final String POISON_PILL = new String(); // Add it to queue end
    private final BlockingQueue<String> queueOfSymbols = new ArrayBlockingQueue<String>(10);
    private final ConcurrentHashMap<Character, AtomicInteger> countOfCharsMap = new ConcurrentHashMap<Character, AtomicInteger>(); // ConcurrentHashMap: <Symbol, CountOfSymbols>
    private final String filePath;
    private final ExecutorService readerService = Executors.newSingleThreadExecutor();
    private final ExecutorService symbolsAnalyzer = Executors.newFixedThreadPool(ExecutorWorker.availableProcessors);
    private AtomicInteger symbolsCount = new AtomicInteger(0);

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
        final Producer producer = new Producer(this.queueOfSymbols, this.filePath);
        final Consumer consumer = new Consumer(this.queueOfSymbols, this.countOfCharsMap);
        final List<Future<AtomicInteger>> workerList = new ArrayList<Future<AtomicInteger>>();

        readerService.submit(producer);
        for (int i = 0; i < ExecutorWorker.availableProcessors; i++) {
            Future<AtomicInteger> worker = symbolsAnalyzer.submit(consumer);
            workerList.add(worker);
        }

        for (Future<AtomicInteger> future: workerList) {
            try {
                 symbolsCount = future.get();
            } catch (Exception e) {
                System.err.println(e.getMessage());
            } finally {
                readerService.shutdown();
                symbolsAnalyzer.shutdown();
            }
        }
    }
}