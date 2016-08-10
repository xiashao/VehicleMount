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
public class NewsPage implements Serializable{
    private int allNum;
    private int allPages;
    private List<News> contentlist;
    private int currentPage;
    private int maxResult;

    public int getAllNum() {
        return allNum;
    }

    public int getAllPages() {
        return allPages;
    }

    public List<News> getContentlist() {
        return contentlist;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public int getMaxResult() {
        return maxResult;
    }
}
