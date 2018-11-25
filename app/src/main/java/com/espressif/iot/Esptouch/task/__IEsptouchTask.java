package com.espressif.iot.Esptouch.task;

import com.espressif.iot.Esptouch.IEsptouchListener;
import com.espressif.iot.Esptouch.IEsptouchResult;
import java.util.List;

public interface __IEsptouchTask {
    public static final boolean DEBUG = true;

    IEsptouchResult executeForResult() throws RuntimeException;

    List<IEsptouchResult> executeForResults(int i) throws RuntimeException;

    void interrupt();

    boolean isCancelled();

    void setEsptouchListener(IEsptouchListener iEsptouchListener);
}
