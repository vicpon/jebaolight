package com.espressif.iot.Esptouch;

import android.content.Context;
import com.espressif.iot.Esptouch.task.EsptouchTaskParameter;
import com.espressif.iot.Esptouch.task.IEsptouchTaskParameter;
import com.espressif.iot.Esptouch.task.__EsptouchTask;
import java.util.List;

public class EsptouchTask implements IEsptouchTask {
    public __EsptouchTask _mEsptouchTask;
    private IEsptouchTaskParameter _mParameter;

    public EsptouchTask(String apSsid, String apBssid, String apPassword, Context context) {
        this._mParameter = new EsptouchTaskParameter();
        this._mEsptouchTask = new __EsptouchTask(apSsid, apBssid, apPassword, context, this._mParameter, true);
    }

    public EsptouchTask(String apSsid, String apBssid, String apPassword, boolean isSsidHidden, Context context) {
        this(apSsid, apBssid, apPassword, context);
    }

    public EsptouchTask(String apSsid, String apBssid, String apPassword, int timeoutMillisecond, Context context) {
        this._mParameter = new EsptouchTaskParameter();
        this._mParameter.setWaitUdpTotalMillisecond(timeoutMillisecond);
        this._mEsptouchTask = new __EsptouchTask(apSsid, apBssid, apPassword, context, this._mParameter, true);
    }

    public EsptouchTask(String apSsid, String apBssid, String apPassword, boolean isSsidHidden, int timeoutMillisecond, Context context) {
        this(apSsid, apBssid, apPassword, context);
    }

    public void interrupt() {
        this._mEsptouchTask.interrupt();
    }

    public IEsptouchResult executeForResult() throws RuntimeException {
        return this._mEsptouchTask.executeForResult();
    }

    public boolean isCancelled() {
        return this._mEsptouchTask.isCancelled();
    }

    public List<IEsptouchResult> executeForResults(int expectTaskResultCount) throws RuntimeException {
        if (expectTaskResultCount <= 0) {
            expectTaskResultCount = Integer.MAX_VALUE;
        }
        return this._mEsptouchTask.executeForResults(expectTaskResultCount);
    }

    public void setEsptouchListener(IEsptouchListener esptouchListener) {
        this._mEsptouchTask.setEsptouchListener(esptouchListener);
    }
}
