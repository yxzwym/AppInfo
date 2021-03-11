package cm.cym.packinfo.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.ResolveInfo;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * 包管理工具类
 */
public class PackUtil {

    /**
     * 使用系统方法获取包名列表
     *
     * @param context context
     * @return list
     */
    public static List<String> getPacksBySystem(Context context) {
        List<String> packNameList = new ArrayList<>();
        List<PackageInfo> packageInfoList = context.getPackageManager().getInstalledPackages(0);
        for (PackageInfo packageInfo : packageInfoList) {
            packNameList.add(packageInfo.packageName);
        }
        return packNameList;
    }

    /**
     * 使用adb获取包名列表
     * 和用系统方法获取到的是一模一样的，没区别
     *
     * @return list
     */
    public static List<String> getPacksByAdb() {
        List<String> packNameList = new ArrayList<>();
        try {
            Process process = Runtime.getRuntime().exec("pm list package");
            BufferedReader bis = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = bis.readLine()) != null) {
                line = line.replace("package:", "");
                packNameList.add(line);
            }
        } catch (Exception ignored) {
        }
        return packNameList;
    }

    /**
     * 使用queryIntentActivities获取包名列表
     * 这种方法获取到的并没有另外两种多，除非是国产手机只回复了部分列表，这个方法能获取全部
     *
     * @param context context
     * @return list
     */
    public static List<String> getPacksByQueryIntent(Context context) {
        List<String> packNameList = new ArrayList<>();
        List<ResolveInfo> resolveInfoList = context.getPackageManager().queryIntentActivities(new Intent(Intent.ACTION_MAIN), 0);
        for (ResolveInfo resolveInfo : resolveInfoList) {
            String packName = resolveInfo.activityInfo.applicationInfo.packageName;
            if (!packNameList.contains(packName)) {
                packNameList.add(packName);
            }
        }
        return packNameList;
    }

    /**
     * 根据包名获取包信息
     *
     * @param context  context
     * @param packName 包名
     * @return packInfo
     */
    public static PackageInfo getPackInfo(Context context, String packName) {
        PackageInfo packInfo = null;
        try {
            packInfo = context.getPackageManager().getPackageInfo(packName, 0);
        } catch (Exception ignored) {
        }
        return packInfo;
    }
}
