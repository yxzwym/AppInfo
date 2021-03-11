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
}
