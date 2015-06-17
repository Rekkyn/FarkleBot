package rekkyn.farkle;

import java.util.*;
import java.util.Map.Entry;

public class Farkle {
    
    private Random rand = new Random();
    /** The total number of dice in each round */
    public static final int totalDice = 6;
    /** All possible <code>ScoreSet</code>s in the rules of the game */
    public static Set<ScoreSet> scoreSets = new HashSet<ScoreSet>();
    private static List<Map<List<Integer>, Integer>> rollChances = getRollChances();
    private static List<Map<Float, Float>> averageScores = new ArrayList<Map<Float, Float>>();
    
    private static final boolean DEBUG = false;
    private String indent = "";
    private boolean shouldRoll = false;
    private static final int MAXREROLLS = 2;
    
    public static void main(String[] args) {
        Farkle main = new Farkle();
        main.run();
    }
    
    /** Adds all of the <code>ScoreSet</code>s in the rules */
    public Farkle() {
        scoreSets.add(new ScoreSet("1", new Pattern(1), 100));
        scoreSets.add(new ScoreSet("5", new Pattern(5), 50));
        scoreSets.add(new ScoreSet("3 of a kind", new Pattern("X", "X", "X"), 300));
        scoreSets.add(new ScoreSet("4 of a kind", new Pattern("X", "X", "X", "X"), 1000));
        scoreSets.add(new ScoreSet("5 of a kind", new Pattern("X", "X", "X", "X", "X"), 2000));
        scoreSets.add(new ScoreSet("6 of a kind", new Pattern("X", "X", "X", "X", "X", "X"), 3000));
        scoreSets.add(new ScoreSet("Straight", new Pattern(1, 2, 3, 4, 5, 6), 1500));
        scoreSets.add(new ScoreSet("Three pairs", new Pattern("X", "X", "Y", "Y", "Z", "Z"), 1500));
        scoreSets.add(new ScoreSet("Two triplets", new Pattern("X", "X", "X", "Y", "Y", "Y"), 2500));
        for (int i = 0; i < totalDice; i++)
            averageScores.add(new HashMap<Float, Float>());
    }
    
    public void run() {
        float score = 0;
        Dice dice = new Dice(totalDice);
        List<List<ScoreSet>> combinations = new ArrayList<List<ScoreSet>>(dice.getScoreSetCombinations());
        Collections.sort(combinations, new Comparator<List<ScoreSet>>() {
            
            public int compare(List<ScoreSet> l1, List<ScoreSet> l2) {
                int score1 = ScoreSet.getScoreFromList(l1);
                int score2 = ScoreSet.getScoreFromList(l2);
                if (score1 < score2)
                    return -1;
                else if (score1 == score2)
                    return 0;
                else if (score1 > score2) return 1;
                return 0;
            }
        });
        Collections.reverse(combinations);
        pruneOptions(combinations);
        
        System.out.println("Score: " + score);
        System.out.println("Roll: " + dice);
        for (List<ScoreSet> list : combinations) {
            System.out.println(list + " " + ScoreSet.getScoreFromList(list));
        }
        System.out.println("------------------------");
        System.out.println(getBestOption(combinations, score, dice.getRollLength()));
        System.out.println(shouldRoll ? "Roll again!" : "Stop");
    }
    
    /** @return a list of mappings of how often each roll occurs in a number of
     *         dice that equals the index of the list + 1. The total number of
     *         rolls is mapped from the empty list. */
    private static List<Map<List<Integer>, Integer>> getRollChances() {
        List<Map<List<Integer>, Integer>> mapByNum = new ArrayList<Map<List<Integer>, Integer>>(6);
        for (int i = 1; i <= 6; i++) {
            Map<List<Integer>, Integer> map = n_for(0, new int[0], i, new HashMap<List<Integer>, Integer>());
            mapByNum.add(map);
        }
        return mapByNum;
    }
    
