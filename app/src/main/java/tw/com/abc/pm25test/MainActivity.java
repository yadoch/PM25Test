package tw.com.abc.pm25test;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity {
    private UIHandler handler;
    private ListView LvPm25;
    private BaseAdapter ListViewAdapter;
    private String jsonString;
    private JSONArray root;
    private List<Map<String,Object>> data;
//    private String[] from={"titxxxx","pm25xxx"};
//    private int[] to={R.id.tvcity,R.id.tvpm25};



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LvPm25=(ListView)findViewById(R.id.lvpm25);

        handler =new UIHandler();
        fetchData();
    }


   public void Refresh(View view){
       fetchData();
   //    handler.sendEmptyMessage(0);
   }

    private void fetchData() {
        new Thread(){
            @Override
            public void run() {
                super.run();
                fetchDataRunnable();
            }
        }.start();
    }

    private void fetchDataRunnable(){

        try {
            URL url = new URL("http://opendata2.epa.gov.tw/AQX.json");
            URLConnection  conn = url.openConnection();
            conn.connect();
            InputStream in=conn.getInputStream();
            InputStreamReader inputStreamReader=new InputStreamReader(in);
            BufferedReader bufferedReader= new BufferedReader(inputStreamReader);
            jsonString = bufferedReader.readLine();

        } catch (IOException e) {
            //e.printStackTrace();
            Log.i("geoff",e.toString());
        }
        try {
            root = new JSONArray(jsonString);
            for (int i=0 ; i < root.length();i++){
                JSONObject row = root.getJSONObject(i);

                String country = row.getString("County");
                String sitename = row.getString("SiteName");
                String pm25 = row.getString("PM2.5");
                Log.i("geoff",country + ":" + sitename + ":"+ pm25);

            }
        } catch (JSONException e) {
            //e.printStackTrace();
            Log.i("geoff",e.toString());
        }
        handler.sendEmptyMessage(0);
    }

    private class Myadapter extends BaseAdapter {
        private JSONArray  MyjsonArray;
        private LayoutInflater inflater;
        public Myadapter(JSONArray  jsonArray){
            this.MyjsonArray=jsonArray;
            inflater=(LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return MyjsonArray.length();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            //if(convertView == null){
                //convertView= LayoutInflater.from(getApplicationContext()).inflate(R.layout.layoutitem, null);
                convertView=inflater.inflate(R.layout.layoutitem,null);
                TextView tvCity =(TextView)convertView.findViewById(R.id.tvcity);
                TextView tvPm25 =(TextView)convertView.findViewById(R.id.tvpm25);

                JSONObject row = null;
                try {
                    row = MyjsonArray.getJSONObject(position);
                    String city =row.getString("County")+row.getString("SiteName");
                    String pm25 =row.getString("PM2.5");
                    double intpm25=Double.parseDouble(pm25);
                    tvCity.setText(city);
                    tvPm25.setText(pm25);
                    if (  intpm25 > 0 && intpm25 <= 15.4){
                        tvPm25.setBackgroundColor(Color.GREEN);
                        tvCity.setBackgroundColor(Color.GREEN);
                    }else if(intpm25 >= 15.5 && intpm25 <=35.4){
                        tvPm25.setBackgroundColor(Color.YELLOW);
                        tvCity.setBackgroundColor(Color.YELLOW);
                    }else if(intpm25 >= 35.5 && intpm25 <=54.4) {
                        tvPm25.setBackgroundColor(Color.parseColor("#FFA500"));
                        tvCity.setBackgroundColor(Color.parseColor("#FFA500"));
                    }else if(intpm25 >= 54.5 && intpm25 <=150.4) {
                        tvPm25.setBackgroundColor(Color.RED);
                        tvCity.setBackgroundColor(Color.RED);
                    }else if(intpm25 >= 150.5 && intpm25 <=250.4) {
                        tvPm25.setBackgroundColor(Color.parseColor("#800080"));
                        tvCity.setBackgroundColor(Color.parseColor("#800080"));
                    }else if(intpm25 >= 250.5) {
                        tvPm25.setBackgroundColor(Color.parseColor("#A52A2A"));
                        tvCity.setBackgroundColor(Color.parseColor("#A52A2A"));
                    }
                } catch (JSONException e) {
                    Log.i("geoff", "Exception: " + e.toString());
                    e.printStackTrace();
                } catch (Exception e) {
                    Log.i("geoff", "Exception: " + e.toString());
                    e.printStackTrace();
                }
            //}
            return convertView;
        }
    }
    private class UIHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            LvPm25.setAdapter(new Myadapter(root));
        }
    }

}

