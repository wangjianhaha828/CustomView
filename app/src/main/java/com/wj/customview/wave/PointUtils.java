package com.wj.customview.wave;

/**
 * 坐标工具类
 * Created by wangjian on 2017/12/28.
 */

public class PointUtils {

    private static PointUtils sPointUtils;
    public static synchronized PointUtils getInstance() {
        if (sPointUtils == null) {
            sPointUtils = new PointUtils();
        }
        return sPointUtils;
    }

}
