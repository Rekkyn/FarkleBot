package rekkyn.farkle;

import java.util.*;

public class Pattern {
    
    private Set<Object> pattern = new HashSet<Object>();
    
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
    
    public int find(int[] roll) {
        List<Set<Integer>> subsets = getAllSubsets(roll, pattern.size());
        int num = 0;
        for (Set<Integer> subset : subsets) {
            if (match(subset)) num++;
        }
        return num;
    }
    
    private boolean match(Set<Integer> subset) {
        return subset.equals(pattern);
    }
    
    private List<Set<Integer>> getAllSubsets(int[] roll, int length) {
        List<Integer> res = new ArrayList<Integer>();
        List<Set<Integer>> subsets = new ArrayList<Set<Integer>>();
        for (int i = 0; i < length; i++) {
            res.add(0);
        }
        return doCombine(roll, res, 0, 0, length, subsets);
    }
    
    private static List<Set<Integer>> doCombine(int[] roll, List<Integer> res, int currIndex, int level, int length,
            List<Set<Integer>> subsets) {
        if (level == length) {
            subsets.add(new HashSet<Integer>(res));
            return subsets;
        }
        for (int i = currIndex; i < roll.length; i++) {
            res.set(level, roll[i]);
            doCombine(roll, res, i + 1, level + 1, length, subsets);
        }
        return subsets;
    }
}