package com.lib.libs.speedtest;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.location.Location;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import com.github.anastr.speedviewlib.PointerSpeedometer;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.lib.libs.speedtest.controllers.MenuController;
import com.lib.libs.speedtest.controllers.RateDialogController;
import com.lib.libs.speedtest.controllers.support.MenuSupportController;
import com.lib.libs.speedtest.models.HistoryItem;
import com.lib.libs.speedtest.test.HttpDownloadTest;
import com.lib.libs.speedtest.test.HttpUploadTest;
import com.lib.libs.speedtest.test.PingTest;
import com.lib.libs.speedtest.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import pl.bclogic.pulsator4droid.library.PulsatorLayout;

public class MainActivity extends AppCompatActivity {

    private static final int SOCKET_TIMEOUT = 10000;
    //Private fields
    private static final String TAG = MainActivity.class.getSimpleName();

    private PulsatorLayout mPulsator;
    private HistoryItem historyItem;

    private ViewGroup mStartButtonFrame;
    private ViewGroup mSetupFrame;
    private ViewGroup mProgressFrame;
    private ViewGroup mCompletionFrame;
    private ViewGroup menuFrame;
    private ViewGroup mSpeedtestFrame;
    private ViewGroup mWifiAnalyzerFrame;

    private PointerSpeedometer mPointerSpeedometer;
    private PointerSpeedometer mWifiView;

    private View mBackButton;
    private View mBtnStart;
    private View mMenuButton;
    private View mBoxDataLine;
    private View mLinearPingType;

    private View mToolbar;
    private ImageView mSpeedtestTool;
    private ImageView mWifiAnalyzerTool;

    private TextView mPing;
    private TextView mMeaningDown;
    private TextView mMeaningUp;
    private TextView mHostLinearText;
    private TextView mSetupText;
    private TextView mTextTitle;
    private TextView mRateTheApp;

    private LineChart chart;
    private List<Float> listData;

    private MenuController menuController;

    private Thread progressThread;
    private volatile boolean stopThread;

    private FrameGroup currentFrame;
    private FrameGroup currentSuperFrame;

    private HashMap<String, Double> hostsMap = new HashMap<>();
    private String host;


    Timer timer;

    GetSpeedTestHostsHandler getSpeedTestHostsHandler = null;
    HashSet<String> tempBlackList;
    private final DecimalFormat dec = new DecimalFormat("#.#");



