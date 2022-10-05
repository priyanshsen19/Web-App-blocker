package xyz.codewithcoffee.cysapp;

import static xyz.codewithcoffee.cysapp.MainActivity.TAG;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Browser;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;
import xyz.codewithcoffee.cyc_app.R;
public class UrlInterceptorService extends AccessibilityService {

    private ArrayList<Site> SiteList;
    private final HashMap<String, Long> previousUrlDetections = new HashMap<>();

    private Tym startTime;
    private Tym endTime;

    private void updateRestrictTime()
    {
        Tym[] val = getTymTuple(new File(this.getFilesDir(),"restrict_time.txt"));
        startTime = val[0];
        endTime = val[1];
    }

    private Tym[] getTymTuple(File myfile) {
        Tym[] val = new Tym[]{new Tym(-1,-1,""),new Tym(-1,-1,"")};
        FileReader fileReader = null;
        try {
            fileReader = new FileReader(myfile);
            BufferedReader br = new BufferedReader(fileReader);
            StringTokenizer stoken = new StringTokenizer(br.readLine());
            val[0].setHour(Integer.parseInt(stoken.nextToken()));
            val[0].setMin(Integer.parseInt(stoken.nextToken()));
            stoken = new StringTokenizer(br.readLine());
            val[1].setHour(Integer.parseInt(stoken.nextToken()));
            val[1].setMin(Integer.parseInt(stoken.nextToken()));
            return val;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (fileReader != null) {
                try {
                    fileReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        SiteList = (ArrayList<Site>) intent.getSerializableExtra("blocklist");
        Log.e("BLOCKER","-----BEGIN-----");
        Log.e("APP_BLOCK","-----FOR SERVICE TEST-----");
        for(Site site : SiteList) {
            Log.e("BLOCKER",site.getName());
        }
        updateRestrictTime();
        return START_STICKY;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onServiceConnected() {
        AccessibilityServiceInfo info = getServiceInfo();
        info.eventTypes = AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED;
        info.packageNames = packageNames();
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_VISUAL;
        //throttling of accessibility event notification
        info.notificationTimeout = 300;
        //support ids interception
        info.flags = AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS |
                AccessibilityServiceInfo.FLAG_RETRIEVE_INTERACTIVE_WINDOWS;

        this.setServiceInfo(info);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private String captureUrl(AccessibilityNodeInfo info, SupportedBrowserConfig config) {
        List<AccessibilityNodeInfo> nodes = info.findAccessibilityNodeInfosByViewId(config.addressBarId);
        if (nodes == null || nodes.size() <= 0) {
            return null;
        }

        AccessibilityNodeInfo addressBarNodeInfo = nodes.get(0);
        String url = null;
        if (addressBarNodeInfo.getText() != null) {
            url = addressBarNodeInfo.getText().toString();
        }
        addressBarNodeInfo.recycle();
        return url;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public void onAccessibilityEvent(@NonNull AccessibilityEvent event) {
        AccessibilityNodeInfo parentNodeInfo = event.getSource();
        if (parentNodeInfo == null) {
            return;
        }

        String packageName = event.getPackageName().toString();
        SupportedBrowserConfig browserConfig = null;
        for (SupportedBrowserConfig supportedConfig: getSupportedBrowsers()) {
            if (supportedConfig.packageName.equals(packageName)) {
                browserConfig = supportedConfig;
            }
        }
        //this is not supported browser, so exit
        if (browserConfig == null) {
            return;
        }

        String capturedUrl = captureUrl(parentNodeInfo, browserConfig);
        parentNodeInfo.recycle();

        //we can't find a url. Browser either was updated or opened page without url text field
        if (capturedUrl == null) {
            return;
        }

        long eventTime = event.getEventTime();
        String detectionId = packageName + ", and url " + capturedUrl;
        //noinspection ConstantConditions
        long lastRecordedTime = previousUrlDetections.containsKey(detectionId) ? previousUrlDetections.get(detectionId) : 0;
        //some kind of redirect throttling
        if (eventTime - lastRecordedTime > 2000) {
            previousUrlDetections.put(detectionId, eventTime);
            analyzeCapturedUrl(capturedUrl, browserConfig.packageName);
        }
    }
    //TODO Change this func
    private void analyzeCapturedUrl(@NonNull String capturedUrl, @NonNull String browserPackage) {
        updateRestrictTime();
        if(!Timetable.banTime(startTime,endTime))
        {
            Log.d(TAG,"WEB NOT VALID TIME");
            return;
        }
        else
        {
            Log.d(TAG,"WEB VALID TIME");
        }
        String redirectUrl = "https://google.com";
        boolean block = false;
        for(Site e : SiteList)
        {
            if(capturedUrl.toLowerCase().contains(e.getName().toLowerCase()))
            {
                Log.e("BLOCKER","Blocked "+e.getName());
                block = true;
                break;
            }
        }
        if (block) {
            performRedirect(redirectUrl, browserPackage);
            Intent intent=new Intent(this, WebBlockingOverlay.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    /** we just reopen the browser app with our redirect url using service context
     * We may use more complicated solution with invisible activity to send a simple intent to open the url */
    private void performRedirect(@NonNull String redirectUrl, @NonNull String browserPackage) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(redirectUrl));
            intent.setPackage(browserPackage);
            intent.putExtra(Browser.EXTRA_APPLICATION_ID, browserPackage);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        }
        catch(ActivityNotFoundException e) {
            // the expected browser is not installed
            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(redirectUrl));
            startActivity(i);
        }
    }

    @Override
    public void onInterrupt() { }

    @NonNull
    private static String[] packageNames() {
        List<String> packageNames = new ArrayList<>();
        for (SupportedBrowserConfig config: getSupportedBrowsers()) {
            packageNames.add(config.packageName);
        }
        return packageNames.toArray(new String[0]);
    }

    private static class SupportedBrowserConfig {
        public String packageName, addressBarId;
        public SupportedBrowserConfig(String packageName, String addressBarId) {
            this.packageName = packageName;
            this.addressBarId = addressBarId;
        }
    }

    /** @return a list of supported browser configs
     * This list could be instead obtained from remote server to support future browser updates without updating an app */
    @NonNull
    private static List<SupportedBrowserConfig> getSupportedBrowsers() {
        List<SupportedBrowserConfig> browsers = new ArrayList<>();
        browsers.add( new SupportedBrowserConfig("com.android.chrome", "com.android.chrome:id/url_bar"));
        browsers.add( new SupportedBrowserConfig("org.mozilla.firefox", "org.mozilla.firefox:id/url_bar_title"));
        return browsers;
    }
}