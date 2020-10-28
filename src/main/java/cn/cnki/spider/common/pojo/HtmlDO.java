package cn.cnki.spider.common.pojo;

import lombok.Data;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@FieldNameConstants
@Document(collection = "crawl_html")
@CompoundIndex(name = "idx_url", def = "{'url': 1}",
        unique = true, background = true)
public class HtmlDO {

    @Id
    private String id;

    private String url;

    private String html;

    private long ctime;

    private long utime;

}
