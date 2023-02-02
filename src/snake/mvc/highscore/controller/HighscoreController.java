package snake.mvc.highscore.controller;

import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import snake.mvc.ContextType;
import snake.mvc.Controller;
import snake.mvc.highscore.model.HighscoreModel;
import snake.mvc.highscore.view.HighscoreView;

public class HighscoreController extends Controller {

	private HighscoreModel model;
	private HighscoreView view;

	public HighscoreController (HighscoreModel model, HighscoreView view) {
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
