package snake.mvc.menu.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import snake.mvc.View;
import snake.mvc.game.view.GameColor;
import snake.mvc.menu.model.MenuModel;
import snake.mvc.menu.model.Slot;

public class MenuView extends View {

	public Button playButton;
	public Button highscoresButton;
	public TextField usernameField;
	public Canvas retroSnakeCanvas;

	public CheckBox fullScreenToggle;
	public VBox verticalGroup;

	private MenuModel model;

	public int tilesize;

	private Color background;

	public MenuView (MenuModel model) {
		this.model = model;
	}

	@Override public void create () {
		root = new StackPane();
		root.setBackground(new Background(new BackgroundFill(GameColor.background, CornerRadii.EMPTY, Insets.EMPTY)));

		verticalGroup = new VBox();
		verticalGroup.setSpacing(15);
		verticalGroup.setAlignment(Pos.CENTER);

		double buttonWidth = 150;
		double buttonHeight = 40;

		background = new Color(186 / 255.0, 196 / 255.0, 1 / 255.0, 1);

		playButton = new Button("Play");
		playButton.setPrefWidth(buttonWidth);
		playButton.setMaxWidth(buttonWidth);
		playButton.setMinHeight(buttonHeight);
		playButton.setStyle("-fx-font-size: 2em; -fx-font-weight: bold;");

		highscoresButton = new Button("Highscore");
		highscoresButton.setPrefWidth(buttonWidth);
		highscoresButton.setMaxWidth(buttonWidth);
		highscoresButton.setMinHeight(buttonHeight);
		highscoresButton.setStyle("-fx-font-size: 1em; -fx-font-weight: bold;");

		usernameField = new TextField();
		usernameField.setPrefWidth(buttonWidth);
		usernameField.setMaxWidth(buttonWidth);
		usernameField.setMinHeight(buttonHeight);
		usernameField.setPromptText("Username");
		usernameField.setAlignment(Pos.CENTER);
		usernameField.setStyle("-fx-font-weight: bold;");

		fullScreenToggle = new CheckBox("Fullscreen");

		int canvasSize = 170;
		tilesize = canvasSize / model.gridSize;
		retroSnakeCanvas = new Canvas(canvasSize, canvasSize);

		VBox.setMargin(retroSnakeCanvas, new Insets(30, 30, 30, 30));

		verticalGroup.getChildren().addAll(retroSnakeCanvas, playButton, highscoresButton, usernameField, fullScreenToggle);
		root.getChildren().add(verticalGroup);
	}

	@Override public void draw () {
		clear(background);

		// grid
		for (int y = 0; y < model.gridSize; y++) {
			for (int x = 0; x < model.gridSize; x++) {
				double thickness = .5;
				drawRectLine(x * tilesize, y * tilesize, tilesize, tilesize, background, thickness);
			}
		}

		// snake
		for (Slot body : model.snake.body) {
			double gap = 0.8;
			double x = body.x * tilesize + gap;
			double y = body.y * tilesize + gap;
			Color bodyColor = new Color(129 / 255.0, 130 / 255.0, 22 / 255, 1);
			drawRectFill(x, y, tilesize - 2 * gap, tilesize - 2 * gap, bodyColor);
		}

		{ // fruit
			Slot fruit = model.fruit;
			double gap = 0.8;
			double size = 10;

			double cx = fruit.x * tilesize + tilesize / 2;
			double cy = fruit.y * tilesize + tilesize / 2;

			double x = cx - size / 2 + gap;
			double y = cy - size / 2 + gap;

			drawRectFill(x, y, size - 2 * gap, size - 2 * gap, Color.RED);
		}
	}

	private void clear (Color color) {
		drawRectFill(0, 0, retroSnakeCanvas.getWidth(), retroSnakeCanvas.getHeight(), color);
	}

	private void drawRectLine (double x, double y, double width, double height, Color color, double thickness) {
		GraphicsContext gc = retroSnakeCanvas.getGraphicsContext2D();
		gc.setStroke(color);
		gc.setLineWidth(thickness);
		gc.strokeRect(x, y, width, height);
	}

	private void drawRectFill (double x, double y, double width, double height, Color color) {
		GraphicsContext gc = retroSnakeCanvas.getGraphicsContext2D();
		gc.setFill(color);
		gc.fillRect(x, y, width, height);
	}

}
