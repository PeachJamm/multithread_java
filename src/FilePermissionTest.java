import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class FilePermissionTest {

    // 测试目录和多个文件的创建及写入权限
    public static void testMultipleFileCreation(String directoryPath, int numProducers, int numConsumers) {
        File directory = new File(directoryPath);

        // 确保目录存在
        if (!directory.exists() && !directory.mkdirs()) {
            System.out.println("Failed to create directory: " + directoryPath);
            return;
        }

        System.out.println("Directory ready for writing: " + directoryPath);

        // 为不同的生产者和消费者组合创建和测试文件
        for (int p = 3; p <= numProducers; p++) {
            for (int c = 3; c <= numConsumers; c++) {
                String fileName = "PCresult_" + p + "_" + c + ".txt";
                File testFile = new File(directory, fileName);
                try {
                    if (testFile.createNewFile() || testFile.exists()) {
                        System.out.println("File created or already exists: " + testFile.getPath());
                        try (PrintWriter writer = new PrintWriter(new FileWriter(testFile, true))) {
                            writer.println("Test writing for Producers: " + p + ", Consumers: " + c);
                            System.out.println("Successfully wrote to file: " + fileName);
                        }
                    }
                } catch (IOException e) {
                    System.out.println("Failed to create or write to file: " + fileName);
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        String directoryPath = "PCresult_20";
        testMultipleFileCreation(directoryPath, 5, 5);  // 测试从3到5的生产者和消费者
    }
}
