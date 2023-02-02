package snake.mvc.menu.model;

import java.util.LinkedList;

public class RetroSnake {
	public LinkedList<Slot> body = new LinkedList<Slot>();
	public double moveTicks, ticksToMove = .4;	// sec
}
