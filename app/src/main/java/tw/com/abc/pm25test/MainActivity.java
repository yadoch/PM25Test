package tw.com.abc.pm25test;

import android.Manifest;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private ListView LvPm25;
    private BaseAdapter ListViewAdapter;
    private String jsonString;
    private JSONArray root;
    private List<Map<String,Object>> data;
    private String[] from={"titxxxx","pm25xxx"};
    private int[] to={R.id.tvcity,R.id.tvpm25};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LvPm25=(ListView)findViewById(R.id.lvpm25);

        fetchData();

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
/*
//用 LinkedList 方式只能顯示最後一筆,且無法用lenght() 和 position 控制顯示內容

            data = new LinkedList<>();
            String city,pm25;
            for (int i=0 ; i < root.length();i++) {
                JSONObject row = root.getJSONObject(i);
                Map<String, Object> d0 = new HashMap<>();
                d0.put(from[0], row.getString("County") + row.getString("SiteName"));
                d0.put(from[1], row.getString("PM2.5"));
                data.add(d0);
            }

            for(Map<String, Object> dItem:data){
                city=dItem.get("titxxxx").toString();
                pm25=dItem.get("pm25xxx").toString();
                Log.i("geoff","city:"+city+"|PM2.5:"+pm25);
            }
*/
        } catch (JSONException e) {
            //e.printStackTrace();
            Log.i("geoff",e.toString());
        }

        LvPm25.setAdapter(new Myadapter(root));

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
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if(convertView == null){
                //convertView= LayoutInflater.from(getApplicationContext()).inflate(R.layout.layoutitem, null);
                convertView=inflater.inflate(R.layout.layoutitem,null);
                TextView tvCity =(TextView)convertView.findViewById(R.id.tvcity);
                TextView tvPm25 =(TextView)convertView.findViewById(R.id.tvpm25);

                JSONObject row = null;
                try {
                    row = MyjsonArray.getJSONObject(position);
                    String city =row.getString("County")+row.getString("SiteName");
                    String pm25 =row.getString("PM2.5");
                    tvCity.setText(city);
                    tvPm25.setText(pm25);
                    
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                /*
                for(Map<String, Object> dItem:data){
                    tvCity.setText(dItem.get("titxxxx").toString());
                    tvPm25.setText(dItem.get("pm25xxx").toString());
                }
                */
//                Log.i("geoff","root.length:"+root.length());

            }
            return convertView;
        }
    }
}

