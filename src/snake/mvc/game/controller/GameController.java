package snake.mvc.game.controller;

import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import snake.mvc.ContextType;
import snake.mvc.Controller;
import snake.mvc.game.model.Direction;
import snake.mvc.game.model.GameModel;
import snake.mvc.game.model.Snake;
import snake.mvc.game.view.GameView;

public class GameController extends Controller {

	private GameModel model;
	private GameView view;

	public GameController (GameModel gameModel, GameView gameView) {
		this.model = gameModel;
		this.view = gameView;
	}

	@Override public void enter () {
		view.root.getScene().setOnKeyPressed(new EventHandler<KeyEvent>() {

			@Override public void handle (KeyEvent event) {

				KeyCode key = event.getCode();
				if (key == KeyCode.LEFT || key == KeyCode.A || key == KeyCode.NUMPAD4) {
					model.requestMove(Direction.left);
				}

				if (key == KeyCode.RIGHT || key == KeyCode.D || key == KeyCode.NUMPAD6) {
					model.requestMove(Direction.right);
				}

				if (key == KeyCode.UP || key == KeyCode.W || key == KeyCode.NUMPAD8) {
					model.requestMove(Direction.up);
				}

				if (key == KeyCode.DOWN || key == KeyCode.S || key == KeyCode.NUMPAD2) {
					model.requestMove(Direction.down);
				}

				if (key == KeyCode.SPACE) {

					Snake snake = model.snake;
					if (snake.ticksToMove != snake.runTicksToMove) {

						// when the snake changes moveSpeed (and thereby ticksToMove), it has to keep the ratio between current moveTicks and ticksToMove to stay consistent with movement interpolation.
						double percentage = snake.moveTicks / snake.walkTicksToMove;
						double elapsed = snake.runTicksToMove * percentage;

						snake.ticksToMove = snake.runTicksToMove;
						snake.moveTicks = elapsed;
					}
				}

				if (key == KeyCode.R) {
					model.manager.changeActiveContext(ContextType.game);
				}

				if (key == KeyCode.ESCAPE) {
					model.manager.changeActiveContext(ContextType.menu);
				}

				if (key == KeyCode.P) {
					model.pause = !model.pause;
				}
			}
		});

		view.root.getScene().setOnKeyReleased(new EventHandler<KeyEvent>() {
			@Override public void handle (KeyEvent event) {

				KeyCode key = event.getCode();
				if (key == KeyCode.SPACE) {
					Snake snake = model.snake;

					// when the snake changes moveSpeed (and thereby ticksToMove), it has to keep the ratio between current moveTicks and ticksToMove to stay consistent with movement interpolation.
					double percentage = snake.moveTicks / snake.runTicksToMove;
					double elapsed = snake.walkTicksToMove * percentage;

					snake.ticksToMove = snake.walkTicksToMove;
					snake.moveTicks = elapsed;
				}
			}
		});
	}
}
