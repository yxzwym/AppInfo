package cm.cym.packinfo.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 缓存
 */
public class SP {
    private static SharedPreferences mSp;
    private static SharedPreferences.Editor mEditor;

    // 保存的文件名
    private static final String FILE_NAME = "PACKINFO_SP";

    // 选择的是什么模式
    private static final String MODE = "MODE";
    // 选择的是什么排序
    private static final String SORT = "SORT";

    /**
     * 初始化
     */
    public static void init(Context context) {
        mSp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        mEditor = mSp.edit();
        mEditor.apply();
    }

    /**
     * 设置当前选择的是什么模式
     *
     * @param mode mode
     */
    public static void setMode(int mode) {
        mEditor.putInt(MODE, mode);
        mEditor.commit();
    }

    /**
     * 获取当前选择的是什么模式
     *
     * @return mode
     */
    public static int getMode() {
        return mSp.getInt(MODE, 0);
    }

    /**
     * 设置当前选择的是什么排序
     *
     * @param sort sort
     */
    public static void setSort(int sort) {
        mEditor.putInt(SORT, sort);
        mEditor.commit();
    }

    /**
     * 获取当前设置的是什么排序
     *
     * @return sort
     */
    public static int getSort() {
        return mSp.getInt(SORT, 0);
    }
}
