import javax.naming.OperationNotSupportedException;
import java.util.Arrays;
public class BTreeRunner {
    public static void main(String[] args) throws OperationNotSupportedException {
        BTree<Integer> tree = new BTree<>(3); /* minimum degree is 3 */
        tree.addAll(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 11,
                12, 22, 19, 25, 100, 88, 64, 65, 16));
        tree.show();
//Prints: PASS!
//        [9]
//[3, 6] [19, 64]
//[1, 2] [4, 5] [7, 8] [11, 12, 16] [22, 25] [65, 88, 100]
        BTree<Integer> tree1 = new BTree<>(3);
        tree1.addAll(Arrays.asList(1, 2, 2, 3,25, 100, 88, 64, 65, 16, 4, 5, 6, 7, 8, 9, 11,
                12, 22, 19));

        System.out.println(tree.find(65));
    }
//Prints: PASS!
    // <[65, 88, 100], 0>
}