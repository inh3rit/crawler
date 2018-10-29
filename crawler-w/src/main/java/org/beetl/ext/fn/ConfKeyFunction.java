package org.beetl.ext.fn;

import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import org.beetl.core.Context;
import org.beetl.core.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by cox on 2015/12/15.
 */
public class ConfKeyFunction implements Function {

    private static final Logger log = LoggerFactory.getLogger(ConfKeyFunction.class);
    private static final Prop prop = PropKit.use("conf_name.properties");

    @Override
    public Object call(Object[] objects, Context context) {
        try {
            if (objects.length<1) return prop.get("undefined");
            return prop.get(objects[0].toString().toLowerCase());
        } catch (Exception e) {
            log.error(e.getMessage());
            return prop.get("undefined");
        }
    }
}
