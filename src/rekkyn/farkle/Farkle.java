package rekkyn.farkle;

import java.util.*;

public class Farkle {
    
    private Random rand = new Random();
    /** The total number of dice in each round */
    public static final int totalDice = 6;
    /** All possible <code>ScoreSet</code>s in the rules of the game */
    public static Set<ScoreSet> scoreSets = new HashSet<ScoreSet>();
    
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
        scoreSets.add(new ScoreSet("5 of a kind", new Pattern("X", "X", "X", "X", "X", "X"), 3000));
        scoreSets.add(new ScoreSet("Straight", new Pattern(1, 2, 3, 4, 5, 6), 1500));
        scoreSets.add(new ScoreSet("Three pairs", new Pattern("X", "X", "Y", "Y", "Z", "Z"), 1500));
        scoreSets.add(new ScoreSet("Two triplets", new Pattern("X", "X", "X", "Y", "Y", "Y"), 2500));
    }
    
    public void run() {
        Dice dice = new Dice(totalDice);
        System.out.println(dice);
        List<List<ScoreSet>> combinations = new ArrayList<List<ScoreSet>>(dice.getScoreSetCombinations());
        Collections.sort(combinations, new Comparator<List<ScoreSet>>() {
            
            public int compare(List<ScoreSet> l1, List<ScoreSet> l2) {
                int score1 = 0;
                for (ScoreSet s : l1) {
                    score1 += s.getScore();
                }
                int score2 = 0;
                for (ScoreSet s : l2) {
                    score2 += s.getScore();
                }
                if (score1 < score2)
                    return -1;
                else if (score1 == score2)
                    return 0;
                else if (score1 > score2) return 1;
                return 0;
            }
        });
        Collections.reverse(combinations);
        for (List<ScoreSet> list : combinations) {
            int score = 0;
            for (ScoreSet s : list) {
                score += s.getScore();
            }
            System.out.println(list + " " + score);
        }
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
        public Dice(int a, int b, int c, int d, int e, int f) {
            roll[0] = a;
            roll[1] = b;
            roll[2] = c;
            roll[3] = d;
            roll[4] = e;
            roll[5] = f;
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
