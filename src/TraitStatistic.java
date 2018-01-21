import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TraitStatistic {
	private String traitName;
	private List<Double> scores;
	private int lowCount, mediumCount, highCount;
	
	public TraitStatistic(String traitName) {
		this.traitName = traitName;
		scores = new ArrayList<Double>();
		lowCount = mediumCount = highCount = 0;
	}
	
	public String getTraitName() {
		return traitName;
	}
	
	public void addScore(double s) {
		if (s < 3.0) {
			lowCount++;
		} else if (s <= 5.0) {
			mediumCount++;
		} else {
			highCount++;
		}
		scores.add(s);
	}
	
	public int getLowCount() {
		return lowCount;
	}
	
	public int getMediumCount() {
		return mediumCount;
	}
	
	public int getHighCount() {
		return highCount;
	}
	
	public double getMean() {
		double sum = 0.0;
		for (double s : scores) {
			sum += s;
		}
		return sum / getScoreCount();
	}
	
	public double getStandardDeviation() {
		double mean = getMean(), standardDeviation = 0.0;
		for (double s : scores) {
			standardDeviation += Math.pow(s - mean, 2);
		}
		return Math.sqrt(standardDeviation / getScoreCount()); 
	}
	
	public Map<String, Double> getData() {
		Map<String, Double> data = new LinkedHashMap<String, Double>();
		data.put("Low score (< 3)", (double)getLowCount());
		data.put("Medium score (>=3, <=5)", (double)getMediumCount());
		data.put("High score (> 5)", (double)getHighCount());
		data.put("Mean", truncateToTwoDecimalPlaces(getMean()));
		data.put("Standard Deviation", truncateToTwoDecimalPlaces(getStandardDeviation()));
		return data;
	}
	
	private int getScoreCount() {
		return scores.size() != 0 ? scores.size() : 1;
	}
	
	private double truncateToTwoDecimalPlaces(double s) {
		return Math.floor(s * 100) / 100;
	}
}
