package DynamicThreadPool;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class GeneralWorker_atom implements Runnable{
    private static final int containSize = 10;//和主函数的QUEUE_CAPACITY要一样
    private final BlockingQueue<DataPair> queue;

    private static AtomicInteger totalProduce = new AtomicInteger(0);
    private static AtomicInteger totalConsume = new AtomicInteger(0);
    
    Dijkstra dijkstra;
    private static int size = 2000; // 矩阵大小
    private static int[][] graph = new int[size][size]; 
    private final int maxProduction ; // 最大生产数
    private final int maxConsumption ; // 最大消费数

    public GeneralWorker_atom(int[][] graph,BlockingQueue<DataPair> queue2, int NodeCount) {
        GeneralWorker_atom.graph=graph;
        this.queue = queue2;
        maxProduction=NodeCount;
        maxConsumption=NodeCount;
    }

    @Override
    public void run() {
        long startTime = System.currentTimeMillis();
        while (totalProduce.get() < maxProduction || totalConsume.get() < maxConsumption) {
            DataPair product = (DataPair) produce();
            System.out.println(product.getFirst());
            if (product != null && !queue.offer(product)) {
                System.out.print(" produce"+product.getFirst()+product.getSecond());
                System.out.println(Thread.currentThread().getName() + "生产者生产");
                System.out.println("totalProduce:"+totalProduce.get());
            } else if ((product = queue.poll())!=null) {
                try {
                    consume(product);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                System.out.println(Thread.currentThread().getName()+"消费者消费");
                System.out.println("totalConsume:"+totalConsume.incrementAndGet());
            }
        }
        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;
    
        System.out.println("ExecutionTime" + executionTime + " milliseconds");
    }

    // private boolean shouldProduce(DataPair product) {
    //     // 根据一些逻辑决定是否生产
    //     // return queue.size() < containSize && totalProduce.get() < maxProduction;
    //     return  queue.offer(product);
    // }

    // private boolean shouldConsume() {
    //     // 根据一些逻辑决定是否消费
    //     //return queue.size() > 0 && totalConsume.get() < maxConsumption;
    //     DataPair product = queue.poll();
    //     queue.poll();
    //     return  queue.offer(new DataPair());
    // }

    private DataPair produce() {
        // 生产逻辑
                     String filePath = "./src/query.txt"; // 文件的路径
                        int targetLine = totalProduce.getAndIncrement(); // 我们想要读取的行号
                        System.out.println("TL"+totalProduce.get());
                        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {

                            String line = null;
                            int currentLine = 0;
                            
                            while ((line = reader.readLine()) != null) {
                            
                                if (currentLine == targetLine) { // 当到达目标行
                                    String[] parts = line.trim().split("\\s+"); // 分割行内容
                                    return new DataPair(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
                                  
                                }
                                currentLine++; // 更新当前行号
                            }
                        } catch (IOException e) {
                                e.printStackTrace();
                        }

        return null;
    }

    private void consume(DataPair product) throws IOException {
        // 消费逻辑
        int i=product.getFirst();
        int j=product.getSecond();
        Dijkstra.dijkstra(graph,i,j,"DTresult.txt");
    }
}