    /** A recursive function for the <code>getRollChances</code> method
     * 
     * @return a mapping of how often each roll occurs in a number of dice equal
     *         to <code>diceNum</code>
     * @see #getRollChances() */
    private static Map<List<Integer>, Integer> n_for(int level, int[] indices, int diceNum, Map<List<Integer>, Integer> map) {
        if (level == diceNum) {
            List<Integer> roll = new ArrayList<Integer>();
            for (int i : indices) {
                roll.add(i);
            }
            Collections.sort(roll);
            if (map.containsKey(roll)) {
                map.put(new ArrayList<Integer>(roll), map.get(roll) + 1);
            } else {
                map.put(new ArrayList<Integer>(roll), 1);
            }
            ArrayList<Integer> blankList = new ArrayList<Integer>();
            if (map.containsKey(blankList)) {
                map.put(blankList, map.get(blankList) + 1);
            } else {
                map.put(blankList, 1);
            }
            return map;
        } else {
            int newLevel = level + 1;
            int[] newIndices = new int[newLevel];
            System.arraycopy(indices, 0, newIndices, 0, level);
            newIndices[level] = 1;
            while (newIndices[level] <= 6) {
                n_for(newLevel, newIndices, diceNum, map);
                ++newIndices[level];
            }
        }
        return map;
    }
    
    /** Removes all lists of <code>ScoreSet</code>s from the given list that have
     * the same number of dice and fewer points than another list of
     * <code>ScoreSet</code>s, and removes all lists of <code>ScoreSet</code>s
     * that have too few points to be as worth as much as larger rolls
     * 
     * @param options */
    private void pruneOptions(List<List<ScoreSet>> options) {
        List<Integer> indexesToRemove = new ArrayList<Integer>();
        for (int i = 0; i < options.size(); i++) {
            for (int j = i + 1; j < options.size(); j++) {
                if (ScoreSet.getTotalDice(options.get(i)) == ScoreSet.getTotalDice(options.get(j))) {
                    if (ScoreSet.getScoreFromList(options.get(i)) > ScoreSet.getScoreFromList(options.get(j))) {
                        if (!indexesToRemove.contains(j)) indexesToRemove.add(j);
                    } else {
                        if (!indexesToRemove.contains(i)) indexesToRemove.add(i);
                    }
                } /*else if (ScoreSet.getTotalDice(options.get(i)) > ScoreSet.getTotalDice(options.get(j))) {
                    int difference = ScoreSet.getTotalDice(options.get(i)) - ScoreSet.getTotalDice(options.get(j));
                    if (ScoreSet.getScoreFromList(options.get(i)) > ScoreSet.getScoreFromList(options.get(j)) + maxScore(difference)
                            && !indexesToRemove.contains(j)) indexesToRemove.add(j);
                  } else if (ScoreSet.getTotalDice(options.get(j)) > ScoreSet.getTotalDice(options.get(i))) {
                    int difference = ScoreSet.getTotalDice(options.get(j)) - ScoreSet.getTotalDice(options.get(i));
                    if (ScoreSet.getScoreFromList(options.get(j)) > ScoreSet.getScoreFromList(options.get(i)) + maxScore(difference)
                            && !indexesToRemove.contains(i)) indexesToRemove.add(i);
                  }*/
            }
        }
        Collections.sort(indexesToRemove, Collections.reverseOrder());
        for (int i : indexesToRemove) {
            options.remove(i);
        }
    }
    
    /** @return the maximum possible score from <code>n</code> dice */
    protected int maxScore(int n) {
        if (n == 0) return 0;
        int max = 0;
        for (ScoreSet s : scoreSets) {
            if (s.getPattern().getLength() > n) continue;
            if (s.getMaxScore() + maxScore(n - s.getPattern().getLength()) > max)
                max = s.getMaxScore() + maxScore(n - s.getPattern().getLength());;
        }
        return max;
    }
    
    private List<ScoreSet> getBestOption(List<List<ScoreSet>> options, float score, int diceNum) {
        return getBestOption(options, score, diceNum, 0);
    }
    
