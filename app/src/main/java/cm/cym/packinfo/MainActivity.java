package cm.cym.packinfo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.pm.SigningInfo;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;

import cm.cym.packinfo.list.AppAdapter;
import cm.cym.packinfo.list.AppBean;
import cm.cym.packinfo.util.PackUtil;
import cm.cym.packinfo.util.SP;
import cm.cym.packinfo.util.ScreenUtil;
import cm.cym.packinfo.util.SignatureUtil;

public class MainActivity extends Activity {

    private static final int SEARCH_TYPE_ALL = 0;// 所有APP
    private static final int SEARCH_TYPE_SYSTEM = 1;// 系统APP
    private static final int SEARCH_TYPE_USER = 2;// 用户APP

    private Context mContext;

    // 系统获取的包名列表
    private List<String> mPackNames;

    // APP列表
    private List<AppBean> mAppList;// 所有的APP列表
    private List<AppBean> mShowList;// 需要显示的APP列表
    private AppAdapter mAdapter;

    // 分页加载
    private final int mPageSize = 15;
    private int mCurPage = 0;
    private int mMaxPage = 0;

    // 搜索的文本
    private String mSearchText = "";
    // 搜索的类型
    private int mSearchType = SEARCH_TYPE_USER;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;

        Toolbar tool_bar = findViewById(R.id.tool_bar);
        ListView list_view = findViewById(R.id.list_view);

