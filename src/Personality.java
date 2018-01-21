import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Personality {
	private String nickname;
	private double extraversion, agreeableness, conscientiousness, 
	               emotionalStability, opennessToExperiences;
	private static Pattern sscanfPattern = Pattern.compile("([\\w\\d]+);(\\d.\\d);(\\d.\\d);(\\d.\\d);(\\d.\\d);(\\d.\\d)\\s*");
	
	private static String[] traits = {
			"Extraversion", "Agreeableness", "Conscientiousness", 
            "Emotional Stability", "Openness to Experiences"
	};
	
	public Personality(String nickname, double extraversion, double agreeableness, 
			double conscientiousness, double emotionalStability, double opennessToExperiences) {
		this.nickname = nickname;
		this.extraversion = extraversion;
		this.agreeableness= agreeableness;
		this.conscientiousness= conscientiousness;
		this.emotionalStability = emotionalStability;
		this.opennessToExperiences = opennessToExperiences;
	}
	
	private Personality(double[] scores) {
		this.extraversion = scores[0];
		this.agreeableness= scores[1];
		this.conscientiousness= scores[2];
		this.emotionalStability = scores[3];
		this.opennessToExperiences = scores[4];
	}
	
	public String getNickname() {
		return nickname;
	}
	
	public Personality setNickname(String nickname) {
		this.nickname = nickname;
		return this;
	}
	
	public double getExtraversion() {
	    return extraversion;
	}
	
	public double getAgreeableness() {
	    return agreeableness;
	}
	
	public double getConscientiousness() {
	    return  conscientiousness;
	}
	
	public double getEmotionalStability() {
	    return emotionalStability;
	}
	
	public double getOpennessToExperiences() {
	    return opennessToExperiences;
	}
	
	public double[] getScores() {
		return new double[] {
				getExtraversion(),
				getAgreeableness(),
				getConscientiousness(),
				getEmotionalStability(),
				getOpennessToExperiences()
		};
	}
	
	public Map<String, Double> getScoresAnnotated() {
		Map<String, Double> scores = new HashMap<String, Double>();
		double[] s = getScores();
		for(int i = 0; i < s.length; i++) {
			scores.put(traits[i], s[i]);
		}
		return scores;
	}
	
	public static String[] getTraits() {
		return traits;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(nickname);
		for(double d : getScores()) {
			sb.append(";" + Double.toString(d));
		}
		return sb.toString();
	}
	
	public static Personality fromString(String str) {
		// poor man's sscanf: regex
		Matcher m = sscanfPattern.matcher(str);
		if(m.matches()) {
			try {
				return new Personality(m.group(1), Double.parseDouble(m.group(2)), 
						Double.parseDouble(m.group(3)), Double.parseDouble(m.group(4)), 
						Double.parseDouble(m.group(5)), Double.parseDouble(m.group(6)));
			} catch (NumberFormatException ex) {
			}
		}
		return null;
	}
	
	public static Personality fromTestAnswers(int[] answers) {
		if (answers.length != 10) {
			return null;
		}
		
		double[] scores = new double[5];
		for(int i = 0; i < 5; i++) {
			scores[i] = (answers[i] + reverseScore(answers[i+5])) / 2.0;
		}
		
		return new Personality(scores);
	}
	
	private static int reverseScore(int score) {
		return 8 - score;
	}
	
	public static boolean isNicknameValid(String nickname) {
		for(char c : nickname.toCharArray()) {
			if(!Character.isLetterOrDigit(c)) {
				return false;
			}
		}
		return true;
	}
}
