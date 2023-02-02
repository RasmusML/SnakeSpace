package snake.mvc.gameover.model;

import java.util.ArrayList;
import java.util.Collections;

import snake.mvc.Model;
import snake.mvc.Profile;
import snake.mvc.shared.Util;

public class GameOverModel extends Model {

	public Profile userProfile;
	public int placement;

	public GameOverModel (Profile userProfile) {
		this.userProfile = userProfile;
	}

	@Override public void create () {}

	@Override public void enter () {
		saveScore();
	}

	private void saveScore () {
		// update name if empty
		String user = userProfile.name;
		user = user.replaceAll("\\s+", ""); // no white-spaces regex: \s+ (+ = 1 or more, \s = whitespaces)
		if (user.isEmpty()) user = "unknown";
		userProfile.name = user;

		Pair newPair = new Pair();
		newPair.name = userProfile.name;
		newPair.score = userProfile.score;

		// parse the lines into pair-format, to make it easy to sort the highscore list.
		ArrayList<Pair> pairs = new ArrayList<Pair>();
		pairs.add(newPair);

		String highscoresRaw = Util.readFile(Util.highscoreFile);
		String[] highScores = highscoresRaw.split("\\r?\\n");

		for (String highscore : highScores) {
			if (highscore.isEmpty()) continue; // corrupted file, sometimes the highscore file contains 1 empty line from the beginning, so ignore that in case.
			String[] split = highscore.split("#");

			if (split.length != 2) continue;

			String nameRaw = split[0];
			String scoreRaw = split[1];

			if (!Util.isNumeric(scoreRaw)) continue; // corrupted file, score is not a number (can happen if, someone tries to manually change the content of the file (cheating!))

			Pair pair = new Pair();
			pair.name = nameRaw;
			pair.score = Integer.parseInt(scoreRaw);
			pairs.add(pair);
		}

		// sort highscores from highest to lowest
		Collections.sort(pairs);

		// get placement
		int index = pairs.indexOf(newPair);
		placement = index + 1;

		// reformat to file format (now with the new pair (just played game)).
		StringBuilder ordered = new StringBuilder();
		for (Pair pair : pairs) {
			ordered.append(pair.name + "#" + pair.score + "\n");
		}

		Util.writeFile(ordered.toString(), Util.highscoreFile);
	}
}
