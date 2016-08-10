package car.ccut.com.vehicle.bean;

import java.io.Serializable;
import java.util.List;

/**
 * *
 * へ　　　　　／|
 * 　　/＼7　　　 ∠＿/
 * 　 /　│　　 ／　／
 * 　│　Z ＿,＜　／　　 /`ヽ
 * 　│　　　　　ヽ　　 /　　〉
 * 　 Y　　　　　`　 /　　/
 * 　ｲ●　､　●　　⊂⊃〈　　/
 * 　()　 へ　　　　|　＼〈
 * 　　>ｰ ､_　 ィ　 │ ／／      去吧！
 * 　 / へ　　 /　ﾉ＜| ＼＼        比卡丘~
 * 　 ヽ_ﾉ　　(_／　 │／／           消灭代码BUG
 * 　　7　　　　　　　|／
 * 　　＞―r￣￣`ｰ―＿
 * Created by WangXin on 2016/7/30 0030.
 */
public class News implements Serializable{
    private String channelId;
    private String channelName;
    private String content;
    private List<Imageurls> imageurls;
    private String link;
    private String nid;
    private String pubDate;
    private String sentiment_display;
    private String source;
    private String title;

    public String getChannelId() {
        return channelId;
    }

    public String getChannelName() {
        return channelName;
    }

    public String getContent() {
        return content;
    }

    public List<Imageurls> getImageurls() {
        return imageurls;
    }

    public String getLink() {
        return link;
    }

    public String getNid() {
        return nid;
    }

    public String getPubDate() {
        return pubDate;
    }

    public String getSentiment_display() {
        return sentiment_display;
    }

    public String getSource() {
        return source;
    }

    public String getTitle() {
        return title;
    }
}


