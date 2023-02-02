package snake.mvc.game.view;

import javafx.geometry.Insets;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import snake.mvc.View;
import snake.mvc.game.model.Consumption;
import snake.mvc.game.model.Direction;
import snake.mvc.game.model.GameModel;
import snake.mvc.game.model.Grid;
import snake.mvc.game.model.Slot;
import snake.mvc.game.model.Snake;
import snake.mvc.game.model.StrangleArea;
import snake.mvc.game.model.Vector2;
import snake.mvc.game.model.entity.Entity;
import snake.mvc.game.model.entity.Rabbit;
import snake.mvc.shared.Util;

public class GameView extends View {

	public Canvas canvas;

	private Renderer renderer;
	private Camera camera;
	private FitViewport viewport;

	private GameModel model;

	public GameView (GameModel gameModel) {
		this.model = gameModel;
	}

	@Override public void create () {
		double virtualWidth = 600;
		double virtualHeight = 400;

		camera = new Camera();
		viewport = new FitViewport(virtualWidth, virtualHeight, camera);
		renderer = new Renderer(viewport);

		root = new Pane();
		canvas = new Canvas(virtualWidth, virtualHeight);
		root.getChildren().add(canvas);

		root.setBackground(new Background(new BackgroundFill(GameColor.background, CornerRadii.EMPTY, Insets.EMPTY)));
	}

	@Override public void enter () {

		Snake snake = model.snake;
		Grid grid = model.grid;

		Slot head = snake.body.getFirst();
		Vector2 headPosition = grid.toPosition(head);

		camera.lookAt(headPosition.x, headPosition.y);
		camera.setZoom(.2);
		camera.zoom(.8);
	}

	public void draw () {
		drawGame();
		drawHUD();
	}

	private void drawHUD () {

		{ // count down
			if (model.countDownToStart > 0) {
				String text = String.format("%.1f", model.countDownToStart);
				int fontSize = 32;
				double x = viewport.virtualWidth / 2;
				double y = 60;
				renderer.drawText(x, y, text, GameColor.countdown, fontSize, Alignment.center, canvas);
			}
		}

		{ // score

			int scoreFontSize = 16;
			int bonusFontSize = 10;
			int penaltyFontSize = 10;

			double offset = 2;

			{
				int score = model.score;
				int change = model.bonus - model.penalty;

				String sign = change > 0 ? "+" : "";
				String scoreText = String.format("%d %s%d", score, sign, change);

				double x = offset;
				double y = offset;
				renderer.drawText(x, y, scoreText, GameColor.score, scoreFontSize, Alignment.topLeft, canvas);
			}

			{ // bonus
				int bonus = model.bonus;
				String text = String.format("+%d", bonus);

				double x = offset;
				double y = scoreFontSize + offset;
				renderer.drawText(x, y, text, GameColor.bonus, bonusFontSize, Alignment.topLeft, canvas);

			}

			{ // penalty
				int penalty = model.penalty;
				if (penalty != 0) {
					String text = String.format("-%d", penalty);

					double x = offset;
					double y = scoreFontSize + bonusFontSize + offset;
					renderer.drawText(x, y, text, GameColor.penalty, penaltyFontSize, Alignment.topLeft, canvas);
				}
			}
		}

		{ // clock
			int minutes = (int) (model.gameTime / 60.0);
			int seconds = (int) (model.gameTime % 60.0);

			// add a zero if seconds is less than 10, thus 3:8 becomes 3:08
			String maybeZero = seconds < 10 ? "0" : "";

			String text = String.format("%d:%s%d", minutes, maybeZero, seconds);
			int fontSize = 16;
			double offset = 2;
			double x = viewport.virtualWidth - offset;
			double y = offset;
			renderer.drawText(x, y, text, GameColor.clock, fontSize, Alignment.topRight, canvas);
		}
	}

