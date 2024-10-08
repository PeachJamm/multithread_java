package PrepareData;
//生产矩阵
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class generate_map {
    public static void main(String[] args) {
        int size = 2000; // 图的大小
        int[][] graph = new int[size][size]; // 创建矩阵
        Random random = new Random(); // 随机数生成器

        // 生成随机有向图，边的权重在1到9之间
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (i != j) { // 确保没有自环
                    graph[i][j] = random.nextInt(99) + 10; // 生成1到9之间的随机权重
                } else {
                    graph[i][j] = 0; // 自环位置设置为0
                }
            }
        }

        // 保存矩阵到文件
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("map.txt"))) {
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    bw.write(graph[i][j] + (j == size - 1 ? "" : " ")); // 在同一行内用空格分隔数字
                }
                bw.newLine(); // 每一行后换行
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


