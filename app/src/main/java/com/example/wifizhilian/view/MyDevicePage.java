package com.example.wifizhilian.view;

import android.annotation.SuppressLint;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import com.example.wifizhilian.R;
import com.example.wifizhilian.LedsixColorActivity;
//import com.example.wifizhilian.LedtwoColorActivity;
import com.example.wifizhilian.MainActivity;
import com.example.wifizhilian.ScanAPListActivity;
import com.example.wifizhilian.SysApp;
import com.example.wifizhilian.UDPServer;
//import com.example.wifizhilian.WaveMakerActivity;
import com.example.wifizhilian.data.AppConfig;
import com.example.wifizhilian.data.BindDevObj;
import com.example.wifizhilian.libs.ComUtils;
import com.example.wifizhilian.libs.NetInterface;
import com.example.wifizhilian.libs.xLog;
import com.songnick.blogdemo.widget.SwitchLayout;
import com.songnick.blogdemo.widget.SwitchLayout.Status;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyDevicePage extends LinearLayout {
    private static final int Refresh_DevList = 3000;
    protected static final String TAG = "MyDevicePage";
    private MyAdapter mAdapter;
    private Context mContext;
    private List<Map<String, Object>> mData;
    private ListView mDevList;
    private Handler mHandler = new C03382();
    private PageHead mPageHead;
    private PopupWindow menuPop;
    private boolean showGroup = true;
    Runnable timeRefresh = new C03393();
    private BroadcastReceiver updataReceiver = new C03371();

    /* renamed from: com.example.wifizhilian.view.MyDevicePage$1 */
    class C03371 extends BroadcastReceiver {
        C03371() {
        }

        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(AppConfig.DEVICE_LIST_CHANG)) {
                MyDevicePage.this.RefreshDevList();
            }
        }
    }

    /* renamed from: com.example.wifizhilian.view.MyDevicePage$2 */
    class C03382 extends Handler {
        C03382() {
        }

        @SuppressLint("WrongConstant")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case NetInterface.Net_DataSubmit_FAIL /*20014*/:
                    Toast.makeText(MyDevicePage.this.mContext, R.string.str_submit_failure, 0).show();
                    return;
                case NetInterface.Net_UnKnown_Error /*20018*/:
                    Toast.makeText(MyDevicePage.this.mContext, R.string.str_error_unknown, 0).show();
                    return;
                case NetInterface.Net_Data_Error /*20019*/:
                    Toast.makeText(MyDevicePage.this.mContext, R.string.str_error_net, 0).show();
                    return;
                default:
                    return;
            }
        }
    }

    /* renamed from: com.example.wifizhilian.view.MyDevicePage$3 */
    class C03393 implements Runnable {
        C03393() {
        }

        public void run() {
            if (((MainActivity) MyDevicePage.this.mContext).getMainLayout().getViewStatus() == Status.Close) {
                MyDevicePage.this.RefreshDevList();
            }
            MyDevicePage.this.mHandler.postDelayed(MyDevicePage.this.timeRefresh, 3000);
        }
    }

    /* renamed from: com.example.wifizhilian.view.MyDevicePage$4 */
    class C03404 implements OnClickListener {
        C03404() {
        }

        public void onClick(View v) {
            SwitchLayout layout = ((MainActivity) MyDevicePage.this.mContext).getMainLayout();
            if (layout.getViewStatus() == Status.Close) {
                layout.ShowLeft();
            } else {
                layout.CloseLeft();
            }
        }
    }

    /* renamed from: com.example.wifizhilian.view.MyDevicePage$5 */
    class C03415 implements OnClickListener {
        C03415() {
        }

        public void onClick(View v) {
            MyDevicePage.this.showMenuList(v);
        }
    }

    /* renamed from: com.example.wifizhilian.view.MyDevicePage$6 */
    class C03426 implements OnItemClickListener {
        C03426() {
        }

        public void onItemClick(AdapterView<?> adapterView, View arg1, int arg2, long arg3) {
            switch (arg2) {
                case 0:
                    MyDevicePage.this.mContext.startActivity(new Intent(MyDevicePage.this.mContext, ScanAPListActivity.class));
                    break;
                case 1:
                    MyDevicePage.this.showGroup = !MyDevicePage.this.showGroup;
                    MyDevicePage.this.RefreshDevList();
                    break;
            }
            MyDevicePage.this.menuPop.dismiss();
        }
    }

    public final class MenuItemHolder {
        public ImageView icon;
        public TextView info;
        public ImageView opbnt;
        public TextView title;
    }

    public class MyAdapter extends BaseAdapter {
        private LayoutInflater mInflater;

        public MyAdapter(Context context) {
            this.mInflater = LayoutInflater.from(context);
        }

        public int getCount() {
            return MyDevicePage.this.mData.size();
        }

        public long getItemId(int arg0) {
            return 0;
        }

        public Object getItem(int index) {
            if (index < MyDevicePage.this.mData.size()) {
                return MyDevicePage.this.mData.get(index);
            }
            return null;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            MenuItemHolder holder;
            if (convertView == null) {
                holder = new MenuItemHolder();
                convertView = this.mInflater.inflate(R.layout.view_devlist_item, null);
                holder.title = (TextView) convertView.findViewById(R.id.TitletextView);
                holder.info = (TextView) convertView.findViewById(R.id.InfotextView);
                holder.icon = (ImageView) convertView.findViewById(R.id.APIconimageView);
                holder.opbnt = (ImageView) convertView.findViewById(R.id.OptionimageView);
                convertView.setTag(holder);
            } else {
                holder = (MenuItemHolder) convertView.getTag();
            }
            final String titleid = (String) ((Map) MyDevicePage.this.mData.get(position)).get("chipid");
            holder.title.setText((String) ((Map) MyDevicePage.this.mData.get(position)).get("title"));
            holder.info.setText((String) ((Map) MyDevicePage.this.mData.get(position)).get("info"));
            holder.icon.setImageResource(Integer.parseInt(String.valueOf(((Map) MyDevicePage.this.mData.get(position)).get("icon"))));
            if (String.valueOf(((Map) MyDevicePage.this.mData.get(position)).get("online")).equals("T")) {
                holder.opbnt.setImageResource(R.drawable.icon_online);
            } else {
                holder.opbnt.setImageResource(R.drawable.icon_offline);
            }
            convertView.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    MyDevicePage.this.ListItemClick(titleid);
                }
            });
            convertView.setOnLongClickListener(new OnLongClickListener() {
                public boolean onLongClick(View v) {
                    MyDevicePage.this.ListItemLongClick(titleid);
                    return false;
                }
            });
            return convertView;
        }
    }

    public MyDevicePage(Context context) {
        super(context);
        this.mContext = context;
        View layout = ((LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.page_mydevice, this);
        this.mPageHead = (PageHead) findViewById(R.id.MyDevicepageHead);
        this.mPageHead.LeftBnt.setImageResource(R.drawable.menu_2);
        this.mPageHead.LeftBnt.setOnClickListener(new C03404());
        this.mPageHead.RightBnt.setImageResource(R.drawable.icon_more);
        this.mPageHead.RightBnt.setOnClickListener(new C03415());
        this.mPageHead.TitleView.setText(R.string.menu_item_mydevice);
        this.mDevList = (ListView) findViewById(R.id.MyDevicelistView);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(AppConfig.DEVICE_LIST_CHANG);
        this.mContext.registerReceiver(this.updataReceiver, intentFilter);
        this.mData = getData();
        this.mAdapter = new MyAdapter(this.mContext);
        this.mDevList.setAdapter(this.mAdapter);
        this.mHandler.postDelayed(this.timeRefresh, 3000);
    }

    public void showMenuList(View v) {
        View view = LayoutInflater.from(this.mContext).inflate(R.layout.view_pop_menu, null);
        ListView userView = (ListView) view.findViewById(R.id.MenulistView);
        userView.setAdapter(new SimpleAdapter(this.mContext, getMenuData(), R.layout.view_textmenu_item, new String[]{"title"}, new int[]{R.id.MenuItemtextView}));
        userView.setOnItemClickListener(new C03426());
        this.menuPop = new PopupWindow(view, (int) getResources().getDimension(R.dimen.pop_menu_width), -2, false);
        this.menuPop.setBackgroundDrawable(new ColorDrawable(-13421773));
        this.menuPop.setOutsideTouchable(true);
        this.menuPop.update();
        this.menuPop.setFocusable(true);
        int[] location = new int[2];
        v.getLocationOnScreen(location);
        int nX = (int) getResources().getDimension(R.dimen.menu_top_padd);
        int nY = location[1] + v.getHeight();
        xLog.m5i(TAG, "NX:" + nX + "   NY:" + nY);
        this.menuPop.showAtLocation(v, 53, nX, nY);
    }

    private List<? extends Map<String, ?>> getMenuData() {
        List<HashMap<String, Object>> mHashMaps = new ArrayList();
        HashMap<String, Object> map1 = new HashMap();
        map1.put("title", getResources().getString(R.string.menu_item_config));
        mHashMaps.add(map1);
        map1 = new HashMap();
        if (this.showGroup) {
            map1.put("title", getResources().getString(R.string.menu_item_alone));
        } else {
            map1.put("title", getResources().getString(R.string.menu_item_group));
        }
        mHashMaps.add(map1);
        return mHashMaps;
    }

    private List<Map<String, Object>> getData() {
        int i;
        List<Map<String, Object>> list = new ArrayList();
        List<BindDevObj> tmpdevList = SysApp.getMe().getDevices();
        List<BindDevObj> devList = new ArrayList();
        for (i = 0; i < tmpdevList.size(); i++) {
            if (!this.showGroup) {
                devList.add(tmpdevList.get(i));
            } else if (((BindDevObj) tmpdevList.get(i)).isGroup() != 2) {
                devList.add(tmpdevList.get(i));
            }
        }
        for (i = 0; i < devList.size(); i++) {
            BindDevObj obj = (BindDevObj) devList.get(i);
            String name = obj.getNickName();
            if (ComUtils.StrIsEmpty(name)) {
                name = SysApp.getMe().getDevName(obj.getProduct(), obj.getSSID());
            }
            String info = "";
            String isOnline = "F";
            if (UDPServer.checkChipID(obj.getChipid())) {
                if (!UDPServer.getDevList().containsKey(Integer.valueOf(obj.getNetID())) || System.currentTimeMillis() - ((BindDevObj) UDPServer.getDevList().get(Integer.valueOf(obj.getNetID()))).getRXtime() >= 10000) {
                    isOnline = "F";
                } else {
                    isOnline = "T";
                }
            }
            Map<String, Object> map = new HashMap();
            map.put("chipid", obj.getChipid());
            map.put("ssid", obj.getSSID());
            map.put("title", name);
            map.put("info", info);
            map.put("online", isOnline);
            if (obj.isGroup() == 1) {
                map.put("icon", Integer.valueOf(R.drawable.led_dong_group));
            } else {
                map.put("icon", Integer.valueOf(R.drawable.led_dong));
            }
            map.put("opbnt", Integer.valueOf(R.drawable.setting));
            list.add(map);
        }
        return list;
    }

    private void RefreshDevList() {
        this.mData = getData();
        this.mAdapter.notifyDataSetChanged();
    }

    @SuppressLint("WrongConstant")
    public void ListItemClick(String chipID) {
        xLog.m5i(TAG, "DevID:" + chipID);
        BindDevObj dev = SysApp.getMe().getDevByChipid(chipID);
        if (dev.isGroup() == 2) {
            Toast.makeText(this.mContext, R.string.str_sondev_noop, 0).show();
            return;
        }
        Intent intent;
        xLog.m5i(TAG, "getProduct Name:" + dev.getProduct() + "   SSID:" + dev.getSSID());
//        if (dev.getDevType() == 3 || dev.getProduct().startsWith("WaterPump_a")) {
//            intent = new Intent(this.mContext, WaveMakerActivity.class);
//        } else
          //if (dev.getDevType() == 2 || dev.getProduct().startsWith("MultiLED_6")) {
            intent = new Intent(this.mContext, LedsixColorActivity.class);
        //}
        //else {
//            intent = new Intent(this.mContext, LedtwoColorActivity.class);
//        }
        intent.putExtra("chipid", chipID);
        this.mContext.startActivity(intent);
    }

    public void ListItemLongClick(String chipID) {
        xLog.m5i(TAG, "DevID:" + chipID);
        final BindDevObj dev = SysApp.getMe().getDevByChipid(chipID);
        new Builder(this.mContext).setTitle(getResources().getString(R.string.str_is_delete) + "\r\n" + dev.getNickName()).setIcon(R.drawable.select_yes).setPositiveButton(R.string.str_confirm, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                SysApp.getMe().getDevices().remove(dev);
                new NetInterface(MyDevicePage.this.mContext, MyDevicePage.this.mHandler).UnBindDevice(dev);
                MyDevicePage.this.RefreshDevList();
            }
        }).setNegativeButton(R.string.str_cancel, null).show();
    }

    public void dispose() {
        this.mContext.unregisterReceiver(this.updataReceiver);
        this.mHandler.removeCallbacks(this.timeRefresh);
    }
}