    /** @param options the list of options to choose from
     * @param score the current score
     * @param diceNum the number of dice in play
     * @return the list of <code>ScoreSet</code>s in the given list that has the
     *         highest average yield */
    private List<ScoreSet> getBestOption(List<List<ScoreSet>> options, float score, int diceNum, int rerolls) {
        if (DEBUG) {
            System.out.println(indent + "~Getting the best option~");
            System.out.println(indent + options);
        }
        pruneOptions(options);
        float topScore = score;
        boolean shouldRoll = false;
        List<ScoreSet> bestOption = new ArrayList<ScoreSet>();
        for (List<ScoreSet> option : options) {
            float newScore = score + ScoreSet.getScoreFromList(option);
            float aveScore = getAverageScore(newScore, diceNum - ScoreSet.getTotalDice(option), rerolls);
            if (DEBUG) {
                indent();
                System.out.println(indent + "Option: " + option);
                System.out.println(indent + "getAverageScore " + newScore + " " + (diceNum - ScoreSet.getTotalDice(option)));
                System.out.println(indent + aveScore);
                unindent();
            }
            boolean reroll = true;
            if (newScore > aveScore) {
                aveScore = newScore;
                reroll = false;
            }
            if (aveScore > topScore) {
                topScore = aveScore;
                bestOption = option;
                shouldRoll = reroll; // TODO: this.
            }
        }
        this.shouldRoll = shouldRoll;
        return bestOption;
    }
    
    protected float getAverageScore(float score, int diceLeft, int rerolls) {
        if (diceLeft == 0) {
            if (++rerolls <= MAXREROLLS) {
                diceLeft = totalDice;
            } else
                return score;
        }
        if (averageScores.get(diceLeft - 1).containsKey(score)) {
            return averageScores.get(diceLeft - 1).get(score);
        }
        float averageScore = _getAverageScore(score, diceLeft, rerolls);
        averageScores.get(diceLeft - 1).put(score, averageScore);
        return averageScore;
    }
    
    /** @param score the score to add to
     * @param diceLeft how many dice are being rolled
     * @return the average score given by a number of dice */
    private float _getAverageScore(float score, int diceLeft, int rerolls) {
        if (DEBUG) System.out.println(indent + "Get Average Score(" + score + " " + diceLeft + ")");
        float average = 0;
        int n = 0;
        Map<List<Integer>, Integer> rollChancesForDice = rollChances.get(diceLeft - 1);
        for (Entry<List<Integer>, Integer> entry : rollChancesForDice.entrySet()) {
            if (DEBUG) {
                indent();
                System.out.println(indent + "Possible Roll : " + entry.getKey() + " " + entry.getValue());
                indent();
            }
            if (entry.getKey().equals(new ArrayList<Integer>())) {
                if (DEBUG) {
                    System.out.println(indent + "This is the total number of rolls");
                    unindent();
                    System.out.println(indent + "End Possible Roll: " + entry.getKey() + " " + entry.getValue() + "\n" + indent);
                    unindent();
                }
                continue;
            }
            Dice d = new Dice(entry.getKey());
            List<List<ScoreSet>> options = new ArrayList<List<ScoreSet>>(d.getScoreSetCombinations());
            if (DEBUG) System.out.println(indent + "options: " + options);
            float newScore = 0;
            if (options.size() != 0) {
                if (DEBUG) indent();
                List<ScoreSet> bestOption = getBestOption(options, score, diceLeft, rerolls);
                if (DEBUG) {
                    unindent();
                    System.out.println(indent + "Best Option:" + bestOption);
                }
                newScore = score + ScoreSet.getScoreFromList(bestOption);
                if (DEBUG) indent();
                float newAveScore = getAverageScore(newScore, diceLeft - ScoreSet.getTotalDice(bestOption), rerolls);
                if (DEBUG) unindent();
                if (newAveScore > newScore) newScore = newAveScore;
            }
            if (DEBUG) {
                System.out.println(indent + "Score: " + score);
                System.out.println(indent + "Old Average: " + average + ", total: " + n);
                System.out.println(indent + "New Score: " + newScore);
            }
            for (int i = 0; i < entry.getValue(); i++) {
                if (n == 0) {
                    average = newScore;
                    n++;
                } else {
                    n++;
                    average += (newScore - average) / n;
                }
            }
            if (DEBUG) {
                System.out.println(indent + "Average so far: " + average + ", total: " + n);
                unindent();
                System.out.println(indent + "End Possible Roll: " + entry.getKey() + " " + entry.getValue() + "\n" + indent);
                unindent();
            }
        }
        if (DEBUG) System.out.println(indent + "Return Average(" + score + " " + diceLeft + "): " + average);
        return average;
    }
    
