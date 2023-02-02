package snake.mvc.game.model;

import java.util.ArrayList;

import snake.mvc.ContextType;
import snake.mvc.Model;
import snake.mvc.Profile;
import snake.mvc.game.model.entity.Bomb;
import snake.mvc.game.model.entity.Entity;
import snake.mvc.game.model.entity.EntityType;
import snake.mvc.game.model.entity.Food;
import snake.mvc.game.model.entity.Rabbit;
import snake.mvc.shared.Util;

public class GameModel extends Model {

	public Snake snake;
	public Grid grid;

	public int score;
	public double scoreTicks, ticksToUpdateScore;

	public boolean pause;

	public double gameTime;
	public boolean gameOver;

	public double countDownToStart;

	public Direction requestedDirection;

	// handles score
	public int bonus;
	public int penalty;
	private double penaltyPercentage;
	private int penaltyConstant;

	public ArrayList<Entity> entities;

	private Profile userProfile;

	public GameModel (Profile userProfile) {
		this.userProfile = userProfile;
	}

	@Override public void enter () {
		reset();
	}

	private void reset () {
		score = 0;
		
		pause = false;

		penaltyPercentage = .8;
		penaltyConstant = 2;
		penalty = 0;
		bonus = 0;

		gameOver = false;
		gameTime = 3 * 60;

		ticksToUpdateScore = 1f; // sec

		countDownToStart = 3; // sec

		grid = new Grid();
		snake = new Snake(grid);

		entities = new ArrayList<Entity>();

		int foodPopulation = 15;
		for (int i = 0; i < foodPopulation; i++) {
			Food food = new Food();
			food.placeRandomly(snake, entities, grid);
			entities.add(food);
		}

		int rabbitPopulation = 12;
		for (int i = 0; i < rabbitPopulation; i++) {
			Rabbit rabbit = new Rabbit();
			rabbit.placeRandomly(snake, entities, grid);
			entities.add(rabbit);
		}

		int bombPopulation = 5;
		for (int i = 0; i < bombPopulation; i++) {
			Bomb bomb = new Bomb();
			bomb.placeRandomly(snake, entities, grid);
			entities.add(bomb);
		}
	}

	@Override public void update (double dt) {
		if(pause) return;
		
		countDownToStart -= dt;
		if (countDownToStart > 0) return;

		if (gameTime < 1) gameOver = true;
		if (gameOver) {
			userProfile.score = score;
			manager.changeActiveContext(ContextType.gameover);
			return;
		}

		gameTime -= dt;

		// update penalty and bonus
		bonus = snake.body.size();
		if (snake.ticksToMove == snake.runTicksToMove) {
			penalty = Util.floorInt(penaltyPercentage * bonus) + penaltyConstant;
		} else {
			penalty = 0;
		}

		// update score
		scoreTicks += dt;
		if (scoreTicks >= ticksToUpdateScore) {
			scoreTicks -= ticksToUpdateScore;
			score += bonus - penalty;
		}

		// update rabbits
		ArrayList<Rabbit> rabbits = get(EntityType.rabbit);
		for (Rabbit rabbit : rabbits) {
			rabbit.moveTicks += dt;

			if (rabbit.moveTicks >= rabbit.ticksToMove) {
				rabbit.moveTicks -= rabbit.ticksToMove;
				rabbit.previousMove = null;
				rabbit.moveAway(snake, entities, grid);
			}
		}

		// update snake
		snake.moveTicks += dt;
		if (snake.moveTicks >= snake.ticksToMove) {
			snake.moveTicks -= snake.ticksToMove;

			SnakeMoveResult moveResult = snake.move(entities, grid);

			if (moveResult.bombHit) {
				int shorten = snake.body.size() / 2;
				snake.shorten(shorten);
			}

			// Instead of dying, the snake eats (removes) its body up to the self-collision point. 
			if (moveResult.selfCollided) {
				Slot head = snake.body.getFirst();
				Slot tail = snake.body.getLast();

				// there are 2 cases:
				//  1. the snake eats the tail, then remove the tail (inclusive tail)
				//  2. the snake eats part of the body (not tail), then remove up to that body-part (exclusive this body-part)
				//  By handling these as 2 cases, the gameplay feels smoother.

				if (head.equals(tail)) {
					snake.shorten(1);
				} else {
					int collisionIndex = snake.body.lastIndexOf(head); // ignore the head, so find the last one
					int shorten = snake.body.size() - collisionIndex - 1;
					snake.shorten(shorten);
				}
			}

			snake.strangle(entities, grid);
			snake.digest();

			// re-spawn entities if there were killed by strangle or snake moved into them.
			for (Entity entity : entities) {
				if (entity.dead) {
					entity.placeRandomly(snake, entities, grid);
				}
			}

			// if a valid requested direction exists, use that as the next move, else keep the current snake direction as the next move.
			snake.move = requestedDirection != null ? requestedDirection : snake.direction;
			requestedDirection = null;

			// apply grid effect on direction change.
			if (snake.move != snake.direction) {
				grid.applyForce(snake);
			}

			// gameover check
			int gridSize = grid.width * grid.height;
			gameOver = (moveResult.wallCollision || (gridSize == snake.body.size()));

		}

		// update grid
		grid.update();
	}

	public void requestMove (Direction request) {
		Direction currentMove = snake.move;
		boolean oppositeDirections = request.dx + currentMove.dx == 0 && request.dy + currentMove.dy == 0;
		if (!oppositeDirections) requestedDirection = request;
	}

	/*
	 * returns an ArrayList with entities of the specified type
	 */
	private <T extends Entity> ArrayList<T> get (EntityType type) {
		ArrayList<T> filtered = new ArrayList<T>();

		for (Entity e : entities) {
			if (e.type == type) filtered.add((T) e);
		}

		return filtered;
	}
}
