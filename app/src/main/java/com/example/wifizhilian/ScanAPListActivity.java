package com.example.wifizhilian;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.espressif.iot.Esptouch.EspWifiAdminSimple;
import com.espressif.iot.Esptouch.EsptouchTask;
import com.espressif.iot.Esptouch.IEsptouchListener;
import com.espressif.iot.Esptouch.IEsptouchResult;
import com.espressif.iot.Esptouch.IEsptouchTask;
import com.example.wifizhilian.data.AppConfig;
import com.example.wifizhilian.data.BindDevObj;
import com.example.wifizhilian.libs.CmdAppFactory;
import com.example.wifizhilian.libs.ComUtils;
import com.example.wifizhilian.libs.HttpClient;
import com.example.wifizhilian.libs.xLog;
import com.example.wifizhilian.view.PageHead;
import com.example.wifizhilian.view.WaitDialog;
//import com.facebook.appevents.AppEventsConstants;
//import com.facebook.share.internal.ShareConstants;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONObject;
import org.json.JSONTokener;

public class ScanAPListActivity extends Activity {
    private static final String IOT_CONFIG = "IOT CONFIG";
    private static final int MSG_SCAN_FAILED = 1000;
    private static final int MSG_SCAN_NEWDEV = 1002;
    private static final int MSG_SCAN_SUCCEED = 1001;
    protected static final String TAG = "ScanAPListActivity";
    Runnable checkScnaWaiteDialog = new C02733();
    Runnable checkSetDevice = new C02722();
    private int curWifiID = 0;
    Handler handler = new C02711();
    private MyAdapter mAdapter;
    private List<Map<String, Object>> mData;
    private ListView mDevList;
    private boolean mIsWait = false;
    private String mNewSetdevName = "";
    private String mOldWifiSSID = "";
    private PageHead mPageHead;
    private WaitDialog mWaitDialog;
    private EspWifiAdminSimple mWifiAdmin;
    private int[] mWifiIcon = new int[]{R.drawable.wifi_4, R.drawable.wifi_4, R.drawable.wifi_3, R.drawable.wifi_2, R.drawable.wifi_1, R.drawable.wifi_1};
    private WifiManager mWifiManager;
    private IEsptouchListener myListener = new IEsptouchListener() {
        public void onEsptouchResultAdded(IEsptouchResult result) {
            ScanAPListActivity.this.onEsptoucResultAddedPerform(result);
        }
    };
    private String setWifiPASS;
    private String setWifiSSID;

