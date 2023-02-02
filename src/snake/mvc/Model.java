package snake.mvc;

public class Model {

	public ContextManager manager;

	public void setup (ContextManager manager) {
		this.manager = manager;
	}

	public void create () {}

	public void enter () {}

	public void update (double dt) {}
}
