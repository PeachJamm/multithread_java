package DynamicThreadPool;
public class DataPair {
    private final int first;
    private final int second;

    public DataPair(int first, int second) {
        this.first = first;
        this.second = second;
    }

    public DataPair() {
        this.first = -1;
        this.second = -1;
    }

    public int getFirst() {
        return first;
    }

    public int getSecond() {
        return second;
    }
}
