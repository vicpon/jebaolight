package com.example.wifizhilian.view;

import android.annotation.SuppressLint;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import com.example.wifizhilian.R;
import com.example.wifizhilian.MainActivity;
import com.example.wifizhilian.SysApp;
import com.example.wifizhilian.UDPServer;
import com.example.wifizhilian.data.AppConfig;
import com.example.wifizhilian.data.AppConfig.EnumNetType;
import com.example.wifizhilian.data.BindDevObj;
import com.example.wifizhilian.libs.ComUtils;
import com.example.wifizhilian.libs.NetInterface;
import com.example.wifizhilian.libs.xLog;
import com.songnick.blogdemo.widget.SwitchLayout.Status;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LANDevicePage extends LinearLayout {
    protected static final String TAG = "LANDevicePage";
    private MyAdapter mAdapter;
    private Context mContext;
    private List<Map<String, Object>> mData;
    private ListView mDevList;
    private Handler mHandler = new C03271();
    private PageHead mPageHead;
    Runnable timeRefresh = new C03282();

    /* renamed from: com.example.wifizhilian.view.LANDevicePage$1 */
    class C03271 extends Handler {
        C03271() {
        }

        @SuppressLint("WrongConstant")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case NetInterface.Net_DataSubmit_FAIL /*20014*/:
                    Toast.makeText(LANDevicePage.this.mContext, R.string.str_submit_failure, 0).show();
                    return;
                case NetInterface.Net_UnKnown_Error /*20018*/:
                    Toast.makeText(LANDevicePage.this.mContext, R.string.str_error_unknown, 0).show();
                    return;
                case NetInterface.Net_Data_Error /*20019*/:
                    Toast.makeText(LANDevicePage.this.mContext, R.string.str_error_net, 0).show();
                    return;
                case NetInterface.UnAllRelation /*20042*/:
                    Toast.makeText(LANDevicePage.this.mContext, R.string.str_relation_initover, 0).show();
                    return;
                default:
                    return;
            }
        }
    }

    /* renamed from: com.example.wifizhilian.view.LANDevicePage$2 */
    class C03282 implements Runnable {
        C03282() {
        }

        public void run() {
            if (((MainActivity) LANDevicePage.this.mContext).getMainLayout().getViewStatus() == Status.Close) {
                LANDevicePage.this.RefreshDevList();
            }
            LANDevicePage.this.mHandler.postDelayed(LANDevicePage.this.timeRefresh, 1000);
        }
    }

    /* renamed from: com.example.wifizhilian.view.LANDevicePage$3 */
    class C03293 implements OnClickListener {
        C03293() {
        }

        public void onClick(View v) {
            ((MainActivity) LANDevicePage.this.mContext).SelectMenuItem(R.string.menu_item_mydevice);
        }
    }

    /* renamed from: com.example.wifizhilian.view.LANDevicePage$7 */
    class C03337 implements DialogInterface.OnClickListener {
        C03337() {
        }

        public void onClick(DialogInterface dialog, int whichButton) {
        }
    }

    /* renamed from: com.example.wifizhilian.view.LANDevicePage$8 */
    class C03348 implements DialogInterface.OnClickListener {
        C03348() {
        }

        public void onClick(DialogInterface dialog, int whichButton) {
        }
    }

    public final class MenuItemHolder {
        public ImageView icon;
        public TextView info;
        public ImageView opbnt;
        public ImageView optwobnt;
        public TextView title;
    }

    public class MyAdapter extends BaseAdapter {
        private LayoutInflater mInflater;

        public MyAdapter(Context context) {
            this.mInflater = LayoutInflater.from(context);
        }

        public int getCount() {
            return LANDevicePage.this.mData.size();
        }

        public long getItemId(int arg0) {
            return 0;
        }

        public Object getItem(int index) {
            if (index < LANDevicePage.this.mData.size()) {
                return LANDevicePage.this.mData.get(index);
            }
            return null;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            MenuItemHolder holder;
            if (convertView == null) {
                holder = new MenuItemHolder();
                convertView = this.mInflater.inflate(R.layout.view_scandev_item, null);
                holder.title = (TextView) convertView.findViewById(R.id.TitletextView);
                holder.info = (TextView) convertView.findViewById(R.id.InfotextView);
                holder.icon = (ImageView) convertView.findViewById(R.id.APIconimageView);
                holder.opbnt = (ImageView) convertView.findViewById(R.id.OptionimageView);
                holder.optwobnt = (ImageView) convertView.findViewById(R.id.OptionTwoimageView);
                convertView.setTag(holder);
            } else {
                holder = (MenuItemHolder) convertView.getTag();
            }
            final String devid = String.valueOf(((Map) LANDevicePage.this.mData.get(position)).get("devid"));
            holder.title.setText((String) ((Map) LANDevicePage.this.mData.get(position)).get("title"));
            holder.info.setText((String) ((Map) LANDevicePage.this.mData.get(position)).get("info"));
            holder.icon.setImageResource(Integer.parseInt(String.valueOf(((Map) LANDevicePage.this.mData.get(position)).get("icon"))));
            holder.opbnt.setImageResource(Integer.parseInt(String.valueOf(((Map) LANDevicePage.this.mData.get(position)).get("opbnt"))));
            holder.opbnt.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    LANDevicePage.this.BindDevices(devid);
                }
            });
            holder.optwobnt.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    LANDevicePage.this.UnAllRelation(devid);
                }
            });
            return convertView;
        }
    }

    public LANDevicePage(Context context) {
        super(context);
        this.mContext = context;
        View layout = ((LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.page_landevice, this);
        this.mPageHead = (PageHead) findViewById(R.id.LanDevicepageHead);
        this.mPageHead.LeftBnt.setImageResource(R.drawable.menu_2);
        this.mPageHead.LeftBnt.setOnClickListener(new C03293());
        this.mPageHead.RightBnt.setVisibility(View.INVISIBLE);
        this.mPageHead.TitleView.setText(R.string.menu_item_shortcut);
        this.mDevList = (ListView) findViewById(R.id.LanDevicelistView);
        this.mData = getData();
        this.mAdapter = new MyAdapter(this.mContext);
        this.mDevList.setAdapter(this.mAdapter);
        this.mHandler.postDelayed(this.timeRefresh, 1000);
    }

    private List<Map<String, Object>> getData() {
        List<Map<String, Object>> list = new ArrayList();
        Map<Integer, BindDevObj> devList = UDPServer.getDevList();
        for (Integer key : devList.keySet()) {
            Map<String, Object> map = new HashMap();
            BindDevObj obj = (BindDevObj) devList.get(key);
            if (obj.getNetType().equals(EnumNetType.Lan) && !ComUtils.StrIsEmpty(obj.getSSID())) {
                String name = SysApp.getMe().getDevName(obj.getProduct(), obj.getSSID());
                String info = "";
                if (System.currentTimeMillis() - obj.getRXtime() < 4000) {
                    info = getResources().getString(R.string.str_info_online);
                    map.put("devid", obj.getChipid());
                    map.put("title", name);
                    map.put("info", info);
                    map.put("icon", Integer.valueOf(R.drawable.led_dong));
                    map.put("opbnt", Integer.valueOf(R.drawable.add_2));
                    list.add(map);
                }
            }
        }
        return list;
    }

    private void RefreshDevList() {
        this.mData = getData();
        this.mAdapter.notifyDataSetChanged();
    }

    private void SetTongDongVal(int did, int v1, int v2) {
        byte[] data = new byte[]{(byte) v1, (byte) v2};
        Bundle bundle = new Bundle();
        bundle.putSerializable("DATA", data);
        Intent i = new Intent(AppConfig.SEND_CMD_TODEV);
        i.putExtra("DEVID", did);
        i.putExtra("CODE", 10);
        i.putExtras(bundle);
        this.mContext.sendBroadcast(i);
    }

    @SuppressLint("WrongConstant")
    private void BindDevices(String devID) {
        BindDevObj obj = UDPServer.getByChipID(devID);
        BindDevObj tmpDev = SysApp.getMe().getDevByChipid(obj.getChipid());
        if (tmpDev == null || (obj.getNetID() > 0 && obj.getNetID() != tmpDev.getNetID())) {
            obj.setUserID(SysApp.getMe().getConfig().getAccount());
            obj.setState(0);
            SysApp.getMe().addNewDevice(obj);
            this.mContext.sendBroadcast(new Intent(AppConfig.ADD_NEW_DEVICE));
            Toast.makeText(this.mContext, R.string.lable_device_bindok, 0).show();
            return;
        }
        Toast.makeText(this.mContext, R.string.lable_device_binded, 0).show();
    }

    private void UnAllRelation(String devID) {
        final BindDevObj dev = UDPServer.getByChipID(devID);
        if (dev.getNetID() > 0) {
            new Builder(this.mContext).setTitle(getResources().getString(R.string.str_relation_init)).setIcon(R.drawable.connec).setPositiveButton(R.string.str_confirm, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    SysApp.getMe().getDevices().remove(dev);
                    new NetInterface(LANDevicePage.this.mContext, LANDevicePage.this.mHandler).UnAllRelation(dev);
                    LANDevicePage.this.mContext.sendBroadcast(new Intent(AppConfig.ADD_NEW_DEVICE));
                }
            }).setNegativeButton(R.string.str_cancel, null).show();
        }
    }

    public void ListItemClick(String devID) {
        xLog.m5i(TAG, "DevID:" + devID);
        if (UDPServer.checkChipID(devID)) {
            final BindDevObj obj = UDPServer.getByChipID(devID);
            Builder builder = new Builder(this.mContext);
            View textEntryView = LayoutInflater.from(this.mContext).inflate(R.layout.view_tongdong_handle, null);
            final SeekBar bar1 = (SeekBar) textEntryView.findViewById(R.id.seekBar1);
            final SeekBar bar2 = (SeekBar) textEntryView.findViewById(R.id.seekBar2);
            if (obj.getWordPara().length > 2) {
                int v1 = ComUtils.byte2Unint(obj.getWordPara()[0]);
                int v2 = ComUtils.byte2Unint(obj.getWordPara()[1]);
                bar1.setProgress(v1);
                bar2.setProgress(v2);
            }
            bar1.setOnTouchListener(new OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == 1) {
                        LANDevicePage.this.SetTongDongVal(obj.getNetID(), bar1.getProgress(), bar2.getProgress());
                    }
                    return false;
                }
            });
            bar2.setOnTouchListener(new OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == 1) {
                        LANDevicePage.this.SetTongDongVal(obj.getNetID(), bar1.getProgress(), bar2.getProgress());
                    }
                    return false;
                }
            });
            builder.setTitle(getResources().getString(R.string.str_lable_handle_tongdong));
            builder.setView(textEntryView);
            builder.setPositiveButton(getResources().getString(R.string.str_OK), new C03337());
            builder.setNegativeButton(getResources().getString(R.string.str_cancel), new C03348());
            builder.create().show();
        }
    }

    public void dispose() {
        this.mHandler.removeCallbacks(this.timeRefresh);
    }
}
