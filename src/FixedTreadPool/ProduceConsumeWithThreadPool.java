package FixedTreadPool;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

//4个线程，10个生产者任务，10个消费者任务
public class ProduceConsumeWithThreadPool {
    private static int totalProduce = 0;
    private static int totalConsume = 0;
    private static int count = 0;
    private int containSize=10;
    Dijkstra dijkstra;

    ReentrantLock lock = new ReentrantLock();
    Condition producerCondition = lock.newCondition();
    Condition consumerCondition = lock.newCondition();

    private static int maxNum = 20;
    int [][] data ={{-1,-1},{-1,-1},{-1,-1},{-1,-1},{-1,-1},{-1,-1},{-1,-1},{-1,-1},{-1,-1},{-1,-1}};//放query的容器，最大容量10

     
    private static int size = 2000; // 矩阵大小
    private static int[][] graph = new int[size][size]; 

    public static void main(String[] args) {
        ProduceConsumeWithThreadPool test = new ProduceConsumeWithThreadPool();

        long startTime = System.currentTimeMillis();
        loadMatrixFromFile();
        // 使用线程池来执行任务
        ExecutorService executorService = Executors.newFixedThreadPool(8); // 创建一个固定大小的线程池
                // 提交生产者和消费者任务
        for (int i = 0; i <maxNum ; i++) {
            executorService.submit(test.new Producer());
            executorService.submit(test.new Consumer());
        }
        // // 提交生产者和消费者任务到线程池
        // for(int i=0;i<maxNum;i++)executorService.submit(test.new Producer());//非阻塞
        // for(int i=0;i<maxNum;i++)executorService.submit(test.new Consumer());

        executorService.shutdown(); // 关闭线程池
        while (!executorService.isTerminated()) {
            // 等待所有任务完成
        }

        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;

        System.out.println("ExecutionTime" + executionTime + " milliseconds");
        System.out.println("Totalproduce" + totalProduce);
        System.out.println("Totalconsume" + totalConsume);
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
                    lock.lock();
                    try{
                        //if(totalProduce>=20) {lock.unlock();producerCondition.signalAll();return;}
                        while(count>=containSize){//同步1：生产者在容器满时不生产，让消费者来消费
                            System.out.println(Thread.currentThread().getName()+"生产者暂停");
                            producerCondition.await();
                        }
                        count++;
                        totalProduce++;
                     String filePath = "./src/query.txt"; // 文件的路径
                        int targetLine = totalProduce; // 我们想要读取的行号
                        int j=0;
                        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {

                            String line = null;
                            int currentLine = 0;
                            
                            while ((line = reader.readLine()) != null) {
                                currentLine++; // 更新当前行号
                                if (currentLine == targetLine) { // 当到达目标行
                                    String[] parts = line.trim().split("\\s+"); // 分割行内容

                                        while(data[j][0]!=-1)j++;
                                        data[j][0]=Integer.parseInt(parts[0]);
                                        data[j][1]=Integer.parseInt(parts[1]);
                                    // data[targetLine-1][0] = Integer.parseInt(parts[0]); // 存储第一个数字
                                    // data[targetLine-1][1] = Integer.parseInt(parts[1]); // 存储第二个数字
                                    break; // 不再读取文件的其余部分
                                }
                            }
                        } catch (IOException e) {
                                e.printStackTrace();
                        }
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
                        lock.unlock();
                    }
                }
            
        }

    class Consumer implements Runnable{
        @Override
        public void run() {
                lock.lock();
                try{
                    while(count<=0){//同步2：消费者在池空时不能消费，让生产者来生产。
                        System.out.println(Thread.currentThread().getName()+"消费者暂停");
                        consumerCondition.await();
                        //lock.unlock();no！
                    }
                    count--;
                    totalConsume++;
                    int j=0;
                    while(data[j][0]==-1)j++;
                    System.out.println(Thread.currentThread().getName()+"消费者消费，目前共有"+count+"消费的数对为："+data[j][0]+" "+data[j][1]);
                    Dijkstra.dijkstra(graph,data[j][0],data[j][1]);
                    System.out.println("totalconsum:"+totalConsume);
                    data[j][0]=-1;
                    data[j][1]=-1;
                    producerCondition.signalAll();
                }catch(Exception e){
                    e.printStackTrace();
                }finally{
                    //if(totalConsume>=20)
                    lock.unlock();
                }
            
        }
    }
}
