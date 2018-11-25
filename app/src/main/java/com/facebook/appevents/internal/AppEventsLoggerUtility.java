package com.facebook.appevents.internal;

import android.content.Context;
import android.support.v4.app.NotificationCompat;
import com.facebook.LoggingBehavior;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.internal.AttributionIdentifiers;
import com.facebook.internal.Logger;
import com.facebook.internal.Utility;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;

public class AppEventsLoggerUtility {
    private static final Map<GraphAPIActivityType, String> API_ACTIVITY_TYPE_TO_STRING = new C03921();

    /* renamed from: com.facebook.appevents.internal.AppEventsLoggerUtility$1 */
    static class C03921 extends HashMap<GraphAPIActivityType, String> {
        C03921() {
            put(GraphAPIActivityType.MOBILE_INSTALL_EVENT, "MOBILE_APP_INSTALL");
            put(GraphAPIActivityType.CUSTOM_APP_EVENTS, "CUSTOM_APP_EVENTS");
        }
    }

    public enum GraphAPIActivityType {
        MOBILE_INSTALL_EVENT,
        CUSTOM_APP_EVENTS
    }

    public static JSONObject getJSONObjectForGraphAPICall(GraphAPIActivityType activityType, AttributionIdentifiers attributionIdentifiers, String anonymousAppDeviceGUID, boolean limitEventUsage, Context context) throws JSONException {
        JSONObject publishParams = new JSONObject();
        publishParams.put(NotificationCompat.CATEGORY_EVENT, API_ACTIVITY_TYPE_TO_STRING.get(activityType));
        String externalAnalyticsUserId = AppEventsLogger.getUserID();
        if (externalAnalyticsUserId != null) {
            publishParams.put("app_user_id", externalAnalyticsUserId);
        }
        Utility.setAppEventAttributionParameters(publishParams, attributionIdentifiers, anonymousAppDeviceGUID, limitEventUsage);
        try {
            Utility.setAppEventExtendedDeviceInfoParameters(publishParams, context);
        } catch (Exception e) {
            Logger.log(LoggingBehavior.APP_EVENTS, "AppEvents", "Fetching extended device info parameters failed: '%s'", e.toString());
        }
        publishParams.put("application_package_name", context.getPackageName());
        return publishParams;
    }
}
