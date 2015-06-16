package rekkyn.farkle;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TestFarkle {
    
    @Test
    public void test() {
        Farkle f = new Farkle();
        assertEquals(f.getAverageScore(100, 1), 58.333, 0.1);
        assertEquals(f.getAverageScore(0, 1), 25, 0.1);
        assertEquals(f.getAverageScore(50, 1), 41.666, 0.1);
        System.out.println(f.maxScore(0));
    }
    
}
