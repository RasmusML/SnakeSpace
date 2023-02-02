package snake.mvc.game.model;

import java.util.ArrayList;

import snake.mvc.game.model.entity.Entity;

public class Grid {

	public final int tilesize;

	private ArrayList<Spring> springs;
	private ArrayList<PointMass> points;

	public int width, height;

	public ArrayList<Slot> slots; // used to set random entity position

	private Vector2[][] positions;
	private Vector2[][] positionsCentered;

	public Grid () {
		width = 21;
		height = 21;

		slots = new ArrayList<Slot>();

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				Slot slot = new Slot(x, y);
				slots.add(slot);
			}
		}

		tilesize = 32;

		// setup the springs and point mass from the grid movement
		positionsCentered = new Vector2[width][height];
		for (int x = 0; x < positionsCentered.length; x++) {
			for (int y = 0; y < positionsCentered[x].length; y++) {
				positionsCentered[x][y] = new Vector2();
			}
		}

		positions = new Vector2[width][height];
		for (int x = 0; x < positions.length; x++) {
			for (int y = 0; y < positions[x].length; y++) {
				positions[x][y] = new Vector2();
			}
		}

		springs = new ArrayList<Spring>((width - 1) * (height - 1));
		points = new ArrayList<PointMass>(width * height);

		ArrayList<PointMass> fixedPoints = new ArrayList<PointMass>(width * height);
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				double yy = y * tilesize;
				double xx = x * tilesize;
				points.add(new PointMass(xx, yy, 1f));
				fixedPoints.add(new PointMass(xx, yy, 0f));
			}
		}

		// link the point masses with springs
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				boolean border = x == 0 || y == 0 || x == width - 1 || y == height - 1;
				if (border) {

					// anchor the border to make sure that the pointmasses will return to original position.
					springs.add(new Spring(
						get(fixedPoints, x, y, width),
						get(points, x, y, width),
						.1f,
						.1f)
					);

					// anchor every third to enforce grid structure
				} else if (x % 3 == 0 && y % 3 == 0) {
					springs.add(new Spring(
						get(fixedPoints, x, y, width),
						get(points, x, y, width),
						.002f,
						.02f)
					);
				}

				if (x > 0) {
					springs.add(new Spring(
						get(points, x - 1, y, width),
						get(points, x, y, width),
						.28f,
						.06f)
					);
				}

				if (y > 0) {
					springs.add(new Spring(
						get(points, x, y - 1, width),
						get(points, x, y, width),
						.28f,
						.06f)
					);
				}
			}
		}

		update();
	}

	private <T> T get (ArrayList<T> gridArrayXthenY, int x, int y, int width) {
		int index = y * width + x;
		return gridArrayXthenY.get(index);
	}

	public void update () {
		for (Spring spring : springs) {
			spring.update();
		}

		for (PointMass point : points) {
			point.update();
		}

		// update stored positions
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				Vector2 point = get(points, x, y, width).position;

				// calculate and update centerPosition
				if (x > 0 && y > 0) {
					Vector2 upLeft = point;
					Vector2 upRight = new Vector2(get(points, x - 1, y, width).position);
					Vector2 bottomLeft = new Vector2(get(points, x, y - 1, width).position);
					Vector2 bottomRight = new Vector2(get(points, x - 1, y - 1, width).position);

					double xx = ((.5 * (upLeft.x + upRight.x)) + (.5 * (bottomLeft.x + bottomRight.x))) / 2.0;
					double yy = ((.5 * (upLeft.y + upRight.y)) + (.5 * (bottomLeft.y + bottomRight.y))) / 2.0;

					positionsCentered[x][y].set(xx, yy);
				}

				// update position
				Vector2 position = positions[x][y];
				position.set(point);
			}
		}
	}

	public void applyForce (Snake snake) {
		double radius = 40f;
		double strength = 50f;

		Direction dir = snake.move;

		Slot head = snake.body.getFirst();
		Vector2 outline = toPosition(head);

		double x = outline.x - dir.dx * tilesize / 4.0;
		double y = outline.y - dir.dy * tilesize / 4.0;

		Vector2 force = new Vector2(dir.dx, dir.dy).scl(strength);
		Vector2 position = new Vector2(x, y);

		applyDirectedForce(force, position, radius);
	}

	private void applyDirectedForce (Vector2 force, Vector2 position, double radius) {
		double radius2 = radius * radius;

		for (PointMass p : points) {
			if (position.distanceSquared(p.position) < radius2) {
				p.applyForce(new Vector2(force).scl(1f / (position.distance(p.position))));
			}
		}
	}

	public boolean inside (int x, int y) {
		return 0 <= x && x <= width - 1 && 0 <= y && y <= height - 1;
	}

	public Slot[] getNeighbors (Slot slot) {
		return new Slot[] {
				new Slot(slot.x - 1, slot.y), // left
				new Slot(slot.x + 1, slot.y), // right
				new Slot(slot.x, slot.y - 1), // up
				new Slot(slot.x, slot.y + 1) // down
		};
	}

	/*
	 * translates a gridX/gridY to absolute x/y according to the grid "wobble-ness"
	 */
	public Vector2 toPosition (int x, int y) {
		if (inside(x, y)) return positions[x][y];
		return new Vector2(x * tilesize, y * tilesize); // only gets called once (when snake is out of the grid and therefore dead)
	}

	public Vector2 toPosition (Slot slot) {
		return toPosition(slot.x, slot.y);
	}

	public Vector2 toCenterPosition (int x, int y) {
		if (inside(x, y)) return positionsCentered[x][y];
		throw new IllegalStateException("broken!"); // it is not possible to create a strangle outside the grid, because the snake is dead at that moment.
	}

	public Vector2 toCenterPosition (Slot slot) {
		return toCenterPosition(slot.x, slot.y);
	}

	private final ArrayList<Slot> openSlotsBuffer = new ArrayList<Slot>();

	public ArrayList<Slot> getOpenSlots (Snake snake, ArrayList<Entity> entities) {
		openSlotsBuffer.clear();
		openSlotsBuffer.addAll(slots);
		openSlotsBuffer.removeAll(snake.body);

		// don't spawn in a strangle area, because then they get consumed right away.
		for (StrangleArea area : snake.strangles) {
			openSlotsBuffer.removeAll(area.strangle);
		}

		for (Entity entity : entities) {
			if (!entity.dead) {
				openSlotsBuffer.remove(entity.position);
			}
		}

		return openSlotsBuffer;
	}
}
