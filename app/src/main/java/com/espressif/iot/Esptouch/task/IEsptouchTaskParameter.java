package com.espressif.iot.Esptouch.task;

public interface IEsptouchTaskParameter {
    int getEsptouchResultIpLen();

    int getEsptouchResultMacLen();

    int getEsptouchResultOneLen();

    int getEsptouchResultTotalLen();

    int getExpectTaskResultCount();

    long getIntervalDataCodeMillisecond();

    long getIntervalGuideCodeMillisecond();

    int getPortListening();

    String getTargetHostname();

    int getTargetPort();

    int getThresholdSucBroadcastCount();

    long getTimeoutDataCodeMillisecond();

    long getTimeoutGuideCodeMillisecond();

    long getTimeoutTotalCodeMillisecond();

    int getTotalRepeatTime();

    int getWaitUdpReceivingMillisecond();

    int getWaitUdpSendingMillisecond();

    int getWaitUdpTotalMillisecond();

    void setExpectTaskResultCount(int i);

    void setWaitUdpTotalMillisecond(int i);
}
