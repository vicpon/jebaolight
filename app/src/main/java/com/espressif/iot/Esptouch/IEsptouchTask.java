package com.espressif.iot.Esptouch;

import java.util.List;

public interface IEsptouchTask {
    IEsptouchResult executeForResult() throws RuntimeException;

    List<IEsptouchResult> executeForResults(int i) throws RuntimeException;

    void interrupt();

    boolean isCancelled();

    void setEsptouchListener(IEsptouchListener iEsptouchListener);
}
