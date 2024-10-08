package SimpleLock;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;


public class PC {
    private static PC test=new PC();
    private int totalProduce=0;//当前已生产结点对数量
    private int totalConsume=0;//当前已消费结点对数量
    private static int count=0;//当前容器内所剩结点对数量
    private static int maxNum=20;//结点对数量
    private int containSize=10;//容器大小
    //条件锁
    ReentrantLock lock=new ReentrantLock();
    Condition producerCondition=lock.newCondition();
    Condition consumerCondition=lock.newCondition();
    
    int[][] data = {{-1,-1}, {-1,-1}, {-1,-1},{-1,-1}, {-1,-1}, {-1,-1},{-1,-1}, {-1,-1}, {-1,-1},{-1,-1}};//容器
    private static int size = 2000; // 矩阵大小
    private static int[][] graph = new int[size][size]; //矩阵

    Dijkstra dijkstra;
    static String result_FilePath;//记录完成一批结点对生产消费后的结果。所有结点对路径和最小距离
    static String stat_FilePath;//记录完成一批结点对生产消费后的统计数据。时间和阻塞次数
    int block_ProducerHit=0;//生产者阻塞次数
    int block_ConsumerHit=0;//消费者阻塞次数

    public static void main(String[] args) {
        //创建文件夹
        String directoryPath = "PCresult_"+maxNum;
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            directory.mkdir(); // 如果文件夹不存在，则创建文件夹
        }
        //创建总统计数据文件
        String time_FileName = "PCresult_stat.txt";
        File timeFile = new File(directory, time_FileName);
        stat_FilePath=timeFile.getPath();

