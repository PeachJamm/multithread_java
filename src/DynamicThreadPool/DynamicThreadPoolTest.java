package DynamicThreadPool;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class DynamicThreadPoolTest {
    private static final int QUEUE_CAPACITY = 10;
    private static final int NUMBER_OF_THREADS = 7;
    private static int size = 2000; // 矩阵大小
    private static int[][] graph = new int[size][size]; 
    private static int NodeCount = 20; // 最大生产消费数
 
    public static void main(String[] args) {

        BlockingQueue<DataPair> queue = new LinkedBlockingQueue<>(QUEUE_CAPACITY);
        ExecutorService executor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
        loadMatrixFromFile();
        for (int i = 0; i < NUMBER_OF_THREADS; i++) {
            executor.submit(new GeneralWorker_atom(graph,queue,NodeCount));//改这里sync或atom模式
        }
        
        // 添加关闭钩子来适当地关闭线程池
        executor.shutdown();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            
            try {
                if (!executor.awaitTermination(800, TimeUnit.MILLISECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
            }
        }));
    }


    public static void loadMatrixFromFile(){
        String filePath = "./src/map.txt"; // 文件路径
            
               try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
                   String line;
                   int row = 0; // 当前行号
       
                   while ((line = reader.readLine()) != null && row < size) {
                       String[] parts = line.trim().split("\\s+"); // 分割每行的数据
       
                       for (int col = 0; col < size && col < parts.length; col++) {
                           graph[row][col] = Integer.parseInt(parts[col]); // 将字符串转换为整数并存储到数组
                       }
       
                       row++; // 移动到下一行
                   }
       
                   System.out.println("Matrix loaded successfully.");
               } catch (IOException e) {
                   e.printStackTrace();
                   System.out.println("Error reading from file.");
               }
                
                
    }

}
