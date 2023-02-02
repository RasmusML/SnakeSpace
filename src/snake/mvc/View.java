package snake.mvc;

import javafx.scene.layout.Pane;

public class View {

	public Pane root; // instead of changing scene, we change root, because changing scene makes the window resize
										// if the window is fullscreen, and we change scene, then it goes out of fullscreen.

	public void create () {}

	public void enter () {}

	public void draw () {}

	public void resize (int width, int height) {}
}
