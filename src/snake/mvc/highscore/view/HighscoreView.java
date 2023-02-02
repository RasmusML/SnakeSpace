package snake.mvc.highscore.view;

import java.util.ArrayList;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import snake.mvc.View;
import snake.mvc.game.view.GameColor;
import snake.mvc.gameover.model.Pair;
import snake.mvc.highscore.model.HighscoreModel;

public class HighscoreView extends View {

	public VBox vertical;

	private HighscoreModel model;

	private Label topFivetitle;
	private ArrayList<Label> scores;

	public HighscoreView (HighscoreModel model) {
		this.model = model;
	}

	@Override public void create () {
		root = new StackPane();
		root.setBackground(new Background(new BackgroundFill(GameColor.background, CornerRadii.EMPTY, Insets.EMPTY)));

		vertical = new VBox();
		vertical.setSpacing(10);
		vertical.setAlignment(Pos.TOP_CENTER);

		topFivetitle = new Label();
		topFivetitle.setText("- Top 5 - ");
		topFivetitle.setTextFill(Color.WHITE);
		topFivetitle.setStyle("-fx-font-size: 6em; -fx-font-weight: bold;");

		VBox.setMargin(topFivetitle, new Insets(100, 0, 0, 0));

		root.getChildren().add(vertical);

		scores = new ArrayList<Label>();
	}

	@Override public void enter () {
		vertical.getChildren().clear();
		vertical.getChildren().add(topFivetitle);

		scores.clear();

		int scoresToShow = 5;
		for (int i = 0; i < scoresToShow; i++) {
			String out = "---";

			if (i <= model.leaderboard.size() - 1) {
				Pair pair = model.leaderboard.get(i);
				out = String.format("%s : %d", pair.name, pair.score);
			}

			int rank = i + 1;

			Label score = new Label();
			score.setTextFill(Color.WHITE);

			String output = String.format("%d. %s", rank, out);

			score.setText(output);
			score.setStyle("-fx-font-size: 2em; -fx-font-weight: bold;");
			scores.add(score);
		}

		vertical.getChildren().addAll(scores);

	}

}
