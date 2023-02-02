package snake.mvc.game.model;

public class Consumption {

	public Slot position;
	public int growthAmount;

	public Consumption (Slot position, int growthAmount) {
		this.position = position;
		this.growthAmount = growthAmount;
	}
}