        //创建每轮结果文件和运行一批结果
        for(int numProducers = 4; numProducers<=13; numProducers++ ){// 生产者的数量
            for(int numConsumers = 4; numConsumers<=12; numConsumers++ ){// 消费者的数量

                String result_FileName = "PCresult_" + numProducers + "_" + numConsumers + ".txt";
                File resultFile = new File(directory, result_FileName);
                result_FilePath=resultFile.getPath();

                try {
                    if (resultFile.createNewFile() || resultFile.exists()) {
                        test.startThreads(numProducers, numConsumers);//启动线程们
                    }
                } catch (IOException e) {
                    System.out.println("Failed to create or write to file: " + result_FilePath);
                    e.printStackTrace();
                }
            }
        }
      
    }


    
    public void startThreads(int numProducers, int numConsumers) throws IOException {
        long startTime = System.currentTimeMillis();
        // 加载资源，初始化操作
        loadMatrixFromFile();

        // 创建并启动生产者线程
        Thread[] producers = new Thread[numProducers];
        for (int i = 0; i < numProducers; i++) {
            producers[i] = new Thread(test.new Producer());
            producers[i].start();
        }

        // 创建并启动消费者线程
        Thread[] consumers = new Thread[numConsumers];
        for (int i = 0; i < numConsumers; i++) {
            consumers[i] = new Thread(test.new Consumer());
            consumers[i].start();
        }

        // 等待所有生产者线程完成
        for (Thread producer : producers) {
            try {
                producer.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // 等待所有消费者线程完成
        for (Thread consumer : consumers) {
            try {
                consumer.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;

        System.out.println("Execution time: " + executionTime + " milliseconds");
        System.out.println("Total produced: " + test.totalProduce);
        System.out.println("Total consumed: " + test.totalConsume);
        try (PrintWriter writer = new PrintWriter(new FileWriter(stat_FilePath, true))) {
            // 写入统计结果
            writer.println("Time: " +executionTime+ " milliseconds "+"Missed ProducerHit: "+block_ProducerHit+" Missed ConsumerHit: "+block_ConsumerHit+" P C Number: " +numProducers+ " "+ numConsumers + " "  );
        } catch (FileNotFoundException e) {
            System.out.println("Error: Unable to open file path '" + stat_FilePath + "'.");
            e.printStackTrace();
        }
        test.totalProduce=0;
        test.totalConsume=0;
        test.block_ProducerHit=0;
        test.block_ConsumerHit=0;
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



    class Producer implements Runnable{

        @Override
        public void run() {
           for(int i=0;i<20;i++){
                lock.lock();
                try{
                    while(count>=containSize){//同步1：生产者在容器满时不生产，让消费者来消费
                        block_ProducerHit++;
                        System.out.println(Thread.currentThread().getName());
                        if(totalProduce>=maxNum) {
                            consumerCondition.signalAll();
                            return;
                        }
                        producerCondition.await();
                    }
                    if(totalProduce>=maxNum) {
                        consumerCondition.signalAll();
                        return;
                    }//这里一定要再判断一遍。如果生产者生产够了，那么最后一个生产的会通知其他生产者都死亡。
                    //但有的生产者刚刚可能阻塞在while循环里的，如果不判断，会继续生产一轮。
                    
                     String filePath = "./src/query.txt"; // 文件的路径
                        int targetLine = totalProduce; // 我们想要读取的行号
                        int j=0;
                        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
                            String line = null;
                            int currentLine = 0;
                            while ((line = reader.readLine()) != null) {
                                
                                if (currentLine == targetLine) { // 当到达目标行
                                    String[] parts = line.trim().split("\\s+"); // 分割行内容

                                        while(data[j][0]!=-1)j++;
                                        data[j][0]=Integer.parseInt(parts[0]);
                                        data[j][1]=Integer.parseInt(parts[1]);
                                    // data[targetLine-1][0] = Integer.parseInt(parts[0]); // 存储第一个数字
                                    // data[targetLine-1][1] = Integer.parseInt(parts[1]); // 存储第二个数字
                                    break; // 不再读取文件的其余部分
                                }
                                currentLine++; // 更新当前行号
                            }
                           
                        } catch (IOException e) {
                                e.printStackTrace();
                        }
                        count++;
                        totalProduce++;
                        System.out.println(Thread.currentThread().getName()+"生产者生产，目前共有"+count+"生产的数对为："+data[j][0]+" "+data[j][1]);
                        // System.out.println(Thread.currentThread().getName()+"生产者生产，目前共有"+count+"生产的数对为："+data[targetLine-1][0]+" "+data[targetLine-1][1]);
                        System.out.println("totalProduce:"+totalProduce);
                        // int j=0;
                    // while(data[j][0]!=-1)j++;
                    // data[j][0]=(int) (Math.random() * 10);
                    // data[j][1]=(int) (Math.random() * 10);
                    // System.out.println(Thread.currentThread().getName()+"生产者生产，目前共有"+count+"生产的数对为："+data[j][0]+" "+data[j][1]);
                    //System.out.println("totalProduce:"+totalProduce);
                    consumerCondition.signalAll();
                }catch(Exception e){
                    e.printStackTrace();
                }finally{
                    
                    if(totalProduce>=maxNum){
                        producerCondition.signalAll();
                        System.out.println(Thread.currentThread().getName()+"生产者者已死！！");
                        lock.unlock();
                        return;
                    }
                    lock.unlock();
                }
            }
        }
    }

    class Consumer implements Runnable{
        @Override
        public void run() {
            for(int i=0;i<20;i++){
            
                lock.lock();
                try{
                    while(count<=0){//同步2：消费者在池空时不能消费，让生产者来生产。
                        block_ConsumerHit++;
                        System.out.println(Thread.currentThread().getName());
                        if(totalConsume>=maxNum){
                            producerCondition.signalAll();
                            return;}
                        consumerCondition.await();
                        //lock.unlock();no！
                    }
                    if(totalConsume>=maxNum){
                        producerCondition.signalAll();
                        return;}
                    // count--;
                    // totalConsume++;
                    
                    // System.out.println(Thread.currentThread().getName()+"消费者消费，目前共有"+count+"消费的数对为："+data[totalConsume-1][0]+" "+data[totalConsume-1][1]);
                    // Dijkstra.dijkstra(graph,data[totalConsume-1][0],data[totalConsume-1][1]);
                    // System.out.println("totalconsum:"+totalConsume);
                    
                    // producerCondition.signalAll();
                    count--;
                    totalConsume++;
                    int j=0;
                    while(data[j][0]==-1)j++;
                    System.out.println(Thread.currentThread().getName()+"消费者消费，目前共有"+count+"消费的数对为："+data[j][0]+" "+data[j][1]);
                    Dijkstra.dijkstra(graph,data[j][0],data[j][1],result_FilePath);
                    System.out.println("totalconsum:"+totalConsume);
                    data[j][0]=-1;
                    data[j][1]=-1;
                    producerCondition.signalAll();
                }catch(Exception e){
                    e.printStackTrace();
                }finally{
                    if(totalConsume>=maxNum){
                        consumerCondition.signalAll();
                        System.out.println(Thread.currentThread().getName()+"消费者已死！！");
                        lock.unlock();
                        return;
                    }
                    lock.unlock();
                }

            }
        }
    }
}