	private void drawGame () {
		Grid grid = model.grid;
		Snake snake = model.snake;

		renderer.clear(GameColor.background, canvas);

		{ // draw grid
			int width = grid.width;
			int height = grid.height;

			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					Vector2 p = grid.toPosition(x, y);

					Vector2 left = null;
					Vector2 up = null;

					if (x > 0) {
						left = grid.toPosition(x - 1, y);
						double thickness = 3f;
						renderer.drawLine(left.x, left.y, p.x, p.y, GameColor.grid, thickness, canvas);
					}

					if (y > 0) {
						up = grid.toPosition(x, y - 1);
						double thickness = 3f;
						renderer.drawLine(up.x, up.y, p.x, p.y, GameColor.grid, thickness, canvas);
					}

					if (x > 0 && y > 0) {
						Vector2 upLeft = grid.toPosition(x - 1, y - 1);

						double thickness = 1.0;
						renderer.drawLine(
							.5 * (upLeft.x + up.x),
							.5 * (upLeft.y + up.y),
							.5 * (left.x + p.x),
							.5 * (left.y + p.y),
							GameColor.grid, thickness, canvas); // vertical line

						renderer.drawLine(
							.5 * (upLeft.x + left.x),
							.5 * (upLeft.y + left.y),
							.5 * (up.x + p.x),
							.5 * (up.y + p.y),
							GameColor.grid, thickness, canvas); // horizontal line
					}
				}
			}
		}

		{ // camera look at snake head
			Slot head = snake.body.getFirst();
			Vector2 position = grid.toPosition(head);

			Direction moveDirection = snake.move;

			double percentage = snake.moveTicks / snake.ticksToMove;

			Vector2 next = grid.toPosition(head.x + moveDirection.dx, head.y + moveDirection.dy);
			Vector2 delta = new Vector2(next).sub(position); // create a vector2 between head position and next head position
			delta.scl(percentage); // only move % traveled yet.

			double x = position.x + delta.x;
			double y = position.y + delta.y;

			if (!model.pause) {
				camera.update();
				//						camera.lookAt(x, y);
				camera.moveTowards(x, y);
			}
		}

		{ // snake
			{ // draw tail
				Slot afterTail = snake.body.get(snake.body.size() - 1 - 1);
				Vector2 afterPosition = grid.toPosition(afterTail);

				Slot tail = snake.body.getLast();
				Vector2 tailPosition = grid.toPosition(tail);
				Vector2 delta = new Vector2(afterPosition).sub(tailPosition);

				double percentage = snake.moveTicks / snake.ticksToMove;
				if (snake.growingLeft > 0) percentage = 0; // if snake is growing, don't shorten tail
				delta.scl(percentage);

				double x = afterPosition.x;
				double y = afterPosition.y;

				double tx = tailPosition.x + delta.x;
				double ty = tailPosition.y + delta.y;

				double thickness = 2;

				renderer.drawLine(x, y, tx, ty, GameColor.snake, thickness, canvas);
			}

			// ignore the tail
			for (int i = 0; i <= snake.body.size() - 3; i++) {
				Slot slot = snake.body.get(i);
				Slot after = snake.body.get(i + 1);

				Vector2 position = grid.toPosition(slot);
				double x = position.x;
				double y = position.y;

				Vector2 afterPosition = grid.toPosition(after);
				double tx = afterPosition.x;
				double ty = afterPosition.y;

				double thickness = 2;

				renderer.drawLine(x, y, tx, ty, GameColor.snake, thickness, canvas);
			}

			{ // draw head
				Slot head = snake.body.getFirst();
				Vector2 position = grid.toPosition(head);

				double x = position.x;
				double y = position.y;

				Direction headDirection = snake.move;

				Vector2 next = grid.toPosition(head.x + headDirection.dx, head.y + headDirection.dy);
				Vector2 delta = new Vector2(next).sub(position);

				double percentage = snake.moveTicks / snake.ticksToMove;
				delta.scl(percentage);

				double tx = x + delta.x;
				double ty = y + delta.y;

				double neckThickness = 2;

				renderer.drawLine(x, y, tx, ty, GameColor.snake, neckThickness, canvas);

				double size = grid.tilesize / 2.0;
				double headX = tx - size / 2.0;
				double headY = ty - size / 2.0;

				double headThickness = 3;
				renderer.drawRectLine(headX, headY, size, size, GameColor.snake, headThickness, canvas);
			}
		}

		{ // entities
			for (Entity entity : model.entities) {
				if (entity.dead) continue;

				switch (entity.type) {

					case food: {
						Slot slot = entity.position;
						Vector2 position = grid.toPosition(slot);

						double radius = 6;
						double x = position.x - radius;
						double y = position.y - radius;
						double thickness = 2;

						renderer.drawCircleLine(x, y, radius, GameColor.food, thickness, canvas);
						break;
					}

					case rabbit: {
						Rabbit rabbit = (Rabbit) entity;

						Slot slot = rabbit.position;
						Vector2 position = grid.toPosition(slot);
						double dx = 0;
						double dy = 0;

						// the rabbit is always 1 move in front, to make it more difficult to catch it!
						boolean moving = rabbit.previousMove != null;
						if (moving) {
							Slot previous = new Slot(slot.x + rabbit.previousMove.dx, slot.y + rabbit.previousMove.dy);
							Vector2 previousPosition = grid.toPosition(previous);

							// therefore it gets simulated as if it is on the way to its current position.
							double percentage = rabbit.moveTicks / rabbit.ticksToMove;
							double reversed = 1.0 - percentage;
							dx = (position.x - previousPosition.x) * reversed;
							dy = (position.y - previousPosition.y) * reversed;

						}

						double radius = 8;
						double thickness = 2;
						double x = position.x + dx - radius;
						double y = position.y + dy - radius;
						renderer.drawCircleLine(x, y, radius, GameColor.rabbit, thickness, canvas);

						break;
					}

					case bomb: {
						Slot slot = entity.position;
						Vector2 position = grid.toPosition(slot);

						double radius = 8;
						double x = position.x - radius;
						double y = position.y - radius;
						double thickness = 2;

						renderer.drawCircleLine(x, y, radius, GameColor.bomb, thickness, canvas);
						break;
					}

					default: {
						throw new IllegalStateException("not drawing entities of this type: " + entity.type);
					}
				}

			}
		}

		{
			for (Consumption consumption : snake.consuming) {
				Slot slot = consumption.position;
				Vector2 position = grid.toPosition(slot);

				double radius = 3;
				double x = position.x - radius;
				double y = position.y - radius;
				double thickness = Util.clamp(2 * consumption.growthAmount, 1, 6);

				renderer.drawCircleLine(x, y, radius, GameColor.consumption, thickness, canvas);
			}
		}

		{ // strangles
			for (StrangleArea region : snake.strangles) {
				for (Slot slot : region.strangle) {

					/*
					 * p1--p2
					 *  |  |
					 * p4--p3
					 */

					Vector2 p1 = grid.toCenterPosition(slot.x, slot.y);
					Vector2 p2 = grid.toCenterPosition(slot.x + 1, slot.y);
					Vector2 p3 = grid.toCenterPosition(slot.x + 1, slot.y + 1);
					Vector2 p4 = grid.toCenterPosition(slot.x, slot.y + 1);

					double[][] polygon = new double[][] {
							{ p1.x, p1.y },
							{ p2.x, p2.y },
							{ p3.x, p3.y },
							{ p4.x, p4.y },
					};

					renderer.drawFillPolygon(polygon, GameColor.strangle, canvas);
				}
			}
		}
	}

	public void resize (int screenWidth, int screenHeight) {
		viewport.update(screenWidth, screenHeight);

		canvas.setWidth(viewport.virtualWidth * viewport.scale);
		canvas.setHeight(viewport.virtualHeight * viewport.scale);

		canvas.setTranslateX((screenWidth - canvas.getWidth()) / 2.0);
		canvas.setTranslateY((screenHeight - canvas.getHeight()) / 2.0);
	}

}