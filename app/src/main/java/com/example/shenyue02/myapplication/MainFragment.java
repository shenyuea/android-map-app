package com.example.shenyue02.myapplication;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.maps.AMap;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.track.*;
import com.amap.api.track.query.entity.DriveMode;
import com.amap.api.track.query.entity.Point;
import com.amap.api.track.query.model.AddTerminalRequest;
import com.amap.api.track.query.model.AddTerminalResponse;
import com.amap.api.track.query.model.AddTrackResponse;
import com.amap.api.track.query.model.HistoryTrackRequest;
import com.amap.api.track.query.model.LatestPointRequest;
import com.amap.api.track.query.model.QueryTerminalRequest;
import com.amap.api.track.query.model.QueryTerminalResponse;
import com.amap.api.track.query.model.QueryTrackResponse;
import com.amap.api.track.query.model.LatestPointResponse;
import com.amap.api.track.query.model.AddTrackRequest;
import com.amap.api.track.query.model.QueryTrackRequest;
import com.amap.api.track.query.entity.Track;


import java.util.LinkedList;
import java.util.List;

public class MainFragment extends Fragment {
    MapView mMapView = null;
    private AMap aMap;

    final long serviceId = 2260;  // 这里填入你创建的服务id
    final String terminalName = "user-123";   // 唯一标识某个用户或某台设备的名称

    private long terminalId=1233;
    private long trackId;
    long tStart =0;
    private boolean uploadToTrack = true;
    //声明mlocationClient对象
    public AMapLocationClient mlocationClient;
    //声明mLocationOption对象
    public AMapLocationClientOption mLocationOption = null;

     AMapTrackClient aMapTrackClient;
    private SharedViewModel model;

