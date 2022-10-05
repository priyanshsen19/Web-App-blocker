package xyz.codewithcoffee.cysapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Locale;
import xyz.codewithcoffee.cyc_app.R;
public class WebsiteBlocking extends AppCompatActivity {

    private WebBlockListAdapter dataAdapter = null;
    private ArrayList<Site> SiteList = null;
    private ListView listView = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(!MainActivity.isAccessibilityServiceEnabled(this,UrlInterceptorService.class)){
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
        }
        else
        {
            Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_website_blocking);
            SiteList = new ArrayList<Site>();
            Intent intent = new Intent(WebsiteBlocking.this,UrlInterceptorService.class);
            intent.putExtra("blocklist",SiteList);
            startService(intent);
            funcInit();
        }
    }

    public boolean checkAccessibilityPermission () {
        int accessEnabled = 0;
        try {
            accessEnabled = Settings.Secure.getInt(this.getContentResolver(), Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        if (accessEnabled == 0) {
            // if not construct intent to request permission
            /*Intent intent=new Intent(WebsiteBlocking.this,InstructionsPage.class);
            startActivity(intent);*/
            return false;
        } else {
            /*Settings.Secure.putString(getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES, "xyz.codewithcoffee.cyc_app/UrlInterceptorService");
            Settings.Secure.putString(getContentResolver(),
                    Settings.Secure.ACCESSIBILITY_ENABLED, "1");*/
            return true;
        }
    }

    private void funcInit() {
        /*Site loc = new Site("BLOCKED","127.0.0.1",false);
        SiteList.add(loc);
        Site fb = new Site("BLOCKED","https://facebook.com",false);
        SiteList.add(fb);
        Site goog = new Site("FAILED","https://google.com",false);
        SiteList.add(goog);
        Site yt = new Site("BLOCKED","https://youtube.com",false);
        SiteList.add(yt);*/

        dataAdapter = new WebBlockListAdapter(this,
                R.layout.listitem, new ArrayList<Site>(SiteList));
        listView = (ListView) findViewById(R.id.blocked_apps);
        listView.setAdapter(dataAdapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Site Site = (Site) parent.getItemAtPosition(position);
                Toast.makeText(getApplicationContext(),
                        Site.getName()+" is currently "+Site.getCode(),
                        Toast.LENGTH_LONG).show();
            }
        });

        Context cont = this;

        Button blButt = (Button) findViewById(R.id.block_app_butt);
        blButt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                EditText et_site = (EditText) findViewById(R.id.edit_site_name);
                String sitename = et_site.getText().toString();
                if(site_qualifies(sitename)) {
                    et_site.setText("");
                    Site site = blockSite(sitename);
                    SiteList.add(site);
                    dataAdapter = new WebBlockListAdapter(cont,
                            R.layout.listitem, new ArrayList<Site>(SiteList));
                    listView = (ListView) findViewById(R.id.blocked_apps);
                    listView.setAdapter(dataAdapter);
                    StringBuffer responseText = new StringBuffer();
                    responseText.append(sitename + " was " + site.getCode().toLowerCase(Locale.ROOT) + "\n");


                    Toast.makeText(getApplicationContext(),
                            responseText, Toast.LENGTH_LONG).show();
                    Intent pushIntent = new Intent(WebsiteBlocking.this, UrlInterceptorService.class);
                    pushIntent.putExtra("blocklist", SiteList);
                    startService(pushIntent);
                }
                else
                {
                    Toast.makeText(getApplicationContext(),
                            "Invalid Site Name", Toast.LENGTH_LONG).show();
                }
            }
        });

        Button unbButt = (Button) findViewById(R.id.unblock_app_butt);
        unbButt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                StringBuffer responseText = new StringBuffer();
                responseText.append("The following sites were unblocked :\n");

                //ArrayList<Site> SiteList = dataAdapter.SiteList;
                boolean flag = true;
                ArrayList<Site> itt = new ArrayList<>(SiteList);
                for(Site site : itt){
                    if(site.isSelected()){
                        flag = false;
                        unblockSite(site);
                        SiteList.remove(site);
                        dataAdapter = new WebBlockListAdapter(cont,
                                R.layout.listitem, new ArrayList<Site>(SiteList));
                        listView = (ListView) findViewById(R.id.blocked_apps);
                        listView.setAdapter(dataAdapter);
                        responseText.append("\n" + site.getName());
                    }
                }
                if(flag) responseText = new StringBuffer("No sites were selected for unblocking");
                Toast.makeText(getApplicationContext(),
                        responseText, Toast.LENGTH_LONG).show();
                Intent pushIntent = new Intent(WebsiteBlocking.this, UrlInterceptorService.class);
                pushIntent.putExtra("blocklist", SiteList);
                startService(pushIntent);
            }
        });

    }

    private class WebBlockListAdapter extends ArrayAdapter<Site> {

        private ArrayList<Site> SiteList;

        public WebBlockListAdapter(Context context, int textViewResourceId,
                                   ArrayList<Site> SiteList) {
            super(context, textViewResourceId, SiteList);
            this.SiteList = new ArrayList<Site>();
            this.SiteList.addAll(SiteList);
        }

        private class ViewHolder {
            CheckBox name;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;
            Log.v("ConvertView", String.valueOf(position));

            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater)getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.listitem, null);

                holder = new ViewHolder();
//                holder.code = (TextView) convertView.findViewById(R.id.code);
                holder.name = (CheckBox) convertView.findViewById(R.id.site_check);
                convertView.setTag(holder);

                holder.name.setOnClickListener( new View.OnClickListener() {
                    public void onClick(View v) {
                        CheckBox cb = (CheckBox) v ;
                        Site Site = (Site) cb.getTag();
                        String status = (cb.isChecked() ? "Marked " : "Unmarked ") + cb.getText() + " for Unblocking";
                        Toast.makeText(getApplicationContext(),
                                status,
                                Toast.LENGTH_LONG).show();
                        Site.setSelected(cb.isChecked());
                    }
                });
            }
            else {
                holder = (ViewHolder) convertView.getTag();
            }

            Site Site = SiteList.get(position);
//            holder.code.setText(" (" +  Site.getCode() + ")");
            holder.name.setText(Site.getName());
            holder.name.setChecked(Site.isSelected());
            holder.name.setTag(Site);

            return convertView;

        }

    }

    //TODO : Finish following functions

    private void unblockSite(Site site){
        //PLACEHOLDER -- LATER
    }

    private Site blockSite(String name){
        Site site = new Site("BLOCKED",name,false);
        return site;
    }

    private boolean site_qualifies(String name)
    {
        if(name.equals("")) return false;
        return true;
    }
}