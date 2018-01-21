import java.util.HashMap;
import java.util.Map;

public class PopulationStatistic {
	private Map<String, TraitStatistic> traitsStats;
	
	public PopulationStatistic() {
		traitsStats = new HashMap<String, TraitStatistic>();
		for(String t : Personality.getTraits()) {
			traitsStats.put(t, new TraitStatistic(t));
		}
	}
	
	public void addUser(Personality p) {
		for(Map.Entry<String,Double> s : p.getScoresAnnotated().entrySet()) {
			traitsStats.get(s.getKey()).addScore(s.getValue());
		}
	}
	
	public Map<String, TraitStatistic> getTraitsStatistics() {
		return traitsStats;
	}
}
