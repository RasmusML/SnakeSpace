package snake.mvc.menu.controller;

import java.util.function.UnaryOperator;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextFormatter.Change;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import snake.mvc.ContextType;
import snake.mvc.Controller;
import snake.mvc.menu.model.MenuModel;
import snake.mvc.menu.view.MenuView;
import snake.mvc.shared.Util;

public class MenuController extends Controller {

	private MenuModel model;
	private MenuView view;

	public MenuController (MenuModel model, MenuView view) {
		this.model = model;
		this.view = view;
	}

	@Override public void enter () {

		// change scene to game
		view.playButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle (ActionEvent event) {
				model.manager.changeActiveContext(ContextType.game);
			}
		});

		// change scene to highscore
		view.highscoresButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle (ActionEvent event) {
				model.manager.changeActiveContext(ContextType.highscore);
			}
		});

		// don't allow to right-click on the username field to open copy/paste menu.
		view.usernameField.addEventFilter(ContextMenuEvent.CONTEXT_MENU_REQUESTED, new EventHandler<Event>() {
			@Override public void handle (Event event) {
				event.consume();
			}
		});

		// '#' is a reserved keyword in the username. It is used to split the username and score in the highscore.txt
		view.usernameField.setTextFormatter(new TextFormatter<String>(new UnaryOperator<Change>() {

			@Override public Change apply (Change change) {
				String character = change.getText(); // text is always maximum 1 character.
				int length = character.length();

				// note: deleting a character and using the arrow keys is always allowed. 
				// When length is 0, it means that, the arrows keys have been clicked or backspace (delete)
				if (character.isEmpty()) return change;

				int maxNameLength = 15;
				int totalLength = view.usernameField.getText().length();
				if (totalLength > maxNameLength) return null;

				if (length > 0) {
					// '#' is used to divide name and score in the highscore file; name#score.
					boolean reservedCharacter = character.charAt(0) == '#';
					if (reservedCharacter) return null;
				}

				return change;
			}
		}));

		// update username.
		view.usernameField.textProperty().addListener(new ChangeListener<String>() {

			@Override public void changed (ObservableValue<? extends String> observable, String oldValue, String newValue) {
				model.userProfile.name = newValue;
			}
		});

		// fullscreen toggle.
		view.fullScreenToggle.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override public void changed (ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				model.manager.setFullscreen(newValue);
			}
		});

		view.root.getScene().setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override public void handle (KeyEvent event) {
				if (event.getCode() == KeyCode.ESCAPE) {
					Util.stopProgram();
				}
			}
		});
	}
}
