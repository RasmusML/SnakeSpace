package snake.mvc.game.model;

public enum Direction {
	left (-1, 0),
	right (1, 0),
	up (0, -1),
	down (0, 1);

	public final int dx, dy;

	private Direction (int dx, int dy) {
		this.dx = dx;
		this.dy = dy;
	}
}
