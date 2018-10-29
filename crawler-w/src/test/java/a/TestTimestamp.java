package a;

import org.junit.Test;

/**
 * Created by cox on 2015/9/2.
 */
public class TestTimestamp {


    @Test
    public void test(){
        Long t = System.currentTimeMillis();
        System.out.println(t);
        Long ts = t / 1000l;
        System.out.println(t / 60000.0d);
        System.out.println(ts);
        System.out.println(ts.intValue());
    }


}