    @Override
    public void onResume() {
        super.onResume();

        getSpeedTestHostsHandler = new GetSpeedTestHostsHandler();
        getSpeedTestHostsHandler.start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Request the progress bar to be shown in the title
        requestWindowFeature(Window.FEATURE_PROGRESS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String serverData = getIntent().getStringExtra("servers_data");
        JSONObject object = null;
        try {
            object = new JSONObject(serverData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (object != null){
            try {
                hostsMap = (HashMap<String, Double>) Utils.jsonToMap(object);

                Object[] a = hostsMap.entrySet().toArray();
                Arrays.sort(a, new Comparator() {
                    public int compare(Object o1, Object o2) {
                        return ((Map.Entry<String, Double>) o1).getValue()
                                .compareTo(((Map.Entry<String, Double>) o2).getValue());
                    }
                });
                host = ((Map.Entry<String, Double>) a[0]).getKey();
                System.out.println("AHUENNO");
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        tempBlackList = new HashSet<>();

        getSpeedTestHostsHandler = new GetSpeedTestHostsHandler();
        getSpeedTestHostsHandler.start();


        mPointerSpeedometer = findViewById(R.id.speedView);
        mWifiView = findViewById(R.id.wifiView);
        mWifiView.setMinMaxSpeed(-100, 0);
        mPulsator =  findViewById(R.id.pulsator);
        mStartButtonFrame = findViewById(R.id.startButtonFrame);
        mSetupFrame = findViewById(R.id.setupFrame);
        mProgressFrame = findViewById(R.id.progressFrame);
        mCompletionFrame = findViewById(R.id.completionFrame);
        mBtnStart =  findViewById(R.id.btnStart);
        mPing = (TextView) findViewById(R.id.pingLinearText);
        mMeaningDown = findViewById(R.id.meaningDown);
        mMeaningUp = findViewById(R.id.meaningUp);
        menuFrame = findViewById(R.id.menuFrame);
        mMenuButton = findViewById(R.id.menuButton);
        mHostLinearText = findViewById(R.id.hostLinearText);
        mSetupText = findViewById(R.id.setupText);
        mTextTitle = findViewById(R.id.textTitle);
        mRateTheApp = findViewById(R.id.rateTheApp);

        mBackButton = findViewById(R.id.backButton);
        mBoxDataLine =findViewById(R.id.boxDataLine);
        mLinearPingType = findViewById(R.id.linearPingType);

        mToolbar = findViewById(R.id.toolbar);
        mSpeedtestTool = findViewById(R.id.speedtestTool);
        mWifiAnalyzerTool = findViewById(R.id.wifiAnalyzerTool);
        mSpeedtestFrame = findViewById(R.id.speedtestFrame);
        mWifiAnalyzerFrame = findViewById(R.id.wifiAnalyzerFrame);


        mPulsator.start();

        menuController = new MenuController(this, menuFrame);

        mBtnStart.setOnClickListener(view -> {
            stopThread = false;
            setProgressBarVisibility(true);
            mBtnStart.setEnabled(false);
            historyItem = new HistoryItem();
            historyItem.type = typeNetwork();
            historyItem.date = new Date();
            listData = new ArrayList<>();
            buttonClickStart();
        });

        mMenuButton.setOnClickListener(v -> {
//            menuController = new MenuController(this, menuFrame);
            menuController.openMenu();
        });
        mSpeedtestTool.setColorFilter(getResources().getColor(R.color.cyan_400));
        mSpeedtestTool.setOnClickListener(v -> {
            setSuperFrame(SuperFrameGroup.SPEEDTEST);
            mWifiAnalyzerTool.setColorFilter(getResources().getColor(R.color.blue_grey_400));
            mSpeedtestTool.setColorFilter(getResources().getColor(R.color.cyan_400));
        });


        mWifiAnalyzerTool.setOnClickListener(v -> {
            setSuperFrame(SuperFrameGroup.WIFIANALYZER);
            mWifiAnalyzerTool.setColorFilter(getResources().getColor(R.color.cyan_400));
            mSpeedtestTool.setColorFilter(getResources().getColor(R.color.blue_grey_400));
        });


        mBackButton.setOnClickListener(v -> {
            //TODO
            setFrame(FrameGroup.START);
            mBtnStart.setEnabled(true);
            progressThread.interrupt();
            stopThread = true;
        });

        mRateTheApp.setOnClickListener(v -> {
            RateDialogController.show(this);
        });

        setFrame(FrameGroup.START);
        setSuperFrame(SuperFrameGroup.SPEEDTEST);
        updateStrength();
    }

    @Override
    public void onBackPressed() {
        if (menuController.getMenuStatus()){
            menuController.closeMenu(true);
        } else {
            super.onBackPressed();
        }
    }

    public String typeNetwork(){
        if (Connectivity.isConnectedWifi(this)){
//            mConnectivity.setText("WIFI");
            return "Wi-Fi";
        }else if (Connectivity.isConnectedMobile(this)){
//            mConnectivity.setText("Mobile");
            return "Mobile";
        }
        return null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_speed_test, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setFrame(FrameGroup group){

        if (currentFrame != null && currentFrame == group) {
            return;
        }
        currentFrame = group;

        switch (group){

            case START:
                mBoxDataLine.setVisibility(View.GONE);
                mLinearPingType.setVisibility(View.GONE);
                mBackButton.setVisibility(View.INVISIBLE);
                mStartButtonFrame.setVisibility(View.VISIBLE);
                mProgressFrame.setVisibility(View.GONE);
                mCompletionFrame.setVisibility(View.GONE);
                mHostLinearText.setVisibility(View.GONE);
                mSetupFrame.setVisibility(View.GONE);
                break;
            case SETUP:
                Animation animFadeOutStartBtn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out_start_frame);

                animFadeOutStartBtn.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        Animation animFadeInBoxDataLine = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in_setup_frame);
                        mSetupFrame.startAnimation(animFadeInBoxDataLine);
                        mBoxDataLine.setVisibility(View.VISIBLE);
                        mLinearPingType.setVisibility(View.VISIBLE);
                        mBackButton.setVisibility(View.INVISIBLE);
                        mStartButtonFrame.setVisibility(View.GONE);
                        mProgressFrame.setVisibility(View.GONE);
                        mCompletionFrame.setVisibility(View.GONE);
                        mHostLinearText.setVisibility(View.GONE);
                        mSetupFrame.setVisibility(View.VISIBLE);

                        Animation animFadeInPointerSpeedometer = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in_pointer_speedometr);
                        mPointerSpeedometer.startAnimation(animFadeInPointerSpeedometer);
                        animFadeInPointerSpeedometer.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {
                                mSetupFrame.setVisibility(View.GONE);
                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                mPointerSpeedometer.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) { }
                        });
                        Animation animFadeInChart = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in_chart);
                        chart.startAnimation(animFadeInChart);
                        animFadeInChart.setStartOffset(300);
                        animFadeInChart.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) { }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                chart.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) { }
                        });
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                mStartButtonFrame.startAnimation(animFadeOutStartBtn);


                break;
            case PROGRESS:
                mBoxDataLine.setVisibility(View.VISIBLE);
                mProgressFrame.setVisibility(View.VISIBLE);
                mLinearPingType.setVisibility(View.VISIBLE);
                mBackButton.setVisibility(View.VISIBLE);
                mStartButtonFrame.setVisibility(View.GONE);
                mCompletionFrame.setVisibility(View.GONE);
                mHostLinearText.setVisibility(View.VISIBLE);
                mSetupFrame.setVisibility(View.GONE);
                break;
            case COMPLETION:
                Animation animFadeOutPointerSpeedometr = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out_pointer_speedometr);
                mPointerSpeedometer.startAnimation(animFadeOutPointerSpeedometr);
                animFadeOutPointerSpeedometr.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        mPointerSpeedometer.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                Animation animFadeOutChart = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out_chart);
                chart.startAnimation(animFadeOutChart);
                animFadeOutChart.setStartOffset(300);
                animFadeOutChart.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        mCompletionFrame.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        mProgressFrame.setVisibility(View.GONE);
                        mCompletionFrame.setVisibility(View.VISIBLE);
                        Animation animFadeInCompletionFrame = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in_completion_frame);
                        animFadeInCompletionFrame.setStartOffset(500);
                        mCompletionFrame.startAnimation(animFadeInCompletionFrame);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                mBoxDataLine.setVisibility(View.VISIBLE);
                mLinearPingType.setVisibility(View.VISIBLE);
                mBackButton.setVisibility(View.VISIBLE);
                mStartButtonFrame.setVisibility(View.GONE);
                mCompletionFrame.setVisibility(View.VISIBLE);
                mHostLinearText.setVisibility(View.VISIBLE);
                mSetupFrame.setVisibility(View.GONE);
                break;
        }
    }

    private enum FrameGroup {
        START,
        SETUP,
        PROGRESS,
        COMPLETION
    }

    private void setSuperFrame (SuperFrameGroup superGroup){

       switch (superGroup){
           case SPEEDTEST:
               mSpeedtestFrame.setVisibility(View.VISIBLE);
               mWifiAnalyzerFrame.setVisibility(View.GONE);
               mTextTitle.setText(getString(R.string.text_title_speedtest));
               break;

           case WIFIANALYZER:
               mSpeedtestFrame.setVisibility(View.GONE);
               mWifiAnalyzerFrame.setVisibility(View.VISIBLE);
               mTextTitle.setText(getString(R.string.text_title_wifi));
               break;
       }


    }

    private enum SuperFrameGroup {
        SPEEDTEST,
        WIFIANALYZER
    }



    private void initChart(){
        this.chart = (LineChart) findViewById(R.id.chart1);
        this.chart.getDescription().setEnabled(false);
        this.chart.setTouchEnabled(false);
        this.chart.setDragEnabled(false);
        this.chart.setScaleEnabled(false);
        XAxis xAxis = this.chart.getXAxis();
        xAxis.setEnabled(false);
        YAxis axisLeft = this.chart.getAxisLeft();
        axisLeft.setDrawGridLines(false);
        this.chart.getAxisRight().setEnabled(false);
        this.chart.getLegend().setEnabled(false);
        LineDataSet lineDataSet = new LineDataSet(new ArrayList<>(), "download");
        lineDataSet.setDrawIcons(false);
        lineDataSet.setDrawCircles(false);
        lineDataSet.setDrawValues(false);
        lineDataSet.setLineWidth(2.0f);
        lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        lineDataSet.setDrawFilled(true);
        ArrayList<ILineDataSet> arrayList = new ArrayList<>();
        arrayList.add(lineDataSet);
//        this.chart.setData(new LineData(arrayList));

        LineDataSet lineDataSetUp = new LineDataSet(new ArrayList<>(), "upload");
        lineDataSetUp.setDrawIcons(false);
        lineDataSetUp.setDrawCircles(false);
        lineDataSetUp.setDrawValues(false);
        lineDataSetUp.setLineWidth(2.0f);
        lineDataSetUp.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        lineDataSetUp.setDrawFilled(true);
//        ArrayList<ILineDataSet> arrayList2 = new ArrayList<>();
        arrayList.add(lineDataSetUp);
        this.chart.setData(new LineData(arrayList));

        this.chart.setDrawGridBackground(true);
        this.chart.setGridBackgroundColor(getResources().getColor(android.R.color.transparent));
        this.chart.getAxisLeft().setTextColor(getResources().getColor(R.color.blue_grey_400));

        LineDataSet lineDataSet2 = (LineDataSet) this.chart.getData().getDataSetByLabel("download", true);
        lineDataSet2.setColor(getResources().getColor(R.color.cyan_400));
        GradientDrawable dataSetGradient = new GradientDrawable();
        dataSetGradient.setGradientType(GradientDrawable.LINEAR_GRADIENT);
        dataSetGradient.setOrientation(GradientDrawable.Orientation.TOP_BOTTOM);
        dataSetGradient.setColors(new int[]{getResources().getColor(R.color.cyan_400), 0});
        lineDataSet2.setFillDrawable(dataSetGradient);

        LineDataSet lineDataSet3 = (LineDataSet) this.chart.getData().getDataSetByLabel("upload", true);
        lineDataSet3.setColor(getResources().getColor(R.color.pink_A400));
        GradientDrawable dataSetGradientUp = new GradientDrawable();
        dataSetGradientUp.setGradientType(GradientDrawable.LINEAR_GRADIENT);
        dataSetGradientUp.setOrientation(GradientDrawable.Orientation.TOP_BOTTOM);
        dataSetGradientUp.setColors(new int[]{getResources().getColor(R.color.pink_A400), 0});
        lineDataSet3.setFillDrawable(dataSetGradientUp);
    }

    public void setChartDataList(List<Entry> list) {
        ((LineDataSet) this.chart.getData().getDataSetByLabel("download", true)).setValues(list);
        this.chart.getData().notifyDataChanged();
        this.chart.notifyDataSetChanged();
        this.chart.invalidate();
    }
    public void setChartDataListUp(List<Entry> list) {
        ((LineDataSet) this.chart.getData().getDataSetByLabel("upload", true)).setValues(list);
        this.chart.getData().notifyDataChanged();
        this.chart.notifyDataSetChanged();
        this.chart.invalidate();
    }

    public void buttonClickStart(){
        setFrame(FrameGroup.SETUP);
        //Restart test icin eger baglanti koparsa
        if (getSpeedTestHostsHandler == null) {
            getSpeedTestHostsHandler = new GetSpeedTestHostsHandler();
            getSpeedTestHostsHandler.start();
        }

        progressThread = new Thread(() -> {
            runOnUiThread(() -> {
                mSetupText.setText("Selecting best server based on ping...");
            });

            //Get egcodes.speedtest hosts
            int timeCount = 600; //1min
            while (!getSpeedTestHostsHandler.isFinished()) {
                timeCount--;
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                }
                if (timeCount <= 0) {
                    runOnUiThread(() -> {
                        mSetupText.setText("No Connection...");
//                            startButton.setEnabled(true);
//                            startButton.setTextSize(16);
//                            startButton.setText("Restart Test");
                    });
                    getSpeedTestHostsHandler = null;
                    return;
                }
            }

            //Find closest server
            HashMap<Integer, String> mapKey = getSpeedTestHostsHandler.getMapKey();
            HashMap<Integer, List<String>> mapValue = getSpeedTestHostsHandler.getMapValue();
            double selfLat = getSpeedTestHostsHandler.getSelfLat();
            double selfLon = getSpeedTestHostsHandler.getSelfLon();
            double tmp = 19349458;
            double dist = 0.0;
            int findServerIndex = 0;
            for (int index : mapKey.keySet()) {
                if (tempBlackList.contains(mapValue.get(index).get(5))) {
                    continue;
                }

                Location source = new Location("Source");
                source.setLatitude(selfLat);
                source.setLongitude(selfLon);

                List<String> ls = mapValue.get(index);
                Location dest = new Location("Dest");
                dest.setLatitude(Double.parseDouble(ls.get(0)));
                dest.setLongitude(Double.parseDouble(ls.get(1)));

                double distance = source.distanceTo(dest);
                if (tmp > distance) {
                    tmp = distance;
                    dist = distance;
                    findServerIndex = index;
                }
            }
            System.out.println("");
            String testAddr = mapKey.get(findServerIndex).replace("http://", "https://");
            final List<String> info = mapValue.get(findServerIndex);
            final double distance = dist;

            if (info == null) {
                runOnUiThread(() -> {
                        mSetupText.setText(R.string.host_error);
                });
                return;
            }

            runOnUiThread(() -> {
                    mSetupText.setText(String.format(getString(R.string.host_info), info.get(2), new DecimalFormat("#.##").format(distance / 1000)));
                    mHostLinearText.setText(String.format(getString(R.string.host_info), info.get(2), new DecimalFormat("#.##").format(distance / 1000)));
            });



            //Reset value, graphics
            runOnUiThread(() -> {
                mPing.setText("0 ms");
                mMeaningDown.setText("--");
                mMeaningUp.setText("--");
                initChart();
                //TODO
                //chartUpload.removeAllViews();
            });
            final List<Double> pingRateList = new ArrayList<>();
            final List<Double> downloadRateList = new ArrayList<>();
            final List<Double> uploadRateList = new ArrayList<>();
            Boolean pingTestStarted = false;
            Boolean pingTestFinished = false;
            Boolean downloadTestStarted = false;
            Boolean downloadTestFinished = false;
            Boolean uploadTestStarted = false;
            Boolean uploadTestFinished = false;

            //Init Test
            final PingTest pingTest = new PingTest(info.get(6).replace(":8080", ""), 3);
            final HttpDownloadTest downloadTest = new HttpDownloadTest(testAddr.replace(testAddr.split("/")[testAddr.split("/").length - 1], ""));
            final HttpUploadTest uploadTest = new HttpUploadTest(testAddr);

            boolean downLoadCompletionDone = false;
            boolean pingCompletionDone = false;
            boolean upLoadCompletionDone = false;
            //Tests
            while (true) {
                if (stopThread){
                    downloadTest.stopThread = true;
                    uploadTest.stopThread = true;
                    return;
                };
                if (!pingTestStarted) {
                    pingTest.start();
                    pingTestStarted = true;
                }
                if (pingTestFinished && !downloadTestStarted) {
                    downloadTest.start();
                    downloadTestStarted = true;
                }
                if (downloadTestFinished && !uploadTestStarted) {
                    uploadTest.start();
                    uploadTestStarted = true;
                }


                //Ping Test
                if (pingTestFinished) {
                    //Failure
                    if (pingTest.getAvgRtt() == 0) {
                        System.out.println("Ping error...");
                    } else if (!pingCompletionDone){
                        //Success
                        runOnUiThread(() -> {
                            mPing.setText("Ping: " + dec.format(pingTest.getAvgRtt()) + " ms");
                        });
                        pingCompletionDone = true;
                    }
                } else {
                    pingRateList.add(pingTest.getInstantRtt());

                    runOnUiThread(() -> {
                        mPing.setText("Ping: " + dec.format(pingTest.getInstantRtt()) + " ms");
                    });
                }

                //Download Test
                if (pingTestFinished) {
                    runOnUiThread(() ->{
                        setFrame(FrameGroup.PROGRESS);
                    });
                    if (downloadTestFinished) {
                        //Failure
                        if (downloadTest.getFinalDownloadRate() == 0) {
                            System.out.println("Download error...");
                        } else {
                            //Success
                            if (!downLoadCompletionDone) {
                                runOnUiThread(() -> {
                                    mMeaningDown.setText(dec.format(downloadTest.getFinalDownloadRate()));
                                    listData.clear();
                                    historyItem.dmbps = (float) downloadTest.getFinalDownloadRate();
                                });
                                downLoadCompletionDone = true;
                            }
                        }
                    } else {
                        //Calc position
                        double downloadRate = downloadTest.getInstantDownloadRate();
                        downloadRateList.add(downloadRate);
                        runOnUiThread(() -> {
                            mPointerSpeedometer.speedTo((float) downloadRate);
                            mMeaningDown.setText(dec.format(downloadTest.getInstantDownloadRate()));

                        });

                        //Update chart
                        runOnUiThread(() -> {
                            if (listData.size() >= 150) {
                                listData.remove(0);
                            }
                            if (downloadRate < 0) {
                                listData.add(0f);
                            } else if (downloadRate > 120) {
                                listData.add(120f);
                            } else {
                                listData.add((float) downloadRate);
                            }

                            ArrayList<Entry> entries = new ArrayList<>();
                            for (int i = 0; i < listData.size(); i++) {
                                entries.add(new Entry((float) i, (float) listData.get(i)));
                            }
                            setChartDataList(entries);
                        });

                    }
                }


                //Upload Test

                if (downloadTestFinished) {
                    if (uploadTestFinished) {
                        //Failure
                        if (uploadTest.getFinalUploadRate() == 0) {
                            System.out.println("Upload error...");
                        } else {
                            //Success
                            if (!upLoadCompletionDone) {
                                runOnUiThread(() -> {
                                    mMeaningUp.setText(dec.format(uploadTest.getFinalUploadRate()));
                                    historyItem.umbps = (float) uploadTest.getFinalUploadRate();
                                    historyItem.save();
                                    setFrame(FrameGroup.COMPLETION);

                                });
                                upLoadCompletionDone = true;
                            }
                        }
                    } else {
                        //Calc position
                        double uploadRate = uploadTest.getInstantUploadRate();
                        uploadRateList.add(uploadRate);
                        runOnUiThread(() -> {
                            mPointerSpeedometer.speedTo((float) uploadRate);
                            mMeaningUp.setText(dec.format(uploadTest.getInstantUploadRate()));
                        });

                        //Update chart
                        runOnUiThread(() -> {
                            if (listData.size() >= 150) {
                                listData.remove(0);
                            }
                            if (uploadRate < 0) {
                                listData.add(0f);
                            } else if (uploadRate > 120) {
                                listData.add(120f);
                            } else {
                                listData.add((float) uploadRate);
                            }

                            ArrayList<Entry> entries = new ArrayList<>();
                            for (int i = 0; i < listData.size(); i++) {
                                entries.add(new Entry((float) i, (float) listData.get(i)));
                            }
                            setChartDataListUp(entries);
                        });

                    }
                }

                //Test bitti
                if (pingTestFinished && downloadTestFinished && uploadTestFinished) {
                    break;
                }

                if (pingTest.isFinished()) {
                    pingTestFinished = true;
                }
                if (downloadTest.isFinished()) {
                    downloadTestFinished = true;
                }
                if (uploadTest.isFinished()) {
                    uploadTestFinished = true;
                }

                if (pingTestStarted && !pingTestFinished) {
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                    }
                } else {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                    }
                }
            }

            //Thread bitiminde button yeniden aktif ediliyor
            runOnUiThread(() -> {
                //для рестарта
//                    startButton.setEnabled(true);
//                    startButton.setTextSize(16);
//                    startButton.setText("Restart Test");
            });


        });
        progressThread.start();
    }

    public void findWifiStrength() {
        WifiManager wifiManager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int dbm = wifiInfo.getRssi();
        mWifiView.speedPercentTo(100 - Math.abs(dbm));
        mWifiView.setSpeedometerColor(0xFFFFFFFF);
        if (dbm > -60){
            mWifiView.setSpeedometerColor(getResources().getColor(R.color.cyan_400));
            mWifiView.setSpeedTextColor(getResources().getColor(R.color.cyan_400));
            mWifiView.setUnitTextColor(getResources().getColor(R.color.cyan_400));
        } else if (dbm > -80){
            mWifiView.setSpeedometerColor(getResources().getColor(R.color.blue_grey_400));
            mWifiView.setSpeedTextColor(getResources().getColor(R.color.blue_grey_400));
            mWifiView.setUnitTextColor(getResources().getColor(R.color.blue_grey_400));
        } else {
            mWifiView.setSpeedometerColor(getResources().getColor(R.color.pink_A400));
            mWifiView.setSpeedTextColor(getResources().getColor(R.color.pink_A400));
            mWifiView.setUnitTextColor(getResources().getColor(R.color.pink_A400));
        }

    }

    public void updateStrength() {

        final long time = 1000;

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        findWifiStrength();
                    }
                });
            }
        }, time, time);
    }

}
