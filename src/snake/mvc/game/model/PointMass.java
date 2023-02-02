package snake.mvc.game.model;

public class PointMass {

	public Vector2 position = new Vector2();
	public Vector2 velocity = new Vector2();

	private double inverseMass;

	private Vector2 acceleration = new Vector2();
	private double damping;

	public PointMass (double x, double y, double invMass) {
		position.set(x, y);
		inverseMass = invMass;
		damping = 0.98f;
	}

	public void applyForce (Vector2 force) {
		// f = m * a
		acceleration.add(force.copy().scl(inverseMass));
	}

	public void update () {
		velocity.add(acceleration);
		position.add(velocity);
		acceleration.set(0, 0);

		velocity.scl(damping);
	}
}
