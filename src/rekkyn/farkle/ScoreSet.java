package rekkyn.farkle;

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
    
    @Override
    public String toString() {
        String resultString = "";
        if (result != null) resultString += result.toString();
        return name + resultString;
    }
}
