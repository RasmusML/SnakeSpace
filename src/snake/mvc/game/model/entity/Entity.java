package snake.mvc.game.model.entity;

import java.util.ArrayList;

import snake.mvc.game.model.Grid;
import snake.mvc.game.model.Slot;
import snake.mvc.game.model.Snake;
import snake.mvc.shared.Util;

public class Entity {

	public boolean dead;
	public Slot position = new Slot();
	public EntityType type;
	public int growth;

	public Entity (EntityType type, int growthAmounth) {
		this.type = type;
		this.growth = growthAmounth;
	}

	public void placeRandomly (Snake snake, ArrayList<Entity> entities, Grid grid) {
		ArrayList<Slot> open = grid.getOpenSlots(snake, entities);

		if (open.size() > 0) {
			int random = Util.getRandomInt(0, open.size() - 1);
			Slot newPosition = open.get(random);
			position.x = newPosition.x;
			position.y = newPosition.y;
			dead = false;
		} else {
			dead = true; // don't re-spawn if there is no space on the grid.
		}
	}
}
