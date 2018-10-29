package com.zxsoft.crawler.p.ext.text;

import com.zxsoft.crawler.common.type.RecordType;
import com.zxsoft.crawler.p.ext.text.impl.BBS.DiscuzBBSExtractor;
import com.zxsoft.crawler.p.ext.text.impl.BBS.PHPWindBBSExtractor;
import com.zxsoft.crawler.p.ext.text.impl.BBS.TianyaBBSExtractor;
import com.zxsoft.crawler.p.ext.text.impl.BBSExtractor;
import com.zxsoft.crawler.p.ext.text.impl.DefaultExtractor;
import com.zxsoft.crawler.p.model.RecordModel;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by tony on 16-9-18.
 */
public class ExtractorFactory {

    public static IExtractor getInstance(int type, Document doc, String url) {
        Logger log = LoggerFactory.getLogger(ExtractorFactory.class);
        IExtractor extractor = null;
        switch (type) {
            case 1:
                RecordModel.dao.moduleCount(RecordType.MODULE_BBS, 1); // 论坛模板总量计数

                Elements generator = doc.select("meta[name=generator]");
                Elements author = doc.select("meta[name=author]");
                Elements e_t_f = doc.select(".t_f"); // 使用了discuz论坛模板但没有该模板标志

                if (0 < generator.size() && generator.first().attr("content").toLowerCase().contains("discuz") ||
                        0 < author.size() && author.first().attr("content").toLowerCase().contains("discuz")
                        || 0 < e_t_f.size()) {
                    extractor = DiscuzBBSExtractor.getInstance();
                } else if (0 < generator.size() && generator.first().attr("content").toLowerCase().contains("phpwind")) {
                    extractor = PHPWindBBSExtractor.getInstance();
                } else if (url.contains("m.tianya") || url.contains("bbs.tianya")) { // 天涯论坛
                    extractor = TianyaBBSExtractor.getInstance(url);
                } else {
                    extractor = BBSExtractor.getInstance();
                    // 默认论坛模板
                    RecordModel.dao.moduleCount(RecordType.MODULE_BBS_OTHER, 1);
                    RecordModel.dao.unableResolveUrl(RecordType.MODULE_BBS_OTHER, url);
                }
//                 else if () {
//                    extractor = VanillaBBSExtractor.getInstance();
//                }
                break;
            default:
                extractor = DefaultExtractor.getInstance();
                break;
        }
        return extractor;
    }
}
