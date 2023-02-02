package snake.mvc.game.model;

public class Spring {
	private PointMass end1;
	private PointMass end2;
	private double baseLength;
	private double stifness;
	private double damping;

	public Spring (PointMass end1, PointMass end2, double stiffness, double damping) {
		this.end1 = end1;
		this.end2 = end2;
		this.baseLength = end1.position.distance(end2.position);
		this.stifness = stiffness;
		this.damping = damping;
	}

	public void update () {
		Vector2 x = new Vector2(end1.position).sub(end2.position);
		double length = x.length();

		if (length > baseLength) {
			// distance the spring is stretched out
			double delta = length - baseLength;
			x.normalize().scl(delta);

			Vector2 dv = new Vector2(end1.velocity).sub(end2.velocity);
			dv.scl(damping);

			// modified Hooke's law: f = -stiffness*x+damping*dv
			Vector2 force = new Vector2(x).scl(stifness).add(dv);

			end1.applyForce(new Vector2(force).scl(-1));
			end2.applyForce(force);
		}
	}
}