        // 沉浸式状态栏
        ScreenUtil.hideStateBar(getWindow());
        // 设置顶部边距
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) tool_bar.getLayoutParams();
        layoutParams.topMargin = ScreenUtil.getStatusHeight(this);
        // ToolBar
        tool_bar.setTitleTextColor(getResources().getColor(R.color.white));
        setActionBar(tool_bar);

        // 初始化SP
        SP.init(getApplicationContext());

        // 初始化列表
        mPackNames = new ArrayList<>();
        mAppList = new ArrayList<>();
        mShowList = new ArrayList<>();
        mAdapter = new AppAdapter(this, 0, mShowList);
        list_view.setAdapter(mAdapter);
        // 列表点击事件
        list_view.setOnItemClickListener((parent, view, position, id) -> {
            AppBean bean = mShowList.get(position);
            String packName = bean.getPackName();
            showSignatureDialog(packName);
        });

        // 获取APP列表
        getApp();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        // 搜索框进行筛选
        SearchView item_search = (SearchView) menu.findItem(R.id.item_search).getActionView();
        item_search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // 提交搜索，不处理
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // 搜索文本改变
                mSearchText = newText;
                refreshList();
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // 不用switch
        if (id == R.id.item_all) {
            // 筛选所有应用
            mSearchType = SEARCH_TYPE_ALL;
            refreshList();
        } else if (id == R.id.item_system) {
            // 筛选系统应用
            mSearchType = SEARCH_TYPE_SYSTEM;
            refreshList();
        } else if (id == R.id.item_user) {
            // 筛选用户应用
            mSearchType = SEARCH_TYPE_USER;
            refreshList();
        } else if (id == R.id.item_setting) {
            // 设置
            showSettingDialog();
        } else if (id == R.id.item_about) {
            // 关于
            showAboutDialog();
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * 重新获取APP列表
     */
    private void getApp() {
        // 获取APP列表
        switch (SP.getMode()) {
            case 0:
                // 正常模式
                mPackNames = PackUtil.getPacksBySystem(this);
                break;
            case 1:
                // 备用模式
                mPackNames = PackUtil.getPacksByQueryIntent(this);
                break;
            default:
                // waht？
                mPackNames = new ArrayList<>();
                break;
        }

        mCurPage = 0;
        mMaxPage = mPackNames.size() / mPageSize;
        mAppList.clear();
        mAdapter.notifyDataSetChanged();
        // 分页加载
        loadPage();
    }

    /**
     * 分页加载
     */
    private void loadPage() {
        new Thread(() -> {
            // 分页加载APP
            List<AppBean> list = new ArrayList<>();
            for (int i = mCurPage * mPageSize; i < (mCurPage + 1) * mPageSize; i++) {
                if (i >= mPackNames.size()) {
                    break;
                }
                PackageInfo pack = PackUtil.getPackInfo(this, mPackNames.get(i));
                if (pack == null) {
                    continue;
                }
                list.add(new AppBean(
                        pack.applicationInfo.loadIcon(getPackageManager()),
                        pack.applicationInfo.loadLabel(getPackageManager()).toString(),
                        pack.packageName,
                        (pack.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0 || (pack.applicationInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0
                ));
            }
            mAppList.addAll(list);
            // 加载完成后刷新
            runOnUiThread(this::refreshList);
            // 如果没加载完，就继续加载
            mCurPage++;
            if (mCurPage <= mMaxPage) {
                loadPage();
            }
        }).start();
    }

    /**
     * 根据筛选条件刷新列表
     */
    private void refreshList() {
        mShowList.clear();
        // 筛选
        for (AppBean bean : mAppList) {
            if (isDestroyed() || isFinishing()) {
                return;
            }
            // 筛选APP类型
            if ((mSearchType == SEARCH_TYPE_SYSTEM && !bean.isSystemApp()) || (mSearchType == SEARCH_TYPE_USER && bean.isSystemApp())) {
                try {
                    continue;
                } catch (ConcurrentModificationException concurrentModificationException) {
                    // 报错了就重新刷新
                    refreshList();
                    return;
                }
            }
            // 筛选包名和APP名
            if (bean.getAppName().contains(mSearchText) || bean.getPackName().contains(mSearchText)) {
                mShowList.add(bean);
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    /**
     * 弹出签名信息框
     */
    private void showSignatureDialog(String packName) {
        final String signature = ""
                + "MD5：\n" + SignatureUtil.getMd5(this, packName) + "\n\n"
                + "SHA1：\n" + SignatureUtil.getSha1(this, packName) + "\n\n"
                + "SHA1 Binary Base64：\n" + SignatureUtil.getSha1Base64(this, packName);

        AlertDialog.Builder builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Light_Dialog_Alert);
        builder.setTitle(getString(R.string.signature));
        builder.setMessage(signature);
        builder.setPositiveButton(getString(R.string.copy), ((dialog, which) -> {
            // 复制到剪切板
            ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData mClipData = ClipData.newPlainText("label", signature);
            cm.setPrimaryClip(mClipData);
            Toast.makeText(mContext, getString(R.string.copy_to_clip_success), Toast.LENGTH_SHORT).show();
        }));
        builder.setNegativeButton(getString(R.string.share), ((dialog, which) -> {
            // 分享
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_TEXT, signature);
            intent.setType("text/plain");
            startActivity(Intent.createChooser(intent, getString(R.string.signature)));
        }));
        builder.show();
    }

    // 当前dialog里选择的模式
    private int mSettingMode;

    /**
     * 弹出设置框
     */
    private void showSettingDialog() {
        String[] item = new String[]{getString(R.string.normal_mode), getString(R.string.standby_mode)};

        AlertDialog.Builder builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Light_Dialog_Alert);
        builder.setTitle(getString(R.string.setting));
        builder.setSingleChoiceItems(item, SP.getMode(), (dialog, which) -> {
            mSettingMode = which;
        });
        builder.setPositiveButton(getString(R.string.save), (dialog, which) -> {
            // 保存
            SP.setMode(mSettingMode);
            getApp();
        });
        builder.setNegativeButton(getString(R.string.cancel), (dialog, which) -> {
            // 取消
        });
        builder.show();
    }

    /**
     * 弹出关于框
     */
    private void showAboutDialog() {
        final TextView text_view = new TextView(this);
        final SpannableString spannableString = new SpannableString(getString(R.string.about_info));
        Linkify.addLinks(spannableString, Linkify.WEB_URLS);
        text_view.setText(spannableString);
        text_view.setTextColor(getResources().getColor(R.color.blackFont));
        int dp = ScreenUtil.dp2px(this, 20);
        text_view.setPadding(dp, dp, dp, dp);
        text_view.setMovementMethod(LinkMovementMethod.getInstance());

        AlertDialog.Builder builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Light_Dialog_Alert);
        builder.setTitle(getString(R.string.about));
        builder.setView(text_view);
        builder.show();
    }
}
