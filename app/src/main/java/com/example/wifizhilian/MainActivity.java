package com.example.wifizhilian;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.example.wifizhilian.view.AppSetting;
import com.example.wifizhilian.view.FeedbackPage;
import com.example.wifizhilian.view.LANDevicePage;
import com.example.wifizhilian.view.MyDevicePage;
import com.example.wifizhilian.view.NoFinish;
import com.example.wifizhilian.view.UserInfoPage;
import com.example.wifizhilian.R;
import com.songnick.blogdemo.widget.SwitchLayout;
import com.songnick.blogdemo.widget.SwitchLayout.Status;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//    }

    private static final int REQUSET_CODE_WRITE_EXTERNAL_STORAGE = 200;
    private MyAdapter mAdapter;
    private int mCurPageTitle = 0;
    private List<Map<String, Object>> mData;
    private LANDevicePage mLANDevicePage;
    private ListView mLeftMenuList;
    private LinearLayout mMainPage;
    private MyDevicePage mMyDevicePage;
    private SwitchLayout mSwitchLayout;

    /* renamed from: com.example.wifizhilian.MainActivity$1 */
    class C02631 implements OnClickListener {
        C02631() {
        }

        public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
            MainActivity.this.finish();
            MainActivity.this.getApplication().onTerminate();
            System.exit(0);
        }
    }

    /* renamed from: com.example.wifizhilian.MainActivity$2 */
    class C02642 implements OnClickListener {
        C02642() {
        }

        public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
        }
    }

    public final class MenuItemHolder {
        public ImageView icon;
        public TextView title;
    }

    public class MyAdapter extends BaseAdapter {
        private LayoutInflater mInflater;

        public MyAdapter(Context context) {
            this.mInflater = LayoutInflater.from(context);
        }

        public int getCount() {
            return MainActivity.this.mData.size();
        }

        public long getItemId(int arg0) {
            return 0;
        }

        public Object getItem(int index) {
            if (index < MainActivity.this.mData.size()) {
                return MainActivity.this.mData.get(index);
            }
            return null;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            MenuItemHolder holder;
            if (convertView == null) {
                holder = new MenuItemHolder();
                convertView = this.mInflater.inflate(R.layout.view_iconmenu, null);
                holder.title = (TextView) convertView.findViewById(R.id.MenuTitletextView);
                holder.icon = (ImageView) convertView.findViewById(R.id.MenuIconimageView);
                convertView.setTag(holder);
            } else {
                holder = (MenuItemHolder) convertView.getTag();
            }
            final int titleid = Integer.parseInt(String.valueOf(((Map) MainActivity.this.mData.get(position)).get("title")));
            holder.title.setText(titleid);
            holder.icon.setImageResource(Integer.parseInt(String.valueOf(((Map) MainActivity.this.mData.get(position)).get("icon"))));
            convertView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    MainActivity.this.MenuItemClick(titleid);
                }
            });
            return convertView;
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.mSwitchLayout = (SwitchLayout) findViewById(R.id.switch_layout);
        this.mLeftMenuList = (ListView) findViewById(R.id.LeftMenulistView);
        this.mData = getData();
        this.mAdapter = new MyAdapter(this);
        this.mLeftMenuList.setAdapter(this.mAdapter);
        this.mMainPage = (LinearLayout) findViewById(R.id.surfacePage);
        this.mMyDevicePage = new MyDevicePage(this);
        this.mCurPageTitle = R.string.menu_item_mydevice;
        this.mMainPage.addView(this.mMyDevicePage);
    }

    @SuppressLint({"Override"})
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUSET_CODE_WRITE_EXTERNAL_STORAGE /*200*/:
                if (grantResults.length > 0 && grantResults[0] == 0) {
                    return;
                }
                return;
            default:
                return;
        }
    }

    protected void onStart() {
        super.onStart();
    }

    public SwitchLayout getMainLayout() {
        return this.mSwitchLayout;
    }

    public void ShowMenu() {
    }

    public void MenuItemClick(int titleid) {
        if (this.mSwitchLayout.getViewStatus() != Status.Close) {
            SelectMenuItem(titleid);
        }
    }

    public void SelectMenuItem(int titleid) {
        if (titleid == R.string.menu_item_exit) {
            exitApp();
            return;
        }
        this.mMainPage.removeAllViews();
        switch (this.mCurPageTitle) {
            case R.string.menu_item_mydevice:
                if (this.mMyDevicePage != null) {
                    this.mMyDevicePage.dispose();
                    this.mMyDevicePage = null;
                    break;
                }
                break;
            case R.string.menu_item_shortcut:
                if (this.mLANDevicePage != null) {
                    this.mLANDevicePage.dispose();
                    this.mLANDevicePage = null;
                    break;
                }
                break;
        }
        this.mCurPageTitle = 0;
        switch (titleid) {
            case R.string.menu_item_about:
                if (this.mCurPageTitle != R.string.menu_item_about) {
                    this.mCurPageTitle = R.string.menu_item_about;
                    this.mMainPage.addView(new NoFinish(this, getResources().getString(R.string.menu_item_about)));
                    return;
                }
                return;
            case R.string.menu_item_feedback:
                if (this.mCurPageTitle != R.string.menu_item_feedback) {
                    this.mCurPageTitle = R.string.menu_item_feedback;
                    this.mMainPage.addView(new FeedbackPage(this));
                    return;
                }
                return;
            case R.string.menu_item_manual:
                if (this.mCurPageTitle != R.string.menu_item_manual) {
                    this.mCurPageTitle = R.string.menu_item_manual;
                    this.mMainPage.addView(new NoFinish(this, getResources().getString(R.string.menu_item_manual)));
                    return;
                }
                return;
            case R.string.menu_item_mydevice:
                if (this.mCurPageTitle != R.string.menu_item_mydevice) {
                    this.mMyDevicePage = new MyDevicePage(this);
                    this.mCurPageTitle = R.string.menu_item_mydevice;
                    this.mMainPage.addView(this.mMyDevicePage);
                    return;
                }
                return;
            case R.string.menu_item_setting:
                if (this.mCurPageTitle != R.string.menu_item_setting) {
                    this.mCurPageTitle = R.string.menu_item_setting;
                    this.mMainPage.addView(new AppSetting(this));
                    return;
                }
                return;
            case R.string.menu_item_shortcut:
                if (this.mCurPageTitle != R.string.menu_item_shortcut) {
                    this.mLANDevicePage = new LANDevicePage(this);
                    this.mCurPageTitle = R.string.menu_item_shortcut;
                    this.mMainPage.addView(this.mLANDevicePage);
                    return;
                }
                return;
            case R.string.menu_item_user:
                if (this.mCurPageTitle != R.string.menu_item_user) {
                    this.mCurPageTitle = R.string.menu_item_user;
                    this.mMainPage.addView(new UserInfoPage(this));
                    return;
                }
                return;
            default:
                return;
        }
    }

    private List<Map<String, Object>> getData() {
        List<Map<String, Object>> list = new ArrayList();
        Map<String, Object> map = new HashMap();
        map.put("title", Integer.valueOf(R.string.menu_item_mydevice));
        map.put("icon", Integer.valueOf(R.drawable.mybox));
        list.add(map);
        map = new HashMap();
        map.put("title", Integer.valueOf(R.string.menu_item_shortcut));
        map.put("icon", Integer.valueOf(R.drawable.openbox));
        list.add(map);
        map = new HashMap();
        map.put("title", Integer.valueOf(R.string.menu_item_user));
        map.put("icon", Integer.valueOf(R.drawable.user));
        list.add(map);
        map = new HashMap();
        map.put("title", Integer.valueOf(R.string.menu_item_setting));
        map.put("icon", Integer.valueOf(R.drawable.setting));
        list.add(map);
        map = new HashMap();
        map.put("title", Integer.valueOf(R.string.menu_item_feedback));
        map.put("icon", Integer.valueOf(R.drawable.message));
        list.add(map);
        map = new HashMap();
        map.put("title", Integer.valueOf(R.string.menu_item_exit));
        map.put("icon", Integer.valueOf(R.drawable.close));
        list.add(map);
        return list;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode != 4 || event.getAction() != 0) {
            return super.onKeyDown(keyCode, event);
        }
        if (this.mCurPageTitle != R.string.menu_item_mydevice) {
            SelectMenuItem(R.string.menu_item_mydevice);
        } else {
            exitApp();
        }
        return true;
    }

    private void exitApp() {
        Builder mErrDialog = new Builder(this);
        mErrDialog.setMessage(R.string.str_confirm_exit);
        mErrDialog.setPositiveButton(R.string.str_Yes, new C02631());
        mErrDialog.setNegativeButton(R.string.str_No, new C02642());
        mErrDialog.create().show();
    }
}
