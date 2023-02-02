package snake.mvc.menu.model;

import java.util.ArrayList;
import java.util.Collections;

import snake.mvc.Model;
import snake.mvc.Profile;
import snake.mvc.shared.Util;

public class MenuModel extends Model {

	public RetroSnake snake;
	public Slot fruit;

	public int gridSize;
	public ArrayList<Slot> gridSlots;

	public Profile userProfile;

	public MenuModel (Profile settings) {
		this.userProfile = settings;
	}

	@Override public void create () {
		restart();
	}

	@Override public void enter () {
		restart();
	}

	private void restart () {
		snake = new RetroSnake();
		snake.body.add(new Slot(gridSize / 2, gridSize / 2)); // head
		snake.body.add(new Slot(gridSize / 2 + 1, gridSize / 2)); // tail

		gridSlots = new ArrayList<Slot>();

		path = new ArrayList<Slot>();

		gridSize = 10;

		fruit = new Slot(gridSize / 2, gridSize / 2 + 1);

		for (int x = 0; x < gridSize; x++) {
			for (int y = 0; y < gridSize; y++) {
				gridSlots.add(new Slot(x, y));
			}
		}
	}

	@Override public void update (double dt) {
		snake.moveTicks += dt;

		if (snake.moveTicks >= snake.ticksToMove) {
			snake.moveTicks -= snake.ticksToMove;

			Slot newHead = getNewHead();
			if (newHead == null) { // no path found!
				restart();
			} else {
				snake.body.addFirst(newHead);
				boolean foundFruit = (newHead.x == fruit.x && newHead.y == fruit.y);
				if (!foundFruit) {
					snake.body.removeLast(); // remove old tail
				} else {
					randomlyPlace(fruit);
				}

			}
		}
	}

	private ArrayList<Slot> path;

	private Slot getNewHead () {
		search(fruit);
		if (path.isEmpty()) return null;
		return path.remove(0);
	}

	// breadth-first search
	private void search (Slot target) {
		if (path.size() > 0) return;

		// mark occupied slots on board
		boolean[][] visited = new boolean[gridSize][gridSize];
		for (int i = 1; i < snake.body.size(); i++) { // ignore head.
			Slot body = snake.body.get(i);
			visited[body.x][body.y] = true;
		}

		Slot head = snake.body.getFirst();

		ArrayList<Node> queue = new ArrayList<Node>();
		queue.add(Node.root(head.x, head.y));

		Node backtrack = null;
		while (queue.size() > 0) {
			Node current = queue.remove(0);
			Node[] children = getChildren(current);

			backtrack = current; // we can't always find the path to the fruit (e.g. the snake body blocks). Therefore we store any valid move first.
			if (current.x == target.x && current.y == target.y) break; // if the path to the fruit is found the loop breaks

			for (Node child : children) {
				boolean outsideGrid = child.x < 0 || child.y < 0 || gridSize - 1 < child.x || gridSize - 1 < child.y;
				if (outsideGrid || visited[child.x][child.y]) continue;
				visited[child.x][child.y] = true;
				queue.add(child);
			}
		}

		// backtrack to get the steps.
		while (backtrack.parent != null) {
			path.add(new Slot(backtrack.x, backtrack.y));
			backtrack = backtrack.parent;
		}

		Collections.reverse(path);
	}

	private Node[] getChildren (Node parent) {
		return new Node[] {
				Node.child(parent.x + 1, parent.y, parent),
				Node.child(parent.x - 1, parent.y, parent),
				Node.child(parent.x, parent.y + 1, parent),
				Node.child(parent.x, parent.y - 1, parent)
		};
	}

	public void randomlyPlace (Slot fruit) {
		ArrayList<Slot> open = new ArrayList<Slot>();
		open.addAll(gridSlots);
		open.removeAll(snake.body);

		if (open.size() > 0) {
			int random = Util.getRandomInt(0, open.size() - 1);
			Slot position = open.get(random);
			fruit.x = position.x;
			fruit.y = position.y;
		} else {
			restart();
		}
	}
}
