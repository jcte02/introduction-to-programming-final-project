import java.util.ArrayList;
import java.util.List;

public class SearchResult {
	private List<Personality> matches;
	private List<String> nicknames;
	private int perfectMatchIndex;
	
	public SearchResult() {
		matches = new ArrayList<Personality>();
		nicknames = new ArrayList<String>();
		perfectMatchIndex = -1;
	}
	
	public void addMatch(Personality p) {
		matches.add(p);
		nicknames.add(p.getNickname());
	}
	
	public void addPerfectMatch(Personality p) {
		addMatch(p);
		// there can be only one equal nickname since there can be no duplicates
		if(perfectMatchIndex == -1) {
			perfectMatchIndex = matches.size() - 1;
		}
	}
	
	public boolean isEmpty() {
		return matches.size() == 0;
	}
	
	public boolean hasSingleResult() {
		return matches.size() == 1;
	}
	
	public boolean hasPerfectMatch() {
		return perfectMatchIndex != -1;
	}
	
	public int getPerfectMatchIndex() {
		return perfectMatchIndex;
	}
	
	public List<Personality> getMatches() {
		return matches;
	}
	
	public List<String> getNicknames() {
		return nicknames;
	}
	
	public int size() {
		return matches.size();
	}
}
