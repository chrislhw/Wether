package com.example.chris.wether;

import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;



public class MainActivity extends AppCompatActivity {
    private SwipeRefreshLayout layout = null;
    private TextView name = null;
    private TextView tamp = null;
    private Handler handler = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {

        handler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {

                    case 0:
                        showToast("网络异常");
                        layout.setRefreshing(false);
                        break;
                    case 1:
                        showToast("请求成功");
                        try {
                            JSONObject object = new JSONObject((String) msg.obj);
                            JSONObject weather = object.getJSONObject("weatherinfo");
                            name.setText(weather.getString("city"));
                            tamp.setText(weather.getInt("temp") + "℃");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        layout.setRefreshing(false);

                        break;
                }
            }
        };
        layout = (SwipeRefreshLayout) findViewById(R.id.activity_main);
        name = (TextView) findViewById(R.id.name);
        tamp = (TextView) findViewById(R.id.tamp);
        layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                RequestQueue mQueue = Volley.newRequestQueue(MainActivity.this);
                 StringRequest result = new StringRequest("http://www.weather.com.cn/data/sk/101190801.html", new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String result = null;
                        try {
                            result = new String(response.getBytes("ISO-8859-1"),"utf-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        Message message = handler.obtainMessage();
                        message.what = 1;
                        message.obj = result;
                        Log.d("TAG",result);
                        handler.sendMessage(message);

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        handler.sendEmptyMessage(0);
                    }
                });
                mQueue.add(result);


            }
        });
    }

    private void showToast(String content) {
        Toast.makeText(MainActivity.this, content, Toast.LENGTH_SHORT).show();
    }
}
