package snake.mvc.menu.model;

public class Node {
	public int x, y;
	public Node parent;

	public static Node root (int x, int y) {
		Node node = new Node();
		node.x = x;
		node.y = y;
		node.parent = null;
		return node;
	}

	public static Node child (int x, int y, Node parent) {
		Node node = new Node();
		node.x = x;
		node.y = y;
		node.parent = parent;
		return node;
	}
}
