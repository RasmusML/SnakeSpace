package snake.mvc.game.model;

public class Slot {

	public int x, y;

	public Slot () {}

	public Slot (int x, int y) {
		this.x = x;
		this.y = y;
	}

	public Slot (Slot s) {
		this.x = s.x;
		this.y = s.y;
	}

	@Override public boolean equals (Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		Slot other = (Slot) obj;
		if (x != other.x) return false;
		if (y != other.y) return false;
		return true;
	}

}
