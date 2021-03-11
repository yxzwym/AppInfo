package cm.cym.packinfo.list;

import android.graphics.drawable.Drawable;

public class AppBean {
    private Drawable appIcon;
    private String appName;
    private String packName;
    private boolean isSystemApp;

    public AppBean(Drawable appIcon, String appName, String packName, boolean isSystemApp) {
        this.appIcon = appIcon;
        this.appName = appName;
        this.packName = packName;
        this.isSystemApp = isSystemApp;
    }

    public Drawable getAppIcon() {
        return appIcon;
    }

    public void setAppIcon(Drawable appIcon) {
        this.appIcon = appIcon;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getPackName() {
        return packName;
    }

    public void setPackName(String packName) {
        this.packName = packName;
    }

    public boolean isSystemApp() {
        return isSystemApp;
    }

    public void setSystemApp(boolean systemApp) {
        isSystemApp = systemApp;
    }
}
