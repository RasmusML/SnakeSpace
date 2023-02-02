package snake.mvc.gameover.controller;

import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import snake.mvc.ContextType;
import snake.mvc.Controller;
import snake.mvc.gameover.model.GameOverModel;
import snake.mvc.gameover.view.GameOverView;

public class GameOverController extends Controller {

	private GameOverModel model;
	private GameOverView view;

	public GameOverController (GameOverModel model, GameOverView view) {
		this.model = model;
		this.view = view;
	}

	@Override public void enter () {
		view.root.getScene().setOnKeyPressed(new EventHandler<KeyEvent>() {

			@Override public void handle (KeyEvent event) {
				model.manager.changeActiveContext(ContextType.menu);
			}
		});

		view.root.getScene().setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override public void handle (MouseEvent event) {
				model.manager.changeActiveContext(ContextType.menu);
			}
		});
	}
}
