import javax.naming.OperationNotSupportedException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
public class BTree<E extends Comparable<E>> { //Extend comparable to we can create a btree of any "comparable" type
    private Node root; // root of the Btree
    private final int MIN; //Minimum Degree of the Btree

    //This means each node needs a minimum of Min-1 values and max of 2Min-1 values
    public BTree(int minimumDegree) { //Constructor of the Btree
        if (minimumDegree <= 1) { //Minimum Degree canot be less than or equal to 1
            throw new IllegalArgumentException("Minimum Degree must be greater than 1");
        }
        this.MIN = minimumDegree;
        this.root = new Node(true);
    }

    private class Node {
        private int num;    //number of values currently stored in the Node
        private boolean leaf;   //indicated whether this node is a leaf or not
        private List<E> values = new ArrayList<>();     // create a list which has the values of the node stored
        private List<Node> children = new ArrayList<>();    // create a list which has the children of the node stored in list

        private Node(boolean isLeaf) {  //constructor of the Node class
            this.leaf = isLeaf;
        }
        public Node() { //another constructor of the Node class
            leaf = true;
            num = 0;
            //So we have a degree value. This means that every non-leaf node needs to have at least MIN-1 nodes and at most 2MIN-1. These non-leaf nodes can also have at  most 2MIN children
            //root must have at least one value when the tree is not empty.
            values = new ArrayList<>();
            children = new ArrayList<>();
        }
        /**
         * Returns a string representation of the values in the Node class
         * @return String
         */
        public String toString() {
            return values.toString();
        }
        /**
         * Checks whether the Node is full
         * @return boolean
         */
        private boolean isFull() {
            return values.size() == 2 * MIN - 1;
        }
    }
    /**
     * Adds the specified element to this B-tree. If the B-tree already contains this
     * element, this operation has no effect on the tree (i.e., it silently ignores
     * attempts to add duplicates).
     *
     * @param element The element to be added to this B-tree.
     * @throws OperationNotSupportedException If the add operation results in a call to add
     * the element to a non-full node while the node
     * is actually full.
     */
    public void add(E element) throws OperationNotSupportedException {
        // Check if the element already exists in the tree
        if (this.find(element) != null) {
            // Element already exists, so silently handle it
            return;
        }
        //If the root is full, we have to split it and assign false to the leaf value
        if (root.isFull()) {
            //create a new node for the new root
            Node newRoot = new Node();
            newRoot.leaf = false;
            //now we make sure that the original root node is assigned as a child to the new root node
            newRoot.children.add(root);
            //Now we split the node appropriately
            splitChild(newRoot, 0);
            //lastly we update the root node of the Btree
            root = newRoot;
        }
        //Otherwise, we just insert the value to the corresponding root node
        insertNonFull(root, element);
    }
    /**
     * Inserts an element into a Node that is not full
     * @throws OperationNotSupportedException
     *
     */
    private void insertNonFull(Node node, E element) throws OperationNotSupportedException {
        // we take the size of the node and then subtract it by 1 because indexes go from 0 to i
        int index = node.values.size() - 1;
        //if the node is a leaf, insert the value in the right place
        if (node.leaf) {
            node.values.add(null);
            while (index >= 0 && element.compareTo(node.values.get(index)) < 0) {
                node.values.set(index + 1, node.values.get(index));
                index--;
            }
            node.values.set(index + 1, element);
        }
        else {
            //if this is not a leaf node, things get complicated!
            //figure out the child subtree into which the insertion will descend
            while (index >= 0 && element.compareTo(node.values.get(index)) < 0) {
                index--;
            }
            index++;
            //are we descending into a tree with a full node?
            // if so, we split the node into two
            if (node.children.get(index).isFull()) {
                splitChild(node, index);
                //figure out the child subtree into which the insertion will descend
                if (element.compareTo(node.values.get(index)) > 0) {
                    index++;
                }
            }
            //now we have found the correct subtree, so recursive call
            insertNonFull(node.children.get(index), element);
        }
    }
    /**
     * Splits a full Node and increases the tree height by one.
     * @throws OperationNotSupportedException
     */
    private void splitChild(Node parentNode, int childIndex) throws OperationNotSupportedException {
        //Obtain the specific child at the given index
        Node childNode = parentNode.children.get(childIndex);
        //Create a new node with the leaf status of the child node
        Node newNode = new Node(childNode.leaf);
        //Loop to move the upper half of the values from childNode to newNode
        for (int j = 0; j < MIN - 1; j++) {
            //Remove the element at position MIN from childNode and add it to newNode
            //Move the elements from the second half of childNode to newNode
            newNode.values.add(childNode.values.remove(MIN));
        }
        //Check if the childNode is not a leaf
        if (!childNode.leaf) {
            //If childNode has children, move the corresponding children to newNode
            for (int j = 0; j < MIN; j++) {
                //Remove the child at position MIN from childNode and add it to newNode
                newNode.children.add(childNode.children.remove(MIN));
            }
        }
        //Add the new node to the children of parentNode
        //This is placed immediately after the original childNode that was split
        parentNode.children.add(childIndex + 1, newNode);

        //Move the median key of childNode to parentNode
        //This is done by removing the median key from childNode and adding it at the correct position in parentNode
        //This median key will act as the new "pivot" element for the two split nodes in the tree structure
        parentNode.values.add(childIndex, childNode.values.remove(MIN - 1));
    }
    /**
     * Adds all the elements from a collection to the Btree
     * @throws OperationNotSupportedException
     */
    public void addAll(Collection<E> elements) throws OperationNotSupportedException {
        for (E element : elements) {
            add(element);
        }
    }
    /**
     * Gives a specific format that displays the tree on the console
     */
    public void show() {
        String nodesep = " ";
        Queue<Node> queue1 = new LinkedList<>();
        Queue<Node> queue2 = new LinkedList<>();
        queue1.add(root); /* root of the tree being added */
        while (true) {
            while (!queue1.isEmpty()) {
                Node node = queue1.poll();
                System.out.printf("%s%s", node.toString(), nodesep);
                if (!node.children.isEmpty())
                    queue2.addAll(node.children);
            }
            System.out.printf("%n");
            if (queue2.isEmpty())
                break;
            else {
                queue1 = queue2;
                queue2 = new LinkedList<>();
            }
        }
    }
    public class NodeAndIndex {
        private final Node btNode;
        private final int location;

