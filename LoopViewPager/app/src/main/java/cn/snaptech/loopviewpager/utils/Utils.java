package cn.snaptech.loopviewpager.utils;

import android.content.Context;
import android.net.Uri;

public class Utils {
    public static final String RESOURCE = "android.resource://";

    /**
     * 资源 id 转换成 Uri
     * @param context
     * @param resourceId
     * @return
     */
    public static Uri idToUri(Context context, int resourceId) {
        return Uri.parse(RESOURCE + context.getPackageName() + "/" + resourceId);
    }
}
