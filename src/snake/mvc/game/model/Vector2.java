package snake.mvc.game.model;

public class Vector2 {

	public double x, y;

	public Vector2 () {}

	public Vector2 (Vector2 v) {
		this.x = v.x;
		this.y = v.y;
	}

	public Vector2 (double x, double y) {
		this.x = x;
		this.y = y;
	}

	public Vector2 set (double x, double y) {
		this.x = x;
		this.y = y;
		return this;
	}

	public Vector2 set (Vector2 v) {
		this.x = v.x;
		this.y = v.y;
		return this;
	}

	public Vector2 scl (double scalar) {
		this.x *= scalar;
		this.y *= scalar;
		return this;
	}

	public Vector2 normalize () {
		double len = length();
		if (len == 0) return this;
		x /= len;
		y /= len;
		return this;
	}

	public Vector2 sub (Vector2 v) {
		this.x -= v.x;
		this.y -= v.y;
		return this;
	}

	public Vector2 add (double x, double y) {
		this.x += x;
		this.y += y;
		return this;
	}

	public Vector2 sub (double x, double y) {
		this.x -= x;
		this.y -= y;
		return this;
	}

	public Vector2 add (Vector2 v) {
		this.x += v.x;
		this.y += v.y;
		return this;
	}

	public Vector2 copy () {
		return new Vector2(x, y);
	}

	public double lengthSquared () {
		return x * x + y * y;
	}

	public double distance (Vector2 position) {
		double dx = position.x - x;
		double dy = position.y - y;
		return Math.sqrt(dx * dx + dy * dy);
	}

	public double length () {
		return Math.sqrt(x * x + y * y);
	}

	public double distanceSquared (Vector2 vec) {
		double dx = vec.x - x;
		double dy = vec.y - y;
		return dx * dx + dy * dy;
	}
}
