package xyz.codewithcoffee.cysapp;

import xyz.codewithcoffee.cyc_app.R;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class AppBlocking extends AppCompatActivity {

    private static final String TAG = "APP_BLOCK";
    private AppBlocking.AppBlockListAdapter dataAdapter = null;
    private ArrayList<App> applist = null;
    private ListView listView = null;
    private static final int ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE = 1764;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(!MainActivity.isAccessibilityServiceEnabled(this, AppMonitorService.class)){
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_instructions_page);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    // request permission via start activity for result
                    startActivity(intent);
                    finish();
                }
            },5000);
            onDisplayPopupPermission();
        }
        else
        {
            Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_app_blocking);
            setOnclicks();
        }
    }

    private void onDisplayPopupPermission() {
        if (!isMIUI()) {

            Context this_context1 = this;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(AppBlocking.this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                intent.setData(Uri.parse("package:" + getApplicationContext().getPackageName()));
                startActivityForResult(intent, ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE);
            } else {
                Log.d(TAG, "Application Already has Overlay Permission");
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.canDrawOverlays(this)) {
                    Toast.makeText(this_context1, "Allow display pop up over apps permission!", Toast.LENGTH_SHORT).show();
                    Intent myIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                    startActivity(myIntent);
                }
            }
            return;

        }
        try{
            Intent intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
            intent.setClassName("com.miui.securitycenter",
                    "com.miui.permcenter.permissions.PermissionsEditorActivity");
            intent.putExtra("extra_pkgname", getPackageName());
            startActivityForResult(intent, ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE);

            return;
        }
        catch (Exception ignore){
        }
        try {
            // MIUI 8
            Intent localIntent = new Intent("miui.intent.action.APP_PERM_EDITOR");
            localIntent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.PermissionsEditorActivity");
            localIntent.putExtra("extra_pkgname", getPackageName());
            startActivity(localIntent);
            return ;
        } catch (Exception ignore) {
        }
        try {
            // MIUI 5/6/7
            Intent localIntent = new Intent("miui.intent.action.APP_PERM_EDITOR");
            localIntent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.AppPermissionsEditorActivity");
            localIntent.putExtra("extra_pkgname", getPackageName());
            startActivity(localIntent);
            return;
        }
        catch (Exception ignore) {
        }
        try{
            //  Intent overlays=new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,Uri.parse("package:"+getPackageName()));
            //  startActivityForResult(overlays,OVERLAY_REQUEST_CODE);
            Intent myIntent = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                myIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            }
            startActivity(myIntent);
        }
      /* try{
          //ActivityResultLauncher<String> requestPermissionLauncher=registerForActivityResult(new requestMultiplePermissions(), isGranted ->{
              if(isGranted){
                  Toast.makeText(this_context1, "displaying over apps now!", Toast.LENGTH_SHORT).show();
          }
              else{
                  Toast.makeText(this_context1, "Please enable permissions!", Toast.LENGTH_SHORT).show();
              }
            });
        }*/
        catch (Exception ignore) {
        }
        // Otherwise jump to application details
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }


    private boolean isMIUI(){
        String device = android.os.Build.MANUFACTURER;
        if (device.equals("Xiaomi") || device.equals("realme") || device.equals("xiaomi")) {
            try{
                Intent intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
                intent.setClassName("com.miui.securitycenter",
                        "com.miui.permcenter.permissions.PermissionsEditorActivity");
                intent.putExtra("extra_pkgname", getPackageName());
                startActivityForResult(intent, ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE);

            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                Properties prop = new Properties();
                FileInputStream pip = new FileInputStream(new File(Environment.getRootDirectory(), "build.prop"));
                prop.load(pip);
                return prop.getProperty("ro.miui.ui.version.code", null) != null
                        || prop.getProperty("ro.miui.ui.version.name", null) != null
                        || prop.getProperty("ro.miui.internal.storage", null) != null;
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
        return false;
    }

    private void setOnclicks()
    {
        Context this_context = this;
        Button refresh = findViewById(R.id.refresh_applist);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getAppList();
                Toast.makeText(this_context, "Refreshed App List", Toast.LENGTH_SHORT).show();
                dataAdapter = new AppBlocking.AppBlockListAdapter(this_context,
                        R.layout.listitem, applist);
                listView.setAdapter(dataAdapter);
            }
        });
        applist = new ArrayList<>();
        dataAdapter = new AppBlocking.AppBlockListAdapter(this,
                R.layout.listitem, applist);
        listView = (ListView) findViewById(R.id.blocked_apps);
        listView.setAdapter(dataAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                App pi = (App) parent.getItemAtPosition(position);
                Toast.makeText(getApplicationContext(),
                        pi.getName()+" has version "+pi.getVersion(),
                        Toast.LENGTH_SHORT).show();
            }
        });
        Button bblock = (Button)findViewById(R.id.block_app_butt);
        bblock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isMyServiceRunning(AppMonitorService.class))
                {
                    stopService(new Intent(AppBlocking.this, AppMonitorService.class));
                }
                Toast.makeText(this_context, "Blocked selected Apps", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(AppBlocking.this, AppMonitorService.class);
                intent.putExtra("applist", dataAdapter.blist);
                startService(intent);
            }
        });
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void getAppList()
    {
        PackageManager packageManager = getPackageManager();
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        ArrayList<PackageInfo> finlist = new ArrayList<>();
        List<PackageInfo> packs = packageManager.getInstalledPackages(0);
        for (int i = 0; i < packs.size(); i++) {
            PackageInfo p = packs.get(i);
            ApplicationInfo a = p.applicationInfo;
            // skip system apps if they shall not be included
            /*if ((a.flags & ApplicationInfo.FLAG_SYSTEM) == 1) {
                Log.d(TAG,"SYS: "+p.packageName);
                continue;
            }*/
            Log.d(TAG,p.packageName);
            finlist.add(p);
        }
        applist.clear();
        for(PackageInfo pi : finlist)
        {
            App pp = new App();
            pp.setName(pi.applicationInfo.loadLabel(getPackageManager()).toString());
            pp.setCode(pi.packageName);
            pp.setVersion(pi.versionName);
            pp.setIcon(pi.applicationInfo.loadIcon(getPackageManager()));
            pp.setSelected(false);
            applist.add(pp);
        }
    }

    private void getAppList2() throws PackageManager.NameNotFoundException {
        final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        // get list of all the apps installed
        List<ResolveInfo> ril = getPackageManager().queryIntentActivities(mainIntent, 0);
        List<String> componentList = new ArrayList<String>();
        String name = null;
        int i = 0;

        // get size of ril and create a list
        ArrayList<String> apps = new ArrayList<>();
        for (ResolveInfo ri : ril) {
            if (ri.activityInfo != null) {
                // get package
                Resources res = getPackageManager().getResourcesForApplication(ri.activityInfo.applicationInfo);
                // if activity label res is found
                if (ri.activityInfo.labelRes != 0) {
                    name = res.getString(ri.activityInfo.labelRes);
                } else {
                    name = ri.activityInfo.applicationInfo.loadLabel(
                            getPackageManager()).toString();
                }
                apps.add(name);
                i++;
            }
        }
    }

    public boolean checkAccessibilityPermission () {
        int accessEnabled = 0;
        try {
            accessEnabled = Settings.Secure.getInt(this.getContentResolver(), Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        // if not construct intent to request permission
        /*Intent intent=new Intent(WebsiteBlocking.this,InstructionsPage.class);
            startActivity(intent);*/
        /*Settings.Secure.putString(getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES, "xyz.codewithcoffee.cyc_app/UrlInterceptorService");
            Settings.Secure.putString(getContentResolver(),
                    Settings.Secure.ACCESSIBILITY_ENABLED, "1");*/
        return accessEnabled != 0;
    }


    private class AppBlockListAdapter extends ArrayAdapter<App> {

        private final ArrayList<App> apparray;
        private final ArrayList<String> blist;

        public AppBlockListAdapter(Context context, int textViewResourceId,
                                   ArrayList<App> aalist) {
            super(context, textViewResourceId, aalist);
            this.apparray = new ArrayList<App>();
            this.apparray.addAll(aalist);
            blist = new ArrayList<>();
        }

        private class ViewHolder {
//            TextView code;
            CheckBox name;
            ImageView icon;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            AppBlocking.AppBlockListAdapter.ViewHolder holder = null;
            Log.v("ConvertView", String.valueOf(position));

            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater)getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.listitem, null);

                holder = new AppBlocking.AppBlockListAdapter.ViewHolder();
//                holder.code = (TextView) convertView.findViewById(R.id.code);
                holder.name = (CheckBox) convertView.findViewById(R.id.site_check);
                holder.icon = (ImageView) convertView.findViewById(R.id.icon);
                convertView.setTag(holder);

                holder.name.setOnClickListener( new View.OnClickListener() {
                    public void onClick(View v) {
                        CheckBox cb = (CheckBox) v ;
                        App pp = (App) cb.getTag();
                        //PackageInfo pi = pp.getPackageInfo();
                        String status = (cb.isChecked() ? "Marked " : "Unmarked ") + cb.getText() + " for Blocking";
                        pp.setSelected(cb.isChecked());
                        if(cb.isChecked())
                        {
                            blist.add(pp.getCode());
                        }
                        else
                        {
                            blist.remove(pp.getCode());
                        }
                        Toast.makeText(getApplicationContext(),
                                status,
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
            else {
                holder = (AppBlocking.AppBlockListAdapter.ViewHolder) convertView.getTag();
            }

            App pp = apparray.get(position);
//            holder.code.setText(" [" +  pp.getCode() + "]");
            holder.name.setText(pp.getName());
            holder.name.setChecked(pp.isSelected());
            holder.icon.setImageDrawable(pp.getIcon());
            holder.name.setTag(pp);

            return convertView;

        }

    }
}