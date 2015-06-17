package rekkyn.farkle;

import org.junit.Test;

public class TestFarkle {
    
    @Test
    public void test() {
        Farkle f = new Farkle();
        System.out.println(f.getAverageScore(300, 3, 0));
    }
    
}