    /* renamed from: com.example.wifizhilian.ScanAPListActivity$1 */
    class C02711 extends Handler {
        C02711() {
        }

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1000:
                    Toast.makeText(ScanAPListActivity.this, ScanAPListActivity.this.getResources().getString(R.string.str_config_failure), 0).show();
                    ScanAPListActivity.this.ConnectOldAP();
                    ScanAPListActivity.this.mWaitDialog.Close();
                    return;
                case 1001:
                    ScanAPListActivity.this.ConnectOldAP();
                    ScanAPListActivity.this.handler.post(ScanAPListActivity.this.checkSetDevice);
                    return;
                case 1002:
                    SysApp.getMe().addNewDevice((BindDevObj) msg.obj);
                    return;
                default:
                    return;
            }
        }
    }

    /* renamed from: com.example.wifizhilian.ScanAPListActivity$2 */
    class C02722 implements Runnable {
        C02722() {
        }

        public void run() {
            String newSSID = ScanAPListActivity.this.getSSID();
            if (ComUtils.StrIsEmpty(ScanAPListActivity.this.mNewSetdevName) || (!ComUtils.StrIsEmpty(newSSID) && newSSID.indexOf(ScanAPListActivity.this.mNewSetdevName) >= 0)) {
                ScanAPListActivity.this.handler.postDelayed(ScanAPListActivity.this.checkSetDevice, 1000);
                return;
            }
            ScanAPListActivity.this.RefreshList();
            ScanAPListActivity.this.sendBroadcast(new Intent(AppConfig.ADD_NEW_DEVICE));
            ScanAPListActivity.this.mNewSetdevName = "";
            ScanAPListActivity.this.mWaitDialog.Close();
            ScanAPListActivity.this.finish();
        }
    }

    /* renamed from: com.example.wifizhilian.ScanAPListActivity$3 */
    class C02733 implements Runnable {
        C02733() {
        }

        public void run() {
            if (ScanAPListActivity.this.mIsWait) {
                ScanAPListActivity.this.handler.postDelayed(ScanAPListActivity.this.checkScnaWaiteDialog, 200);
            } else {
                ScanAPListActivity.this.mWaitDialog.Close();
            }
        }
    }

    /* renamed from: com.example.wifizhilian.ScanAPListActivity$4 */
    class C02744 implements OnClickListener {
        C02744() {
        }

        public void onClick(View v) {
            ScanAPListActivity.this.finish();
        }
    }

    /* renamed from: com.example.wifizhilian.ScanAPListActivity$5 */
    class C02755 implements OnClickListener {
        C02755() {
        }

        public void onClick(View v) {
            ScanAPListActivity.this.mIsWait = true;
            ScanAPListActivity.this.handler.postDelayed(ScanAPListActivity.this.checkScnaWaiteDialog, 200);
            ScanAPListActivity.this.mWaitDialog.Show((int) R.string.str_wait_scaning);
            ScanAPListActivity.this.RefreshList();
        }
    }

    /* renamed from: com.example.wifizhilian.ScanAPListActivity$7 */
    class C02777 implements DialogInterface.OnClickListener {
        C02777() {
        }

        public void onClick(DialogInterface dialog, int whichButton) {
        }
    }

    /* renamed from: com.example.wifizhilian.ScanAPListActivity$9 */
    class C02799 implements Runnable {
        C02799() {
        }

        public void run() {
        }
    }

    private class EsptouchAsyncTask extends AsyncTask<String, Void, List<IEsptouchResult>> {
        private IEsptouchTask mEsptouchTask;
        private final Object mLock;

        private EsptouchAsyncTask() {
            this.mLock = new Object();
        }

        protected void onPreExecute() {
            ScanAPListActivity.this.mWaitDialog.Show(ScanAPListActivity.this.getResources().getString(R.string.str_wait_configing));
        }

        protected List<IEsptouchResult> doInBackground(String... params) {
            int taskResultCount;
            synchronized (this.mLock) {
                String apSsid = ScanAPListActivity.this.mWifiAdmin.getWifiConnectedSsidAscii(params[0]);
                String apBssid = params[1];
                String apPassword = params[2];
                taskResultCount = Integer.parseInt(params[3]);
                this.mEsptouchTask = new EsptouchTask(apSsid, apBssid, apPassword, ScanAPListActivity.this);
                this.mEsptouchTask.setEsptouchListener(ScanAPListActivity.this.myListener);
            }
            return this.mEsptouchTask.executeForResults(taskResultCount);
        }

        protected void onPostExecute(List<IEsptouchResult> result) {
            IEsptouchResult firstResult = (IEsptouchResult) result.get(0);
            if (!firstResult.isCancelled()) {
                int count = 0;
                if (firstResult.isSuc()) {
                    StringBuilder sb = new StringBuilder();
                    for (IEsptouchResult resultInList : result) {
                        sb.append("Esptouch success, bssid = " + resultInList.getBssid() + ",InetAddress = " + resultInList.getInetAddress().getHostAddress() + "\n");
                        count++;
                        if (count >= 5) {
                            break;
                        }
                    }
                    if (count < result.size()) {
                        sb.append("\nthere's " + (result.size() - count) + " more result(s) without showing\n");
                    }
                    ScanAPListActivity.this.mWaitDialog.Close();
                    Toast.makeText(ScanAPListActivity.this, ScanAPListActivity.this.getResources().getString(R.string.str_iotconfig_ok), 1).show();
                    return;
                }
                ScanAPListActivity.this.mWaitDialog.Close();
                Toast.makeText(ScanAPListActivity.this, ScanAPListActivity.this.getResources().getString(R.string.str_config_failure), 1).show();
            }
        }
    }

    public final class MenuItemHolder {
        public ImageView icon;
        public TextView info;
        public ImageView opbnt;
        public ImageView optowbnt;
        public TextView title;
    }

    public class MyAdapter extends BaseAdapter {
        private LayoutInflater mInflater;

        public MyAdapter(Context context) {
            this.mInflater = LayoutInflater.from(context);
        }

        public int getCount() {
            return ScanAPListActivity.this.mData.size();
        }

        public long getItemId(int arg0) {
            return 0;
        }

        public Object getItem(int index) {
            if (index < ScanAPListActivity.this.mData.size()) {
                return ScanAPListActivity.this.mData.get(index);
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
                holder.optowbnt = (ImageView) convertView.findViewById(R.id.OptionTwoimageView);
                convertView.setTag(holder);
            } else {
                holder = (MenuItemHolder) convertView.getTag();
            }
            final String titleid = (String) ((Map) ScanAPListActivity.this.mData.get(position)).get("ssid");
            holder.title.setText((String) ((Map) ScanAPListActivity.this.mData.get(position)).get("title"));
            holder.info.setText((String) ((Map) ScanAPListActivity.this.mData.get(position)).get("info"));
            holder.icon.setImageResource(Integer.parseInt(String.valueOf(((Map) ScanAPListActivity.this.mData.get(position)).get("icon"))));
            if (titleid.equals(ScanAPListActivity.IOT_CONFIG)) {
                holder.opbnt.setVisibility(View.INVISIBLE);
            } else {
                holder.opbnt.setImageResource(Integer.parseInt(String.valueOf(((Map) ScanAPListActivity.this.mData.get(position)).get("opbnt"))));
            }
            holder.optowbnt.setVisibility(View.INVISIBLE);
            convertView.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    ScanAPListActivity.this.ListItemClick(titleid);
                }
            });
            return convertView;
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_aplist);
        this.mWifiAdmin = new EspWifiAdminSimple(this);
        this.mWaitDialog = new WaitDialog(this);
        this.mWifiManager = (WifiManager) getApplicationContext().getSystemService("wifi");
        this.mPageHead = (PageHead) findViewById(R.id.MyDevicepageHead);
        this.mPageHead.LeftBnt.setImageResource(R.drawable.back);
        this.mPageHead.LeftBnt.setOnClickListener(new C02744());
        this.mPageHead.RightBnt.setImageResource(R.drawable.refresh);
        this.mPageHead.RightBnt.setOnClickListener(new C02755());
        this.mPageHead.TitleView.setText(R.string.str_title_addap);
        this.mDevList = (ListView) findViewById(R.id.ScanDevicelistView);
        this.mData = getData();
        this.mAdapter = new MyAdapter(this);
        this.mDevList.setAdapter(this.mAdapter);
        getOldWifiConfig();
    }

    private void RefreshList() {
        this.mData = getData();
        this.mAdapter.notifyDataSetChanged();
        this.mIsWait = false;
    }

    private int CheckWifiType(String ssid) {
        if ((ssid.startsWith("WIFIZL") || ssid.startsWith("JEBAO")) && ssid.indexOf("-") > 0) {
            return 1;
        }
        return 0;
    }

    private List<Map<String, Object>> getData() {
        this.mWifiManager.startScan();
        List<Map<String, Object>> list = new ArrayList();
        List<ScanResult> wifiList = this.mWifiManager.getScanResults();
        Map<String, Object> map = new HashMap();
        map.put("ssid", IOT_CONFIG);
        map.put("title", getResources().getString(R.string.str_iotconfig_wifi));
        map.put("info", getResources().getString(R.string.str_iotconfig_explain));
        map.put("icon", Integer.valueOf(R.drawable.ic_launcher));
        list.add(map);
        for (int i = 0; i < wifiList.size(); i++) {
            ScanResult result = (ScanResult) wifiList.get(i);
            if (CheckWifiType(result.SSID) > 0) {
                String name = "";
                if (result.SSID.startsWith("JEBAOJC")) {
                    name = getResources().getString(R.string.str_dev_tongdongaljac) + result.SSID.substring(result.SSID.indexOf("-") + 1);
                } else if (result.SSID.startsWith("JEBAOSL")) {
                    name = getResources().getString(R.string.str_dev_tongdong6l) + result.SSID.substring(result.SSID.indexOf("-") + 1);
                } else if (result.SSID.startsWith("JEBAOLL")) {
                    name = getResources().getString(R.string.str_dev_tongdongal150) + result.SSID.substring(result.SSID.indexOf("-") + 1);
                } else if (result.SSID.startsWith("JEBAOAP")) {
                    name = getResources().getString(R.string.str_dev_waterpumpa) + result.SSID.substring(result.SSID.indexOf("-") + 1);
                } else {
                    name = getResources().getString(R.string.str_dev_tongdong) + result.SSID.substring(result.SSID.indexOf("-") + 1);
                }
                map = new HashMap();
                map.put("ssid", result.SSID);
                map.put("title", name);
                map.put("info", result.BSSID);
                map.put("icon", Integer.valueOf(R.drawable.led_dong));
                map.put("opbnt", Integer.valueOf(this.mWifiIcon[Math.abs(result.level / 20)]));
                list.add(map);
            }
        }
        return list;
    }

    public void ListItemClick(final String titleid) {
        Builder builder = new Builder(this);
        View textEntryView = LayoutInflater.from(this).inflate(R.layout.view_connectap, null);
        final EditText apName = (EditText) textEntryView.findViewById(R.id.APNameeditText);
        final EditText apPassword = (EditText) textEntryView.findViewById(R.id.APPasseditText);
        if (!this.mOldWifiSSID.equals("")) {
            String ssid = getSSID();
            apName.setText(ssid);
            String apinfo = SysApp.getMe().getConfig().getData("APINFO");
            if (apinfo.startsWith(ssid + "|")) {
                apPassword.setText(apinfo.substring(ssid.length() + 1));
            }
        }
        builder.setIcon(R.drawable.ap_icon);
        builder.setTitle(getResources().getString(R.string.lable_input_apinfo));
        builder.setView(textEntryView);
        builder.setPositiveButton(getResources().getString(R.string.str_OK), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                ScanAPListActivity.this.setWifiSSID = apName.getText().toString();
                ScanAPListActivity.this.setWifiPASS = apPassword.getText().toString();
                if (ScanAPListActivity.this.setWifiSSID.equals("")) {
                    Toast.makeText(ScanAPListActivity.this, ScanAPListActivity.this.getResources().getString(R.string.str_input_ssid), 0).show();
                    return;
                }
                SysApp.getMe().getConfig().saveData("APINFO", ScanAPListActivity.this.setWifiSSID + "|" + ScanAPListActivity.this.setWifiPASS);
//                if (titleid.equals(ScanAPListActivity.IOT_CONFIG)) {
//                    String apBssid = ScanAPListActivity.this.mWifiAdmin.getWifiConnectedBssid();
//                    new EsptouchAsyncTask().execute(new String[]{ScanAPListActivity.this.setWifiSSID, apBssid, ScanAPListActivity.this.setWifiPASS, AppEventsConstants.EVENT_PARAM_VALUE_YES});
//                    return;
//                }
                ScanAPListActivity.this.mWaitDialog.Show((int) R.string.str_wait_configing);
                ScanAPListActivity.this.mNewSetdevName = titleid;
                ScanAPListActivity.this.ConnectRoute(ScanAPListActivity.this.setWifiSSID, ScanAPListActivity.this.setWifiPASS, ScanAPListActivity.this.mNewSetdevName);
            }
        });
        builder.setNegativeButton(getResources().getString(R.string.str_cancel), new C02777());
        builder.create().show();
    }

    private void ConnectRoute(final String apName, final String apPass, final String devName) {
        new Thread(new Runnable() {
            public void run() {
                int connTime = 0;
                ScanAPListActivity.this.connectToAP(devName, "");
                while (connTime < 5) {
                    try {
                        Thread.sleep(1000);
                        String curSSID = ScanAPListActivity.this.getSSID();
                        int curIP = ScanAPListActivity.this.getIPAddress();
                        xLog.m5i(ScanAPListActivity.TAG, "ConnectName:" + devName + "  Cur SSID:" + curSSID + "  IP:" + ScanAPListActivity.this.intToIp(curIP));
                        if (curSSID.indexOf(ScanAPListActivity.this.mNewSetdevName) < 0 || curIP <= 0) {
                            ScanAPListActivity.this.connectToAP(devName, "");
                            Thread.sleep(5000);
                            connTime++;
                        } else {
                            JSONObject device = ((JSONObject) new JSONTokener(HttpClient.get("http://192.168.4.1/client?command=info", "UTF8")).nextValue()).getJSONObject("Device");
                            int netID = device.getInt("netid");
                            int chipid = device.getInt("chipid");
                            String mac = device.getString("mac");
                            String product = device.getString("product");
                            BindDevObj dev = new BindDevObj();
                            dev.setChipid(String.valueOf(chipid));
                            dev.setMAC(mac);
                            dev.setNetID(netID);
                            dev.setProduct(product);
                            dev.setSSID(devName);
                            dev.setUserID(SysApp.getMe().getConfig().getAccount());
                            dev.setNickName("");
                            dev.setUpdateCode("");
                            dev.setState(0);
                            Message msg = new Message();
                            msg.what = 1002;
                            msg.obj = dev;
                            ScanAPListActivity.this.handler.sendMessage(msg);
                            //xLog.m5i(ShareConstants.WEB_DIALOG_PARAM_DATA, "netID:" + netID + "   chipid:" + chipid + "   mac:" + mac + "   product:" + product);
                            ByteBuffer buf = ByteBuffer.allocate((apName.length() + apPass.length()) + 2);
                            buf.put((byte) ScanAPListActivity.this.setWifiSSID.length());
                            buf.put((byte) ScanAPListActivity.this.setWifiPASS.length());
                            buf.put(apName.getBytes());
                            buf.put(apPass.getBytes());
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("DATA", CmdAppFactory.SetSSID(dev, buf.array()));
                            Intent i = new Intent(AppConfig.SEND_DATA_TOUDP);
                            i.putExtra("IPADDR", "192.168.4.1");
                            i.putExtra("PORT", 9980);
                            i.putExtras(bundle);
                            ScanAPListActivity.this.sendBroadcast(i);
                            Thread.sleep(500);
                            ScanAPListActivity.this.sendBroadcast(i);
                            ScanAPListActivity.this.ConnectOldAP();
                            ScanAPListActivity.this.handler.sendEmptyMessage(1001);
                            ScanAPListActivity.this.mWaitDialog.Close();
                            return;
                        }
                    } catch (Exception e) {
                        System.out.print(e.toString());
                    }
                }
                ScanAPListActivity.this.mWaitDialog.Close();
                ScanAPListActivity.this.handler.sendEmptyMessage(1000);
            }
        }).start();
    }

    private String intToIp(int paramInt) {
        return (paramInt & 255) + "." + ((paramInt >> 8) & 255) + "." + ((paramInt >> 16) & 255) + "." + ((paramInt >> 24) & 255);
    }

    private void ConnectOldAP() {
        if (this.mWifiManager.getConfiguredNetworks() != null) {
            this.mWifiManager.removeNetwork(this.mWifiManager.getConnectionInfo().getNetworkId());
            this.mWifiManager.disconnect();
            if (!this.mOldWifiSSID.equals("") && CheckWifiType(getSSID()) != 0) {
                for (WifiConfiguration localWifiConfiguration : this.mWifiManager.getConfiguredNetworks()) {
                    if (localWifiConfiguration.SSID.replace("\"", "").trim().equals(this.mOldWifiSSID)) {
                        this.mWifiManager.enableNetwork(this.mWifiManager.addNetwork(localWifiConfiguration), true);
                        boolean changeHappen = this.mWifiManager.saveConfiguration();
                        this.mWifiManager.setWifiEnabled(true);
                    }
                }
            }
        }
    }

    public String getSSID() {
        String ssid = this.mWifiManager.getConnectionInfo().getSSID();
        return ssid == null ? "" : ssid.replace("\"", "").trim();
    }

    public int getIPAddress() {
        return this.mWifiManager.getConnectionInfo().getIpAddress();
    }

    private void getOldWifiConfig() {
        if (this.mWifiManager.getConfiguredNetworks() != null) {
            String str = getSSID();
            if (CheckWifiType(str) != 1) {
                for (WifiConfiguration localWifiConfiguration : this.mWifiManager.getConfiguredNetworks()) {
                    String curSSID = localWifiConfiguration.SSID.replace("\"", "").trim();
                    if (curSSID.equals(str)) {
                        this.mOldWifiSSID = curSSID;
                        return;
                    }
                }
            }
        }
    }

    public boolean connectToAP(String ssid, String passkey) {
        boolean reVal = false;
        WifiConfiguration wifiConfiguration = new WifiConfiguration();
        String networkSSID = ssid;
        String networkPass = passkey;
        xLog.m5i(TAG, "connectToAP ssid:" + ssid + "  passkey:" + passkey);
        for (ScanResult result : this.mWifiManager.getScanResults()) {
            Log.i(TAG, "result.SSID:" + result.SSID);
            if (result.SSID.startsWith(networkSSID)) {
                String securityMode = getScanResultSecurity(result);
                if (securityMode.equalsIgnoreCase("OPEN")) {
                    wifiConfiguration.SSID = "\"" + networkSSID + "\"";
                    wifiConfiguration.allowedKeyManagement.set(0);
                    this.mWifiManager.enableNetwork(this.mWifiManager.addNetwork(wifiConfiguration), true);
                } else if (securityMode.equalsIgnoreCase("WEP")) {
                    wifiConfiguration.SSID = "\"" + networkSSID + "\"";
                    wifiConfiguration.wepKeys[0] = "\"" + networkPass + "\"";
                    wifiConfiguration.wepTxKeyIndex = 0;
                    wifiConfiguration.allowedKeyManagement.set(0);
                    wifiConfiguration.allowedGroupCiphers.set(0);
                    this.mWifiManager.enableNetwork(this.mWifiManager.addNetwork(wifiConfiguration), true);
                }
                if (!this.mWifiManager.isWifiEnabled()) {
                    this.mWifiManager.setWifiEnabled(true);
                }
                wifiConfiguration.SSID = "\"" + networkSSID + "\"";
                wifiConfiguration.preSharedKey = "\"" + networkPass + "\"";
                wifiConfiguration.hiddenSSID = true;
                wifiConfiguration.status = 2;
                wifiConfiguration.allowedGroupCiphers.set(2);
                wifiConfiguration.allowedGroupCiphers.set(3);
                wifiConfiguration.allowedKeyManagement.set(1);
                wifiConfiguration.allowedPairwiseCiphers.set(1);
                wifiConfiguration.allowedPairwiseCiphers.set(2);
                wifiConfiguration.allowedProtocols.set(1);
                wifiConfiguration.allowedProtocols.set(0);
                int res = this.mWifiManager.addNetwork(wifiConfiguration);
                xLog.m5i(TAG, "### 2 ### add Network returned " + res);
                this.mWifiManager.enableNetwork(res, true);
                this.mWifiManager.saveConfiguration();
                this.mWifiManager.setWifiEnabled(true);
                if (res >= 0) {
                    reVal = true;
                }
            }
        }
        return reVal;
    }

    public String getScanResultSecurity(ScanResult scanResult) {
        xLog.m5i(TAG, "* getScanResultSecurity");
        String cap = scanResult.capabilities;
        String[] securityModes = new String[]{"WEP", "PSK", "EAP"};
        for (int i = securityModes.length - 1; i >= 0; i--) {
            if (cap.contains(securityModes[i])) {
                return securityModes[i];
            }
        }
        return "OPEN";
    }

    private void onEsptoucResultAddedPerform(IEsptouchResult result) {
        runOnUiThread(new C02799());
    }
}
