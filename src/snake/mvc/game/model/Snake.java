package snake.mvc.game.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

import snake.mvc.game.model.entity.Entity;
import snake.mvc.shared.Util;

public class Snake {

	public LinkedList<Slot> body;
	public Direction direction;
	public Direction move;

	public double moveTicks, ticksToMove;
	public double walkTicksToMove;
	public double runTicksToMove;

	public ArrayList<Consumption> consuming;
	public int growingLeft;

	public ArrayList<StrangleArea> strangles;

	public Snake (Grid grid) {
		direction = Direction.left;
		move = direction;

		walkTicksToMove = .4; // secs	
		runTicksToMove = .2;
		ticksToMove = walkTicksToMove;

		body = new LinkedList<Slot>();
		body.add(new Slot(grid.width / 2, grid.height / 2)); // head
		body.add(new Slot(grid.width / 2 + 1, grid.height / 2)); // tail

		growingLeft = 0;

		strangles = new ArrayList<StrangleArea>();
		consuming = new ArrayList<Consumption>();
	}

	public SnakeMoveResult move (ArrayList<Entity> entities, Grid grid) {
		SnakeMoveResult result = new SnakeMoveResult();
		result.selfCollided = false;
		result.wallCollision = false;
		result.changedDirection = false;
		result.bombHit = false;

		if (this.direction != move) result.changedDirection = true;
		this.direction = move;

		Slot oldHead = body.getFirst();
		Slot newHead = new Slot(oldHead.x + move.dx, oldHead.y + move.dy);

		if (!grid.inside(newHead.x, newHead.y)) result.wallCollision = true;

		int growth = 0;
		for (Entity entity : entities) {
			if (!entity.dead && entity.position.equals(newHead)) {
				entity.dead = true;

				growth += entity.growth;

				switch (entity.type) {
					case bomb: {
						result.bombHit = true;
						break;
					}
				
					case rabbit: {
						break;
					}
					case food: {
						break;
					}
				}
			}
		}

		if (growth > 0) {
			consuming.add(new Consumption(newHead, growth));
		}

		if (growingLeft == 0) {
			body.removeLast();
		} else {
			growingLeft -= 1; // don't remove tail, if snake is growing.
		}

		// we test whether or not the new head position overlaps one of the body-parts in the body-list.
		result.selfCollided = body.contains(newHead);

		// after the "selfCollision test" the newHead is added as the head to the body list.
		body.addFirst(newHead);

		return result;
	}

	public void strangle (ArrayList<Entity> entities, Grid grid) {

		// only keep the "unique" and "updated" strangles.  Strangles can get out-dated, when the snake moves:
		//  1. two separate strangles become one united strangle.
		// 	2. a new strangle gets added, but an old strangle already exists.

		ArrayList<StrangleArea> updated = new ArrayList<StrangleArea>();
		for (Iterator<StrangleArea> it = strangles.iterator(); it.hasNext();) {
			StrangleArea old = (StrangleArea) it.next();

			Slot source = old.strangle.get(0); // take a point inside the flood, doesn't matter which.
			StrangleArea area = fillStrangleArea(grid, source);
			if (area != null && isUnique(area, updated)) updated.add(area);
		}

		strangles.clear(); // clear out-dated strangles

		// find new strangles
		Slot head = body.getFirst();
		Slot[] headNeighbors = grid.getNeighbors(head);

		// 4 x flood fill, using the neighbors of head as source
		for (Slot source : headNeighbors) {
			StrangleArea area = fillStrangleArea(grid, source);
			if (area != null && isUnique(area, updated)) updated.add(area);
		}

		strangles.addAll(updated);

		// consume entities trapped inside strangle
		int growth = 0;
		for (StrangleArea area : strangles) {
			for (Entity entity : entities) {
				if (area.strangle.contains(entity.position) && !entity.dead) { // in theory the grid can have no open slots (snake grown too big). Therefore entities can keep being dead. Do not re-add them to growth of the snake
					growth += entity.growth;
					entity.dead = true;
				}
			}
		}

		if (growth > 0) consuming.add(new Consumption(head, growth)); // add all the growth from inside the strangle areas as a consumption.

	}

	/*
	 * returns true if the candidate has no duplicate(s) in rest.
	 */
	private boolean isUnique (StrangleArea candidate, ArrayList<StrangleArea> rest) {
		Slot slot = candidate.strangle.get(0); // all strangle slots should be unique, if just one of the slots exist in the other, we know that the rest do too, because of the way flood-fill works.
		for (StrangleArea r : rest) {
			if (r.strangle.contains(slot)) return false;
		}
		return true;
	}

	// based on flood-fill algorithm
	private StrangleArea fillStrangleArea (Grid grid, Slot source) {
		if (!grid.inside(source.x, source.y) || body.contains(source)) return null;

		ArrayList<Slot> flood = new ArrayList<Slot>();
		ArrayList<Slot> pending = new ArrayList<Slot>();

		pending.add(source);
		flood.add(source);

		StrangleArea strangle = new StrangleArea();
		strangle.strangle = flood;

		// the biggest area is when the snake makes a square from head to tail
		int side = Util.ceilInt(body.size() / 4.0);
		int biggestArea = side * side;

		while (pending.size() > 0) {
			if (flood.size() > biggestArea) return null; // the strangle area cannot be larger than the maximum region the snake can make, therefore no strangling in that case!

			Slot current = pending.remove(0);
			Slot[] neighbors = grid.getNeighbors(current);
			for (Slot neighbor : neighbors) {
				if (!grid.inside(neighbor.x, neighbor.y)) return null;

				if (!body.contains(neighbor) && !flood.contains(neighbor)) {
					pending.add(neighbor);
					flood.add(neighbor);
				}
			}
		}

		return strangle;
	}

	public void digest () {
		// after a new move, check if any consumption is on the tail, if so remove consumption and grow snake.
		for (Iterator<Consumption> it = consuming.iterator(); it.hasNext();) {
			Consumption consumption = (Consumption) it.next();

			Slot tail = body.getLast();
			if (consumption.position.equals(tail)) {
				growingLeft += consumption.growthAmount;
				it.remove();
			} else if (!body.contains(consumption.position)) { // if we have been shorten (gone into bomb), then remove the consumptions on the removed part of the snake body.
				it.remove();
			}
		}
	}

	public void shorten (int request) {
		int newSize = body.size() - request;
		int minimumSnakeLength = 2;
		int amount = Math.max(newSize, minimumSnakeLength);

		while (body.size() > amount) {
			body.removeLast();
		}
	}
}
