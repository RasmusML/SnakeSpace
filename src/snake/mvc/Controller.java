package snake.mvc;

public class Controller {
	public void enter() {}	// global keylisteners are binded to the scene, else they wont work.
													// we have to re-bind the keylistener callbacks, when we change scene
													// because one scene per context doesn't work (more info, see View)
}
