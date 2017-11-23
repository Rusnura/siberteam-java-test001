import org.apache.log4j.Logger;
import workers.DrawerWorker;
import workers.ExecutorWorker;

import java.io.IOException;
import java.util.Scanner;

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
            DrawerWorker.draw(executorWorker.getCountOfCharsMap());
        } catch (Exception e) {
            log.error(e);
            e.printStackTrace();
        }

//        final ExecutorWorker executorWorker = new ExecutorWorker(args[0]);
//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                Scanner scanner = new Scanner(System.in);
//                boolean shutdown = false;
//                do {
//                    System.out.println("===\nВыберите одно из действий\n===");
//                    System.out.println("1) Вывести датаграмму");
//                    System.out.println("2) Выйти из приложения");
//                    System.out.print("[1-2]: ");
//                    String item = scanner.nextLine();
//                    if (item.equals("1")) {
//                        DrawerWorker.draw(executorWorker.getCountOfCharsMap());
//                    } else if (item.equals("2")) {
//                        //shutdown = true;
//                    } else {
//                        System.out.println("Неизвестная команда! Повторите снова.");
//                    }
//                } while (!shutdown);
//            }
//        }).start();
//
//        try {
//            System.out.println("Анализ в процессе...");
//            executorWorker.start();
//        } catch (Exception e) {
//            log.error(e);
//            e.printStackTrace();
//        }
    }
}