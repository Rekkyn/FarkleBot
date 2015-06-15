package rekkyn.farkle;

import java.util.Collections;
import java.util.List;

public class ScoreSet {
    
    private String name;
    private Pattern pattern;
    private int score;
    private List<Integer> result;
    
    public ScoreSet(String name, Pattern pattern, int score) {
        this.name = name;
        this.pattern = pattern;
        pattern.setScoreSet(this);
        this.score = score;
    }
    
    public ScoreSet(ScoreSet base, List<Integer> result) {
        name = base.name;
        pattern = base.pattern;
        score = base.score;
        this.result = result;
    }
    
    public List<ScoreSet> find(int[] roll) {
        return pattern.find(roll);
    }
    
    public int getScore() {
        if (name != "3 of a kind") {
            return score;
        } else {
            int i = result.get(0);
            return 100 * (i == 1 ? 3 : i);
        }
    }
    
    public List<Integer> getResult() {
        return result;
    }
    
    @Override
    public String toString() {
        String resultString = "";
        if (result != null) {
            Collections.sort(result);
            resultString += result.toString();
        }
        return name + resultString;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (name == null ? 0 : name.hashCode());
        result = prime * result + (pattern == null ? 0 : pattern.hashCode());
        result = prime * result + (this.result == null ? 0 : this.result.hashCode());
        result = prime * result + score;
        return result;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        ScoreSet other = (ScoreSet) obj;
        if (name == null) {
            if (other.name != null) return false;
        } else if (!name.equals(other.name)) return false;
        if (pattern == null) {
            if (other.pattern != null) return false;
        } else if (!pattern.equals(other.pattern)) return false;
        if (result == null) {
            if (other.result != null) return false;
        } else if (!result.equals(other.result)) return false;
        if (score != other.score) return false;
        return true;
    }
}