    private TextView  trackIdView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        model = ViewModelProviders.of(getActivity()).get(SharedViewModel.class);
        return inflater.inflate(R.layout.activity_mainsub, container, false);
    }
    @Override
    public void onViewCreated(View view,
                              Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMapView = (MapView) getView().findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);
        aMap = mMapView.getMap();
        Toolbar toolbar = (Toolbar) getView().findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) getView().findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        final Button button5 = getView().findViewById(R.id.button5);
        button5.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(tStart == 0 ) {
                    tStart = System.currentTimeMillis();
                }else{

                    long diff = System.currentTimeMillis()-tStart;
                    double elapsedSeconds = diff / 1000.0;
                    final TextView  tv = getView().findViewById(R.id.textView4);
                    tv.setText(Double.toString(elapsedSeconds));
                }
            }
        });

        final Button button = getView().findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent myIntent = new Intent(getActivity(), coolActivity.class);
                //myIntent.putExtra("key", value); //Optional parameters
                getActivity().startActivity(myIntent);
            }
        });



        final Button button_track = getView().findViewById(R.id.floatingActionButton);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                aMapTrackClient.queryLatestPoint(new LatestPointRequest(serviceId, terminalId), new SimpleOnTrackListener() {


                    @Override
                    public void onLatestPointCallback(LatestPointResponse latestPointResponse) {
                        if (latestPointResponse.isSuccess()) {
                            Point point = latestPointResponse.getLatestPoint().getPoint();
                            Log.d("ttttttttt", point.toString());
                            // 查询实时位置成功，point为实时位置信息
                        } else {
                            // 查询实时位置失败
                        }
                    }
                });

            }
        });

         trackIdView  = getView().findViewById(R.id.textView3);


        MyLocationStyle locationStyle = new MyLocationStyle();
        locationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher_background));
        locationStyle.strokeColor(Color.BLUE);
        locationStyle.strokeWidth(5);
        aMap.setMyLocationStyle(locationStyle);


        aMap.getUiSettings().setMyLocationButtonEnabled(true);//设置默认定位按钮是否显示
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        // 设置定位的类型为定位模式，参见类AMap。

        aMap.setMyLocationEnabled(true);
        aMapTrackClient = new AMapTrackClient(getContext());
        //track related
        aMapTrackClient.setInterval(5, 30);


        startTrack();


    }

    private void startGatherTrack(){

        aMapTrackClient.setTrackId(trackId);
        aMapTrackClient.startGather(onTrackListener);

    }




    public void onStartActi(View v){


    }
    private OnTrackLifecycleListener onTrackListener = new SimpleOnTrackLifecycleListener() {
        @Override
        public void onBindServiceCallback(int status, String msg) {
            Log.w("this ", "onBindServiceCallback, status: " + status + ", msg: " + msg);
        }

        @Override
        public void onStartTrackCallback(int status, String msg) {
            if (status == ErrorCode.TrackListen.START_TRACK_SUCEE || status == ErrorCode.TrackListen.START_TRACK_SUCEE_NO_NETWORK) {
                // 成功启动
                Toast.makeText(getActivity(), "启动服务成功", Toast.LENGTH_SHORT).show();
                Log.d("track 3", "start track success");
                startGatherTrack();
                //now can start track gathering

            } else if (status == ErrorCode.TrackListen.START_TRACK_ALREADY_STARTED) {
                // 已经启动
                Toast.makeText(getActivity(), "服务已经启动", Toast.LENGTH_SHORT).show();
                Log.d("track 3", "track already started ");

            } else {
                Log.w("this ", "error onStartTrackCallback, status: " + status + ", msg: " + msg);
                Toast.makeText(getActivity(),
                        "error onStartTrackCallback, status: " + status + ", msg: " + msg,
                        Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onStopTrackCallback(int status, String msg) {
            if (status == ErrorCode.TrackListen.STOP_TRACK_SUCCE) {
                // 成功停止
                Toast.makeText(getActivity(), "停止服务成功", Toast.LENGTH_SHORT).show();

            } else {
                Log.w("this", "error onStopTrackCallback, status: " + status + ", msg: " + msg);
                Toast.makeText(getActivity(),
                        "error onStopTrackCallback, status: " + status + ", msg: " + msg,
                        Toast.LENGTH_LONG).show();

            }
        }

        @Override
        public void onStartGatherCallback(int status, String msg) {
            if (status == ErrorCode.TrackListen.START_GATHER_SUCEE) {
                Toast.makeText(getActivity(), "定位采集开启成功", Toast.LENGTH_SHORT).show();
                Log.d("track 4", "gather started");

            } else if (status == ErrorCode.TrackListen.START_GATHER_ALREADY_STARTED) {
                Toast.makeText(getActivity(), "定位采集已经开启", Toast.LENGTH_SHORT).show();

            } else {
                Log.w("this ", "error onStartGatherCallback, status: " + status + ", msg: " + msg);
                Toast.makeText(getActivity(),
                        "error onStartGatherCallback, status: " + status + ", msg: " + msg,
                        Toast.LENGTH_LONG).show();
                Log.d("track 4", "gather failed ");

            }
        }

        @Override
        public void onStopGatherCallback(int status, String msg) {
            if (status == ErrorCode.TrackListen.STOP_GATHER_SUCCE) {
                Toast.makeText(getActivity(), "定位采集停止成功", Toast.LENGTH_SHORT).show();

            } else {
                Log.w("thsi", "error onStopGatherCallback, status: " + status + ", msg: " + msg);
                Toast.makeText(getActivity(),
                        "error onStopGatherCallback, status: " + status + ", msg: " + msg,
                        Toast.LENGTH_LONG).show();
            }
        }
    };
    //这是第一步， 先要 开启track 然后再开启收集
    private void startTrack() {
        // 先根据Terminal名称查询Terminal ID，如果Terminal还不存在，就尝试创建，拿到Terminal ID后，
        // 用Terminal ID开启轨迹服务
        aMapTrackClient.queryTerminal(new QueryTerminalRequest(Constants.SERVICE_ID, Constants.TERMINAL_NAME), new SimpleOnTrackListener() {
            @Override
            public void onQueryTerminalCallback(QueryTerminalResponse queryTerminalResponse) {
                if (queryTerminalResponse.isSuccess()) {
                    if (queryTerminalResponse.isTerminalExist()) {
                        // 当前终端已经创建过，直接使用查询到的terminal id
                        terminalId = queryTerminalResponse.getTid();
                        model.setTerminalId(terminalId);
                        Log.d("track 1", "terminal exist id is "+ terminalId);
                        if (uploadToTrack) {
                            aMapTrackClient.addTrack(new AddTrackRequest(Constants.SERVICE_ID, terminalId), new SimpleOnTrackListener() {
                                @Override
                                public void onAddTrackCallback(AddTrackResponse addTrackResponse) {
                                    if (addTrackResponse.isSuccess()) {
                                        // trackId需要在启动服务后设置才能生效，因此这里不设置，而是在startGather之前设置了track id
                                        trackId = addTrackResponse.getTrid();
                                        trackIdView.setText(Long.toString(trackId));
                                        model.select(trackId);
                                        Log.d("track 2", "track add success id is "+trackId);
                                        TrackParam trackParam = new TrackParam(Constants.SERVICE_ID, terminalId);
                                        aMapTrackClient.startTrack(trackParam, onTrackListener);
                                    } else {
                                        Toast.makeText(getActivity(), "网络请求失败，" + addTrackResponse.getErrorMsg(), Toast.LENGTH_SHORT).show();
                                        Log.d("track 2", "track add failed ");
                                    }
                                }
                            });
                        } else {
                            // 不指定track id，上报的轨迹点是该终端的散点轨迹
                            TrackParam trackParam = new TrackParam(Constants.SERVICE_ID, terminalId);

                            aMapTrackClient.startTrack(trackParam, onTrackListener);
                        }
                    } else {
                        // 当前终端是新终端，还未创建过，创建该终端并使用新生成的terminal id
                        aMapTrackClient.addTerminal(new AddTerminalRequest(Constants.TERMINAL_NAME, Constants.SERVICE_ID), new SimpleOnTrackListener() {
                            @Override
                            public void onCreateTerminalCallback(AddTerminalResponse addTerminalResponse) {
                                if (addTerminalResponse.isSuccess()) {
                                    terminalId = addTerminalResponse.getTid();
                                    TrackParam trackParam = new TrackParam(Constants.SERVICE_ID, terminalId);

                                    aMapTrackClient.startTrack(trackParam, onTrackListener);
                                } else {
                                    Toast.makeText(getActivity(), "网络请求失败，" + addTerminalResponse.getErrorMsg(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                } else {
                    Toast.makeText(getActivity(), "网络请求失败，" + queryTerminalResponse.getErrorMsg(), Toast.LENGTH_SHORT).show();
                    Log.d("track 0", "query termnal failed");
                }
            }
        });
    }



    //query history track

    HistoryTrackRequest historyTrackRequest = new HistoryTrackRequest(
            Constants.SERVICE_ID,
            terminalId,
            System.currentTimeMillis() - 12 * 60 * 60 * 1000,
            System.currentTimeMillis(),
            0,      // 不绑路
            0,      // 不做距离补偿
            5000,   // 距离补偿阈值，只有超过5km的点才启用距离补偿
            0,  // 由旧到新排序
            1,  // 返回第1页数据
            100,    // 一页不超过100条
            ""  // 暂未实现，该参数无意义，请留空
    );








    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mMapView.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mMapView.onResume();
    }
    @Override
    public void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mMapView.onPause();
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mMapView.onSaveInstanceState(outState);
    }
}
