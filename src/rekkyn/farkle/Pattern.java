package rekkyn.farkle;

import java.util.*;
import java.util.Map.Entry;

/** A pattern of dice */
public class Pattern {
    
    private List<Object> pattern = new ArrayList<Object>();
    private ScoreSet scoreSet;
    
    /** Creates a new <code>Pattern</code>
     * 
     * @param pattern the numbers or <code>String</code>s making up the pattern.
     *            Use numbers to indicate a specific number needed, and use
     *            <code>String</code>s to represent a variable. Variables can be
     *            repeated using the same <code>String</code> to indicate that
     *            these variables need to be the same number. */
    public Pattern(Object... pattern) {
        for (Object o : pattern) {
            if (o instanceof Integer) {
                int i = (Integer) o;
                if (i < 1 || i > 6) throw new IllegalArgumentException("Pattern out of dice range");
            } else if (!(o instanceof String)) throw new IllegalArgumentException("Pattern must be made of integers or strings");
            this.pattern.add(o);
        }
    }
    
    /** @param roll
     * @return a list of <code>ScoreSet</code>s of the associated
     *         <code>ScoreSet</code> type in the given roll */
    public List<ScoreSet> find(int[] roll) {
        List<ScoreSet> scoreSets = new ArrayList<ScoreSet>();
        
        List<List<Integer>> subsets = getAllSubsets(roll, pattern.size());
        for (List<Integer> subset : subsets) {
            if (match(subset)) scoreSets.add(new ScoreSet(scoreSet, subset));
        }
        return scoreSets;
    }
    
    /** @param subset a subset of a roll
     * @return true if the <code>subset</code> contains this
     *         <code>Pattern</code> */
    private boolean match(List<Integer> subset) {
        // puts the pattern and the roll into their own Maps so that each
        // element is counted
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
        
        // checks if the roll has a given number and removes that number if it
        // does
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
        
        // checks if the roll has numbers left that can fill the variables
        if (countRollNumbers.size() < countStrings.size()) return false;
        for (int i = 0; i < countStrings.size(); i++) {
            if (countRollNumbers.get(i) < countStrings.get(i)) return false;
        }
        
        return true;
    }
    
    /** @param roll
     * @param length
     * @return all subsets with the given <code>length</code> from the given
     *         <code>roll</code> */
    private List<List<Integer>> getAllSubsets(int[] roll, int length) {
        List<Integer> res = new ArrayList<Integer>();
        List<List<Integer>> subsets = new ArrayList<List<Integer>>();
        for (int i = 0; i < length; i++) {
            res.add(0);
        }
        return doCombine(roll, res, 0, 0, length, subsets);
    }
    
    /** A recursive function for the <code>getAllSubsets</code> method
     * 
     * @see #getAllSubsets(int[] roll, int length) */
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
    
    public int getLength() {
        return pattern.size();
    }
}