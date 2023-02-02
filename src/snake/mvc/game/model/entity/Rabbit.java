package snake.mvc.game.model.entity;

import java.util.ArrayList;

import snake.mvc.game.model.Direction;
import snake.mvc.game.model.Grid;
import snake.mvc.game.model.Slot;
import snake.mvc.game.model.Snake;
import snake.mvc.shared.Util;

public class Rabbit extends Entity {

	public double moveTicks, ticksToMove;

	public Direction previousMove;

	public Rabbit () {
		super(EntityType.rabbit, 2);

		ticksToMove = .3; // sec
	}

	public void moveAway (Snake snake, ArrayList<Entity> entities, Grid grid) {
		Slot head = snake.body.getFirst();

		// same vertical or horizontal line
		boolean onSameLine = head.x == position.x || head.y == position.y;
		if (onSameLine) {
			int sdx = Util.sign(position.x - head.x);
			int sdy = Util.sign(position.y - head.y);

			boolean snakeChasing = sdx == snake.move.dx && sdy == snake.move.dy;
			if (snakeChasing) {
				ArrayList<Direction> moves = getGoodMoves(snake);

				for (Direction move : moves) {
					Slot newPosition = new Slot(position.x + move.dx, position.y + move.dy);

					ArrayList<Slot> open = grid.getOpenSlots(snake, entities);
					if (open.contains(newPosition)) {
						previousMove = move;
						position = newPosition;
						break;
					}
				}
			}
		}
	}

	// never go in the opposite direction of the snake
	private ArrayList<Direction> getGoodMoves (Snake snake) {
		ArrayList<Direction> directions = new ArrayList<Direction>();
		directions.add(snake.move);

		switch (snake.move) {
			case up:
			case down: {
				directions.add(Direction.left);
				directions.add(Direction.right);
				break;
			}

			case left:
			case right: {
				directions.add(Direction.up);
				directions.add(Direction.down);
				break;
			}
		}

		return directions;
	}
}
