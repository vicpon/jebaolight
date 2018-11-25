package com.espressif.iot.Esptouch.task;

public class EsptouchTaskParameter implements IEsptouchTaskParameter {
    private static int _datagramCount = 0;
    private int mEsptouchResultIpLen = 4;
    private int mEsptouchResultMacLen = 6;
    private int mEsptouchResultOneLen = 1;
    private int mEsptouchResultTotalLen = 11;
    private int mExpectTaskResultCount = 1;
    private long mIntervalDataCodeMillisecond = 8;
    private long mIntervalGuideCodeMillisecond = 8;
    private int mPortListening = 18266;
    private int mTargetPort = 7001;
    private int mThresholdSucBroadcastCount = 1;
    private long mTimeoutDataCodeMillisecond = 4000;
    private long mTimeoutGuideCodeMillisecond = 2000;
    private int mTotalRepeatTime = 1;
    private int mWaitUdpReceivingMilliseond = 15000;
    private int mWaitUdpSendingMillisecond = 45000;

    private static int __getNextDatagramCount() {
        int i = _datagramCount;
        _datagramCount = i + 1;
        return (i % 100) + 1;
    }

    public long getIntervalGuideCodeMillisecond() {
        return this.mIntervalGuideCodeMillisecond;
    }

    public long getIntervalDataCodeMillisecond() {
        return this.mIntervalDataCodeMillisecond;
    }

    public long getTimeoutGuideCodeMillisecond() {
        return this.mTimeoutGuideCodeMillisecond;
    }

    public long getTimeoutDataCodeMillisecond() {
        return this.mTimeoutDataCodeMillisecond;
    }

    public long getTimeoutTotalCodeMillisecond() {
        return this.mTimeoutGuideCodeMillisecond + this.mTimeoutDataCodeMillisecond;
    }

    public int getTotalRepeatTime() {
        return this.mTotalRepeatTime;
    }

    public int getEsptouchResultOneLen() {
        return this.mEsptouchResultOneLen;
    }

    public int getEsptouchResultMacLen() {
        return this.mEsptouchResultMacLen;
    }

    public int getEsptouchResultIpLen() {
        return this.mEsptouchResultIpLen;
    }

    public int getEsptouchResultTotalLen() {
        return this.mEsptouchResultTotalLen;
    }

    public int getPortListening() {
        return this.mPortListening;
    }

    public String getTargetHostname() {
        int count = __getNextDatagramCount();
        return "234." + count + "." + count + "." + count;
    }

    public int getTargetPort() {
        return this.mTargetPort;
    }

    public int getWaitUdpReceivingMillisecond() {
        return this.mWaitUdpReceivingMilliseond;
    }

    public int getWaitUdpSendingMillisecond() {
        return this.mWaitUdpSendingMillisecond;
    }

    public int getWaitUdpTotalMillisecond() {
        return this.mWaitUdpReceivingMilliseond + this.mWaitUdpSendingMillisecond;
    }

    public int getThresholdSucBroadcastCount() {
        return this.mThresholdSucBroadcastCount;
    }

    public void setWaitUdpTotalMillisecond(int waitUdpTotalMillisecond) {
        if (((long) waitUdpTotalMillisecond) < ((long) this.mWaitUdpReceivingMilliseond) + getTimeoutTotalCodeMillisecond()) {
            throw new IllegalArgumentException("waitUdpTotalMillisecod is invalid, it is less than mWaitUdpReceivingMilliseond + getTimeoutTotalCodeMillisecond()");
        }
        this.mWaitUdpSendingMillisecond = waitUdpTotalMillisecond - this.mWaitUdpReceivingMilliseond;
    }

    public int getExpectTaskResultCount() {
        return this.mExpectTaskResultCount;
    }

    public void setExpectTaskResultCount(int expectTaskResultCount) {
        this.mExpectTaskResultCount = expectTaskResultCount;
    }
}
