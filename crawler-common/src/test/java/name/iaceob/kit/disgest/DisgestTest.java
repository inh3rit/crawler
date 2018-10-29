package name.iaceob.kit.disgest;

import org.junit.Test;

/**
 * Created by tony on 17-9-18.
 */
public class DisgestTest {

    @Test
    public void encodeMD5() throws Exception {
        System.out.println(Disgest.encodeMD5("http://www.epochtimes.com/gb/17/9/21/n9653671.htm"));
    }


}
