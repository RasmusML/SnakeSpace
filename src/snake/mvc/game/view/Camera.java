package snake.mvc.game.view;

public class Camera {

	public double x, y;
	public double zoom;

	private double tx, ty;
	private double movespeed;

	private double targetZoom;
	public double zoomSpeed;

	public Camera () {
		movespeed = 0.016;

		zoom = 1;
		targetZoom = 1;
		zoomSpeed = 0.007;
	}

	public void zoom (double amount) {
		targetZoom += amount;
	}

	public void lookAt (double x, double y) {
		this.x = x;
		this.y = y;
		this.tx = x;
		this.ty = y;
	}

	public void moveTowards (double x, double y) {
		this.tx = x;
		this.ty = y;
	}

	public void update () {
		double dx = tx - x;
		double dy = ty - y;

		x += dx * movespeed;
		y += dy * movespeed;

		double dzoom = targetZoom - zoom;
		zoom += dzoom * zoomSpeed;

	}

	public void setZoom (double zoom) {
		this.zoom = zoom;
		this.targetZoom = zoom;
	}
}
