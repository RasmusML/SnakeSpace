package snake.mvc;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import snake.mvc.game.controller.GameController;
import snake.mvc.game.model.GameModel;
import snake.mvc.game.view.GameView;
import snake.mvc.gameover.controller.GameOverController;
import snake.mvc.gameover.model.GameOverModel;
import snake.mvc.gameover.view.GameOverView;
import snake.mvc.highscore.controller.HighscoreController;
import snake.mvc.highscore.model.HighscoreModel;
import snake.mvc.highscore.view.HighscoreView;
import snake.mvc.menu.controller.MenuController;
import snake.mvc.menu.model.MenuModel;
import snake.mvc.menu.view.MenuView;
import snake.mvc.shared.Util;

public class App extends Application {

	@Override public void start (Stage stage) throws Exception {
		Util.createFileIfNotExists(Util.highscoreFile);

		Profile userProfile = new Profile();
		userProfile.name = "";
		userProfile.score = 0;

		stage.setMaximized(true);
		stage.setFullScreen(false);
		stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
		
		stage.setTitle("SpaceSnake");

		Scene scene = new Scene(new Pane() /* a (non-null) root is needed, due to javafx (even though it gets changed right away) */, Color.BLACK);
		stage.setScene(scene); 

		ContextManager manager = new ContextManager(scene, stage);

		{
			// setup menu MVC
			// the children of the MVC get created here, because they need to know each other "child-type" to get the global variables.
			MenuModel model = new MenuModel(userProfile);
			MenuView view = new MenuView(model);
			MenuController controller = new MenuController(model, view);
			manager.createMVC(ContextType.menu, model, view, controller);
		}

		{
			// setup highscore MVC
			// the children of the MVC get created here, because they need to know each other "child-type" to get the global variables.
			HighscoreModel model = new HighscoreModel(userProfile);
			HighscoreView view = new HighscoreView(model);
			HighscoreController controller = new HighscoreController(model, view);
			manager.createMVC(ContextType.highscore, model, view, controller);
		}

		{
			// setup game MVC
			// the children of the MVC get created here, because they need to know each other "child-type" to get the global variables.
			GameModel gameModel = new GameModel(userProfile);
			GameView gameView = new GameView(gameModel);
			GameController gameController = new GameController(gameModel, gameView);
			manager.createMVC(ContextType.game, gameModel, gameView, gameController);
		}

		{
			// setup gameover MVC
			// the children of the MVC get created here, because they need to know each other "child-type" to get the global variables.
			GameOverModel model = new GameOverModel(userProfile);
			GameOverView view = new GameOverView(model);
			GameOverController controller = new GameOverController(model, view);
			manager.createMVC(ContextType.gameover, model, view, controller);
		}

		manager.changeActiveContext(ContextType.menu);

		// gameloop
		double dt = 1 / 60.0; // note: fixed timestamp for smoother gameplay

		AnimationTimer gameloop = new AnimationTimer() {
			@Override public void handle (long now) {
				MVCContext active = manager.getActive();
				active.model.update(dt);
				active.view.draw();
			}
		};

		gameloop.start();

		stage.show();

	}
}
