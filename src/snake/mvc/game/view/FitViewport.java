package snake.mvc.game.view;

import snake.mvc.game.model.Vector2;

public class FitViewport {

	public double virtualWidth, virtualHeight;
	public double scale;

	// a camera gets attached to the viewport
	private Camera camera;

	public FitViewport (double virtualWidth, double virtualHeight, Camera camera) {
		this.virtualWidth = virtualWidth;
		this.virtualHeight = virtualHeight;
		this.camera = camera;
	}

	public void update (double screenWidth, double screenHeight) {
		double ratioX = screenWidth / virtualWidth;
		double ratioY = screenHeight / virtualHeight;
		double ratio = Math.min(ratioX, ratioY);
		this.scale = ratio;
	}

	public Vector2 worldToScreen (double x, double y) {
		double centerX = virtualWidth / 2.0;
		double centerY = virtualHeight / 2.0;

		// to screen (if there was no scaling from the viewport to fit the screen) 
		double screenX = (x - camera.x) * camera.zoom + centerX;
		double screenY = (y - camera.y) * camera.zoom + centerY;

		// scale to fit the screen
		double scaledX = screenX * scale;
		double scaledY = screenY * scale;

		return new Vector2(scaledX, scaledY);
	}

	public double getCameraZoom () {
		return camera.zoom;
	}
}
