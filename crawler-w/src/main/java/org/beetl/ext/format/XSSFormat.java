package org.beetl.ext.format;

import com.jfinal.kit.StrKit;
import org.beetl.core.Format;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by cox on 2015/10/10.
 */
public class XSSFormat implements Format {

    private static final Logger log = LoggerFactory.getLogger(XSSFormat.class);

    @Override
    public Object format(Object o, String s) {
        try {
            String str = String.valueOf(o);
            if (StrKit.isBlank(str)) return "";
            return str.replaceAll("\"", "&quot;").replaceAll("<", "&lt;").replaceAll(">", "&gt;");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }
}
