package PrepareData;

public class Main {
  
        static final int[][] graph = new int[][]{
            {0, 1, 2, 3, 4, 5, 6, 7, 8, 9},
            {1, 0, 1, 1, 1, 1, 1, 1, 1, 1},
            {2, 1, 0, 1, 1, 1, 1, 1, 1, 1},
            {3, 1, 1, 0, 1, 1, 1, 1, 1, 1},
            {4, 1, 1, 1, 0, 1, 1, 1, 1, 1},
            {5, 1, 1, 1, 1, 0, 1, 1, 1, 1},
            {6, 1, 1, 1, 1, 1, 0, 1, 1, 1},
            {7, 1, 1, 1, 1, 1, 1, 0, 1, 1},
            {8, 1, 1, 1, 1, 1, 1, 1, 0, 1},
            {9, 1, 1, 1, 1, 1, 1, 1, 1, 0}
        };
        
        
    // public static void main(String[] args) {   
          
    

    //     // 创建并启动第一个线程，并传递参数
    //     // Thread thread1 = new Thread(new Dijkstra(graph, 0,6));
    //     // 创建并启动第二个线程，并传递参数
    //     Thread thread2 = new Thread(new Dijkstra(graph, 2,0));
        
    //     // 启动两个线程
    //     // thread1.start();
    //     thread2.start();
    // }
}
//1.写dijstra. ok
//3.读入数对。输出最短路径和最短距离.ok
//2.同时启动起点不同的两个线程。ok


//4.实现重复利用子线程。主线程生产，子线程消费。【结点对的访问为临界区】

//4.准备大量数据。动态读入内存。
//6.不同线程数量，测试速度