        public NodeAndIndex(Node node, int index) { //Constructor of the Node and Index class
            this.btNode = node;
            this.location = index;
        }
        /**
         * Returns a string representation of the Node and Index class
         * @return String
         */
        public String toString() {
            return "<[" + btNode.values + "], " + location + ">";
        }
    }
    /**
     * Search for the specified element in this B-tree. If the element is found, return
     * the node where it is located along with the index of the element in the contents
     * of that node.
     *
     * @param element The specified element being searched in this B-tree.
     * @return A {@link NodeAndIndex} instance, containing the node where the element was
     * found and its index in the node’s contents. If the element was not found in this
     * tree, <code>null</code> is returned.
     */
    public NodeAndIndex find(E element) {
        return find(element, root);
    }
    /**
     * Search for the specified element in this B-tree. If the element is found, return
     * the node where it is located along with the index of the element in the contents
     * of that node.
     * @param element
     * @param node
     * @return A {@link NodeAndIndex} instance, containing the node where the element was
     *       found and its index in the node’s contents. If the element was not found in this
     *       tree, <code>null</code> is returned.
     */
    private NodeAndIndex find(E element, Node node) {
        int i = 0;
        while (i < node.values.size() && element.compareTo(node.values.get(i)) > 0) {
            i++;
        }
        if (i < node.values.size() && element.equals(node.values.get(i))) {
            return new NodeAndIndex(node, i);
        }
        if (node.leaf) {
            return null;
        }
        return find(element, node.children.get(i));
    }
}
