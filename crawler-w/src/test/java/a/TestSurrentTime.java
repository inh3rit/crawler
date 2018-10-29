package a;

import org.junit.Test;

/**
 * Created by cox on 2015/9/1.
 */
public class TestSurrentTime {

    @Test
    public void test() {
        System.out.println(1.0d / (System.currentTimeMillis() / 60000.0d + (System.currentTimeMillis()-60l*60l*1000l) * 1.0d));
    }

}
