package snake.mvc.gameover.model;

public class Pair implements Comparable<Pair> {
	public String name;
	public int score;

	@Override public int compareTo (Pair o) {
		return o.score - score;
	}
}
