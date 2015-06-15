package rekkyn.farkle;

import java.util.*;
import java.util.Map.Entry;

public class Pattern {
    
    private List<Object> pattern = new ArrayList<Object>();
    private ScoreSet scoreSet;
    
    public Pattern(Object... pattern) {
        if (pattern.length > Farkle.totalDice) throw new IllegalArgumentException("Pattern is too long");
        for (Object o : pattern) {
            if (o instanceof Integer) {
                int i = (Integer) o;
                if (i < 1 || i > 6) throw new IllegalArgumentException("Pattern out of dice range");
            } else if (!(o instanceof String)) throw new IllegalArgumentException("Pattern must be made of integers or strings");
            this.pattern.add(o);
        }
    }
    
    public List<ScoreSet> find(int[] roll) {
        List<List<Integer>> subsets = getAllSubsets(roll, pattern.size());
        List<ScoreSet> scoreSets = new ArrayList<ScoreSet>();
        for (List<Integer> subset : subsets) {
            if (match(subset)) scoreSets.add(new ScoreSet(scoreSet, subset));
        }
        return scoreSets;
    }
    
    private boolean match(List<Integer> subset) {
        Map<Object, Integer> countMap = new HashMap<Object, Integer>();
        for (Object o : pattern) {
            if (countMap.containsKey(o)) {
                countMap.put(o, countMap.get(o) + 1);
            } else {
                countMap.put(o, 1);
            }
        }
        
        Map<Integer, Integer> rollMap = new HashMap<Integer, Integer>();
        for (Integer i : subset) {
            if (rollMap.containsKey(i)) {
                rollMap.put(i, rollMap.get(i) + 1);
            } else {
                rollMap.put(i, 1);
            }
        }
        
        List<Integer> countStrings = new ArrayList<Integer>();
        
        for (Entry<Object, Integer> cursor : countMap.entrySet()) {
            if (cursor.getKey() instanceof Integer) {
                if (rollMap.containsKey(cursor.getKey()) && rollMap.get(cursor.getKey()) >= cursor.getValue()) {
                    rollMap.put((Integer) cursor.getKey(), rollMap.get(cursor.getKey()) - cursor.getValue());
                } else
                    return false;
            } else if (cursor.getKey() instanceof String) {
                countStrings.add(cursor.getValue());
            }
        }
        
        List<Integer> countRollNumbers = new ArrayList<Integer>(rollMap.values());
        
        Collections.sort(countStrings);
        Collections.reverse(countStrings);
        Collections.sort(countRollNumbers);
        Collections.reverse(countRollNumbers);
        
        if (countRollNumbers.size() < countStrings.size()) return false;
        for (int i = 0; i < countStrings.size(); i++) {
            if (countRollNumbers.get(i) < countStrings.get(i)) return false;
        }
        
        return true;
    }
    
    private List<List<Integer>> getAllSubsets(int[] roll, int length) {
        List<Integer> res = new ArrayList<Integer>();
        List<List<Integer>> subsets = new ArrayList<List<Integer>>();
        for (int i = 0; i < length; i++) {
            res.add(0);
        }
        return doCombine(roll, res, 0, 0, length, subsets);
    }
    
    private static List<List<Integer>> doCombine(int[] roll, List<Integer> res, int currIndex, int level, int length,
            List<List<Integer>> subsets) {
        if (level == length) {
            subsets.add(new ArrayList<Integer>(res));
            return subsets;
        }
        for (int i = currIndex; i < roll.length; i++) {
            res.set(level, roll[i]);
            doCombine(roll, res, i + 1, level + 1, length, subsets);
        }
        return subsets;
    }
    
    public void setScoreSet(ScoreSet scoreSet) {
        this.scoreSet = scoreSet;
    }
}