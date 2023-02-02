package snake.mvc.game.model.entity;

public class Bomb extends Entity {

	public Bomb () {
		super(EntityType.bomb, 0 /* the snake doesn't grow when eating bomb. Because the bomb explodes this case is handled in the gamemodel */);
	}
}
