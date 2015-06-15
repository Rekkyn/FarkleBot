package rekkyn.farkle;

import java.util.*;

public class Farkle {
    
    private Random rand = new Random();
    public static final int totalDice = 6;
    public static Set<ScoreSet> scoreSets = new HashSet<ScoreSet>();
    
    public static void main(String[] args) {
        Farkle main = new Farkle();
        main.run();
    }
    
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
        List<ScoreSet> scores = dice.findScoreSets();
        System.out.println(scores);
    }
    
    public class Dice {
        
        private int diceNum;
        private int[] roll = new int[totalDice];
        
        public Dice(int diceNum) {
            this.diceNum = diceNum;
            roll();
        }
        
        public Dice(int a, int b, int c, int d, int e, int f) {
            roll[0] = a;
            roll[1] = b;
            roll[2] = c;
            roll[3] = d;
            roll[4] = e;
            roll[5] = f;
        }
        
        public int[] roll() {
            for (int i = 0; i < totalDice; i++) {
                roll[i] = i < diceNum ? rand.nextInt(6) + 1 : 0;
            }
            return roll;
        }
        
        public List<ScoreSet> findScoreSets() {
            List<ScoreSet> foundScoreSets = new ArrayList<ScoreSet>();
            
            int n = 0;
            for (int i : roll)
                if (i != 0) n++;
            int[] cleanRoll = new int[n];
            int j = 0;
            for (int i : roll)
                if (i != 0) cleanRoll[j++] = i;
            
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
