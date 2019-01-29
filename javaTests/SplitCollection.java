import java.util.List;
import java.util.ArrayList;
import java.util.Collection;

public class SplitCollection {
    public static void main(String... args) {
        List<Integer> list = new ArrayList<Integer>();
        list.add(0);
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);
        list.add(5);
        list.add(6);

        int batchSize = 3;
        Collection<List<Integer>> lists = split(list, batchSize);
        System.out.println(lists); // [[0, 1, 2], [3, 4, 5], [6]]
    }

    public static <T extends Object> Collection<List<T>> split(List<T> list, int batchSize) {
        Collection<List<T>> lists = new ArrayList<List<T>>((list.size() / batchSize) + 1);
        for (int i = 0; i < list.size(); i += batchSize) {
            lists.add(list.subList(i, Math.min(i + batchSize, list.size())));
        }
        return lists;
    }
}
