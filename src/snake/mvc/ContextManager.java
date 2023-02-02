package snake.mvc;

import java.util.HashMap;

import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;

public class ContextManager {

	private HashMap<ContextType, MVCContext> lookup;
	private MVCContext active;
	private Scene scene;
	private Stage stage;

	private FadeTransition screenFader;
	private double fadeDuration;

	private ContextType nextScreen;

	public ContextManager (Scene scene, Stage stage) {
		this.scene = scene;
		this.stage = stage;

		lookup = new HashMap<ContextType, MVCContext>();

		fadeDuration = 1; // sec
		screenFader = new FadeTransition(Duration.seconds(fadeDuration));

		// these should be called in every MVC-view, so we make the listeners from the manager.
		// (it is not mandatory to use resize() from the individual MVC context)
		scene.widthProperty().addListener( (obs, oldVal, newWidth) -> {
			active.view.resize(newWidth.intValue(), (int) scene.getHeight());
		});

		scene.heightProperty().addListener( (obs, oldVal, newHeight) -> {
			active.view.resize((int) scene.getWidth(), newHeight.intValue());
		});

		// the screens fades, when it is done. The context gets changed.
		screenFader.setOnFinished(new EventHandler<ActionEvent>() {
			@Override public void handle (ActionEvent event) {
				setContext(nextScreen);
			}
		});

	}

	public void createMVC (ContextType type, Model model, View view, Controller controller) {
		MVCContext mvc = new MVCContext();
		mvc.model = model;
		mvc.view = view;
		mvc.controller = controller;

		model.setup(this);

		model.create();
		view.create();

		lookup.put(type, mvc);
	}

	// don't fade on the first context set.
	public void setActiveContext (ContextType context) {
		setContext(context);
	}

	private void setContext (ContextType screen) {
		active = lookup.get(screen);
		active.view.root.setOpacity(1); // we make sure that the opacity is 1, which it wouldn't be if it has faded.

		screenFader.setNode(active.view.root);

		scene.setOnKeyPressed(null);
		scene.setOnKeyReleased(null);
		scene.setOnMouseClicked(null);

		scene.setRoot(active.view.root);

		active.model.enter();
		active.view.enter();
		active.controller.enter();

		// width/height is always whole numbers, so casting them to an int will not change the precision.
		active.view.resize((int) scene.getWidth(), (int) scene.getHeight());
	}

	public void changeActiveContext (ContextType context) {
		nextScreen = context;

		screenFader.setFromValue(1.0);
		screenFader.setToValue(0.0);
		screenFader.play();
	}

	public MVCContext getActive () {
		return active;
	}

	public void setFullscreen (boolean fullscreen) {
		stage.setFullScreen(fullscreen);
	}
}
