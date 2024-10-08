package SimpleLock;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

//5线程按顺序读5对点
//两句话总结【定义-已找到T和未找到V-T最短距离（从start到j的）的两个点集。找到start到V-T最小距离，将最小距离点j加入V,同时以j为中转点，松弛start到V-T的距离】


import java.util.Arrays;

public class Dijkstra {
    // 用来找到最小距离节点
    private static int minDistance(int[] dist, boolean[] sptSet, int V) {
        int min = Integer.MAX_VALUE, min_index = -1;
        for (int v = 0; v < V; v++) {
            if (!sptSet[v] && dist[v] <= min) {
                min = dist[v];
                min_index = v;
            }
        }
        return min_index;
    }

    // 打印最短路径
    private static void printPath(int[] parent, int j) {
        if (parent[j] == -1)
            return;
        printPath(parent, parent[j]);
        System.out.print(j + " ");
    }
 
    // 打印从起点到给定顶点的结果
    private static void printSolution(int[] dist, int[] parent, int startVertex, int endNode) {
        System.out.println("Vertex\t Distance\tPath");
        System.out.print(startVertex + " -> ");
        System.out.print(endNode + " \t\t ");
        System.out.print(dist[endNode] + "\t\t");
        System.out.print(startVertex + " ");
        printPath(parent, endNode);
        System.out.println();
    }


     // 打印最短路径
    private static void writePath(int[] parent, int j, PrintWriter writer) {
        if (parent[j] == -1)
            return;
            writePath(parent, parent[j], writer);
        writer.print(j + " ");
    }
     // 写入从起点到给定顶点的结果到文件
     private static void writeSolution(int[] dist, int[] parent, int startVertex, int endNode, PrintWriter writer) {
        writer.print("Node: " + startVertex + " " + endNode);
        writer.print(" Path:" + startVertex + " ");
        writePath(parent, endNode, writer);
        writer.println(" MinDistance: " + dist[endNode]);
    }

    // 实现Dijkstra算法
    public static void dijkstra(int[][] graph, int startVertex, int endNode,String filePath) throws IOException {
        int V = graph.length;
        int[] dist = new int[V]; // 存储最短距离
        boolean[] sptSet = new boolean[V]; // 确定节点最短路径是否已经被确定

        // 存储最短路径树
        int[] parent = new int[V];
        parent[startVertex] = -1;

        Arrays.fill(dist, Integer.MAX_VALUE);
        dist[startVertex] = 0;

        // 找到所有顶点的最短路径
        for (int count = 0; count < V - 1; count++) {
            int u = minDistance(dist, sptSet, V);
            sptSet[u] = true;

            for (int v = 0; v < V; v++) {
                if (!sptSet[v] && graph[u][v] != 0 &&
                    dist[u] != Integer.MAX_VALUE &&
                    dist[u] + graph[u][v] < dist[v]) {
                    parent[v] = u;
                    dist[v] = dist[u] + graph[u][v];
                }
            }
        }

        // // 打印从起始点到结束点的结果
        // printSolution(dist, parent, startVertex, endNode);
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath, true))) {
            // 写入从起始点到结束点的结果
            writeSolution(dist, parent, startVertex, endNode, writer);
        } catch (FileNotFoundException e) {
            System.out.println("Error: Unable to open file path '" + filePath + "'.");
            e.printStackTrace();
        }
    }

}

    // public void dijkstra(int[][] graph,int startVertex,int endNode){
        
    //     //初始化
    //     int[] v;//前面存路径，最后一个存最短距离
    //     int length = graph.length;
    //     int[] result = new int[length];//存最短距离数组
    //     int[] notFound = new int[length];//存未被松弛的点
    //     v=new int[length+1];
       
    //     for (int i = 0; i < length; i++) {
    //         result[i] = -1;//初始化最短距离为无限大【注意，所有最短距离指的都是从startpoint到当前point的最短距离】
    //         v[i]=-1;//初始化路径
    //     }
    //     result[startVertex] = 0 ;
    //     v[0]=startVertex;//路径第一个是起点
    //     v[length]=-1;//还未找到可达最小距离
        
    //     notFound[startVertex] = -1;
    //     for (int i = 0; i < length; i++) {
    //         notFound[i] = graph[startVertex][i];
    //     }
        
    //     // 开始
    //     for (int i = 1; i < length; i++) {
    //         //1. 从「未求出最短路径的点」notFound 中取出 最短路径的点
    //         //1.1 从notFound[j]不等-1的点中 找到最短距离的点
    //         int min = Integer.MAX_VALUE;//初始化最短距离（用于存结果）
    //         int minIndex = 0;//最短距离的点下标（用于一会松弛）
    //         for (int j = 0; j < length; j++) {
    //             if (notFound[j] > 0 && notFound[j] < min){
    //                 min = notFound[j];
    //                 minIndex = j;
    //             }
    //         }
    //         //1.2 将最短距离的点 取出 放入结果中
    //         result[minIndex] = min;
    //         notFound[minIndex] = -1;
    //         v[i]=minIndex;

    //         if(minIndex==endNode) break;
    //         //2. 刷新 「未求出最短距离的点」 notFound[] 中的距离（松弛）
    //         //2.1 遍历刚刚找到最短距离的点 (B) 的出度 (BA、BB、BC、BD)
    //         for (int j = 0; j < length; j++) {
    //             // 出度可通行(例如 BD:graph[1][3]  > 0)
    //             // 出度点不能已经在结果集 result中(例如 D: result[3] == -1)
    //             if (graph[minIndex][j] > 0
    //             && result[j] == -1){
    //                 int newDistance = result[minIndex] + graph[minIndex][j];
    //                 //通过 B 为桥梁，刷新距离
    //                 //（比如`AD = 6 < AB + BD = 4` 就刷新距离）（ -1 代表无限大）
    //                 if (newDistance < notFound[j] || notFound[j]==-1){
    //                     notFound[j] = newDistance;
    //                 }
    //             }
    //         }

    //     }
    //     v[length]=result[endNode];
    //     for (int i : v) {
    //         System.out.print(i+" ");
    //     }

    //     return ;
    // }

