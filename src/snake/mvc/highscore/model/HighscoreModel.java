package snake.mvc.highscore.model;

import java.util.ArrayList;
import java.util.Collections;

import snake.mvc.Model;
import snake.mvc.Profile;
import snake.mvc.gameover.model.Pair;
import snake.mvc.shared.Util;

public class HighscoreModel extends Model {

	public Profile settings;

	public ArrayList<Pair> leaderboard;

	public HighscoreModel (Profile settings) {
		this.settings = settings;
	}

	@Override public void enter () {
		loadHighscores();
	}

	private void loadHighscores () {
		String highscoresRaw = Util.readFile(Util.highscoreFile);
		String[] highScores = highscoresRaw.split("\\r?\\n");

		// parse the lines into pair-format, to make it easy to sort the highscore list.
		ArrayList<Pair> pairs = new ArrayList<Pair>();

		for (String highscore : highScores) {
			if (highscore.isEmpty()) continue; // sometimes the highscore.stats file contains 1 empty line from the beginning, so ignore that in case.

			String[] split = highscore.split("#");
			if (split.length != 2) continue;
			
			String nameRaw = split[0];
			String scoreRaw = split[1];
			
			if (!Util.isNumeric(scoreRaw)) continue;

			Pair pair = new Pair();
			pair.name = nameRaw;
			pair.score = Integer.parseInt(scoreRaw);
			pairs.add(pair);
		}

		Collections.sort(pairs);

		leaderboard = pairs;
	}
}
