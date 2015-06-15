package rekkyn.farkle;

import java.util.ArrayList;
import java.util.List;

public class ScoreSet {
    
    private String name;
    private Pattern pattern;
    private int score;
    
    public ScoreSet(String name, Pattern pattern, int score) {
        this.name = name;
        this.pattern = pattern;
        this.score = score;
    }
    
    public List<ScoreSet> find(int[] roll) {
        List<ScoreSet> list = new ArrayList<ScoreSet>();
        int num = pattern.find(roll);
        for (int i = 0; i < num; i++)
            list.add(this);
        
        return list;
    }
    
    @Override
    public String toString() {
        return name;
    }
}
