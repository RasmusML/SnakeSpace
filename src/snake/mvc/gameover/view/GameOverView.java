package snake.mvc.gameover.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import snake.mvc.Profile;
import snake.mvc.View;
import snake.mvc.game.view.GameColor;
import snake.mvc.gameover.model.GameOverModel;

public class GameOverView extends View {

	private GameOverModel model;

	private Label gameOverText;
	private Label highscoreText;

	private VBox box;

	public GameOverView (GameOverModel model) {
		this.model = model;
	}

	@Override public void create () {
		root = new StackPane();
		root.setBackground(new Background(new BackgroundFill(GameColor.background, CornerRadii.EMPTY, Insets.EMPTY)));

		gameOverText = new Label();
		gameOverText.setTextFill(Color.WHITE);
		gameOverText.setStyle("-fx-font-size: 6em; -fx-font-weight: bold;");
		gameOverText.setText("Game Over!");

		highscoreText = new Label();
		highscoreText.setTextFill(Color.WHITE);
		highscoreText.setStyle("-fx-font-size: 4em; -fx-font-weight: bold;");

		box = new VBox();
		box.setSpacing(15);
		box.setAlignment(Pos.CENTER);

		box.getChildren().addAll(gameOverText, highscoreText);
		root.getChildren().add(box);
	}

	@Override public void enter () {
		Profile userProfile = model.userProfile;
		highscoreText.setText(String.format("%d. %s %d", model.placement, userProfile.name, userProfile.score));
	}
}