    private void indent() {
        indent += "   |";
    }
    
    private void unindent() {
        indent = indent.substring(4);
    }
    
    public class Dice {
        
        private int diceNum;
        private int[] roll = new int[totalDice];
        
        /** Creates a new set of dice and rolls them
         * 
         * @param diceNum the number of dice */
        public Dice(int diceNum) {
            this.diceNum = diceNum;
            roll();
        }
        
        /** Creates a predefined set of dice */
        public Dice(int... ints) {
            roll = ints;
        }
        
        public Dice(List<Integer> roll) {
            if (roll.size() > totalDice) throw new IllegalArgumentException("Dice roll can't be greater than the total number of dice");
            for (int i = 0; i < roll.size(); i++) {
                this.roll[i] = roll.get(i);
            }
        }
        
        /** Rolls <code>diceNum</code> number of dice and stores the result in
         * <code>roll</code>
         * 
         * @return <code>roll</code> */
        public int[] roll() {
            for (int i = 0; i < totalDice; i++) {
                roll[i] = i < diceNum ? rand.nextInt(6) + 1 : 0;
            }
            return roll;
        }
        
        /** @return the set of all possible combinations of <code>ScoreSet</code>
         *         in the current roll */
        public Set<List<ScoreSet>> getScoreSetCombinations() {
            Set<List<ScoreSet>> combinations = new HashSet<List<ScoreSet>>();
            combinations.add(new ArrayList<ScoreSet>());
            
            // for every item in the original list
            for (ScoreSet item : findScoreSets()) {
                Set<List<ScoreSet>> newCombinations = new HashSet<List<ScoreSet>>();
                
                for (List<ScoreSet> subset : combinations) {
                    // copy all of the current powerset's subsets
                    newCombinations.add(subset);
                    
                    // plus the subsets appended with the current item
                    List<ScoreSet> newSubset = new ArrayList<ScoreSet>(subset);
                    
                    List<Integer> rollList = new ArrayList<Integer>();
                    boolean canAdd = true;
                    for (int i : roll)
                        rollList.add(i);
                    
                    // removes ScoreSets in the current subset from the rollList
                    for (ScoreSet ss : subset) {
                        for (int i : ss.getResult()) {
                            if (rollList.contains(i)) {
                                rollList.remove((Object) i);
                            }
                        }
                    }
                    // checks if the current item can be added to the subset
                    for (int i : item.getResult()) {
                        if (!rollList.contains(i)) {
                            canAdd = false;
                            break;
                        }
                        rollList.remove((Object) i);
                    }
                    
                    if (canAdd) newSubset.add(item);
                    newCombinations.add(newSubset);
                }
                
                // powerset is now powerset of list.subList(0,
                // list.indexOf(item)+1)
                combinations = newCombinations;
            }
            combinations.remove(new ArrayList<ScoreSet>());
            return combinations;
        }
        
        /** @return every single <code>ScoreSet</code> that can be found in the
         *         current roll */
        public List<ScoreSet> findScoreSets() {
            List<ScoreSet> foundScoreSets = new ArrayList<ScoreSet>();
            
            // removes all 0's from the roll array
            int n = 0;
            for (int i : roll)
                if (i != 0) n++;
            int[] cleanRoll = new int[n];
            int j = 0;
            for (int i : roll)
                if (i != 0) cleanRoll[j++] = i;
            
            // checks the roll against every ScoreSet in the rules
            for (ScoreSet scoreSet : scoreSets) {
                foundScoreSets.addAll(scoreSet.find(cleanRoll));
            }
            return foundScoreSets;
        }
        
        public void setDiceNum(int diceNum) {
            this.diceNum = diceNum;
        }
        
        public int getRollLength() {
            return roll.length;
        }
        
        @Override
        public String toString() {
            String s = "";
            for (int i = 0; i < roll.length; i++) {
                if (roll[i] == 0) continue;
                if (i == 0) {
                    s += roll[i];
                } else {
                    s += " " + roll[i];
                }
            }
            return s;
        }
    }
}
