package com.lib.libs.speedtest;

import static com.lib.libs.speedtest.Utils.mbits;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.anastr.speedviewlib.PointerSpeedometer;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.lib.libs.speedtest.controllers.MenuController;
import com.lib.libs.speedtest.models.HistoryItem;

import fr.bmartel.speedtest.SpeedTestReport;
import fr.bmartel.speedtest.SpeedTestSocket;
import fr.bmartel.speedtest.inter.ISpeedTestListener;
import fr.bmartel.speedtest.model.SpeedTestError;
import pl.bclogic.pulsator4droid.library.PulsatorLayout;

public class SpeedTest extends AppCompatActivity {

    private static final int SOCKET_TIMEOUT = 5000;
    //Private fields
    private static final String TAG = SpeedTest.class.getSimpleName();
    private static final int EXPECTED_SIZE_IN_BYTES = 1048576;//1MB 1024*1024

    private static final double EDGE_THRESHOLD = 176.0;
    private static final double BYTE_TO_KILOBIT = 0.0078125;
    private static final double KILOBIT_TO_MEGABIT = 0.0009765625;

    private PulsatorLayout mPulsator;
    private HistoryItem historyItem;



    private ViewGroup mStartButtonFrame;
    private ViewGroup mProgressFrame;
    private ViewGroup mCompletionFrame;
    private ViewGroup menuFrame;

    private PointerSpeedometer mPointerSpeedometer;

    private View mBackButton;
    private View mBtnStart;
    private View mMenuButton;
    private View mBoxDataLine;
    private View mLinearPingType;





    private TextView mPing;
    private TextView mMeaningDown;
    private TextView mMeaningUp;


    private LineChart chart;
    private List<Float> listData;

    private MenuController menuController;




    private final int MSG_UPDATE_STATUS = 0;
    private final int MSG_UPDATE_CONNECTION_TIME = 1;
    private final int MSG_COMPLETE_STATUS = 2;

    private final static int UPDATE_THRESHOLD = 300;


    private DecimalFormat mDecimalFormater;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mDecimalFormater = new DecimalFormat("##.##");
        //Request the progress bar to be shown in the title
        requestWindowFeature(Window.FEATURE_PROGRESS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speed_test);


        mPointerSpeedometer = findViewById(R.id.speedView);
        mPulsator =  findViewById(R.id.pulsator);
        mStartButtonFrame = findViewById(R.id.startButtonFrame);
        mProgressFrame = findViewById(R.id.progressFrame);
        mCompletionFrame = findViewById(R.id.completionFrame);
        mBtnStart =  findViewById(R.id.btnStart);
        mPing = (TextView) findViewById(R.id.pingLinearText);
        mMeaningDown = findViewById(R.id.meaningDown);
        mMeaningUp = findViewById(R.id.meaningUp);
        menuFrame = findViewById(R.id.menuFrame);
        mMenuButton = findViewById(R.id.menuButton);

        mBackButton = findViewById(R.id.backButton);
        mBoxDataLine =findViewById(R.id.boxDataLine);
        mLinearPingType = findViewById(R.id.linearPingType);

        mPulsator.start();

        menuController = new MenuController(this, menuFrame);

        mBtnStart.setOnClickListener(view -> {
            setFrame(2);
            setProgressBarVisibility(true);
            mBtnStart.setEnabled(false);
            //  new Thread(mWorker).start();
            String str = ping("www.google.com");
            System.out.println("HUI^    --------------- " + str);
            mPing.setText(str);
            historyItem = new HistoryItem();
            historyItem.type = typeNetwork();
            historyItem.date = new Date();
            listData = new ArrayList<>();
            downloadTest();
        });






        mMenuButton.setOnClickListener(v -> {
//            menuController = new MenuController(this, menuFrame);
            menuController.openMenu();
        });




        if (Connectivity.isConnectedWifi(this)){
        //    mConnectivity.setText("WIFI");
        }else if (Connectivity.isConnectedMobile(this)){
          //  mConnectivity.setText("mmm");
        }
        setFrame(1);
        initChart();

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

    private void downloadTest() {
        SpeedTestSocket speedTestSocket = new SpeedTestSocket();
        speedTestSocket.setSocketTimeout(SOCKET_TIMEOUT);
        speedTestSocket.addSpeedTestListener(new ISpeedTestListener()
        {
            private int prevPercent = 0;


            @Override
            public void onCompletion(SpeedTestReport report) {
                runOnUiThread(() -> {
                    mMeaningDown.setText(String.valueOf(mbits(report.getTransferRateBit())));
                    uploadTest();
                    float dataSpeed = mbits((report.getTransferRateBit()));


                    historyItem.dmbps = mbits(report.getTransferRateBit());
                });

            }

            @Override
            public void onError(SpeedTestError speedTestError, String errorMessage) {
                // called when a download/upload error occur
                System.out.println("HUI");
            }

            @Override
            public void onProgress(float percent, SpeedTestReport report) {
                runOnUiThread(() -> {
//                    mProgress.setText("[D PROGRESS] progress : " + percent + "%");


                    float dataSpeed = mbits((report.getTransferRateBit()));
                    mPointerSpeedometer.speedTo(dataSpeed);

                    int per = (int) percent;
                    if (per == 0) {
                        prevPercent = 0;
                    }
                    if (per != prevPercent) {
                        if (listData.size() >= 150) {
                            listData.remove(0);
                        }
                        if (dataSpeed < 0) {
                            listData.add(0f);
                        } else if (dataSpeed > 120) {
                            listData.add(120f);
                        } else {
                            listData.add(dataSpeed);
                        }

                        ArrayList<Entry> entries = new ArrayList<>();
                        for (int i = 0; i < listData.size(); i++) {
                            entries.add(new Entry((float) i, (float) listData.get(i)));
                        }
                        setChartDataList(entries);
                    }

                    prevPercent = per;

                });
            }
        });
        AsyncTask.execute(() -> {
            speedTestSocket.startDownload("http://ipv4.ikoula.testdebit.info/10M.iso");
        });

    }

    private void uploadTest() {

        SpeedTestSocket speedTestSocket = new SpeedTestSocket();
        //set timeout for download
        speedTestSocket.setSocketTimeout(SOCKET_TIMEOUT);
        speedTestSocket.addSpeedTestListener(new ISpeedTestListener() {

            private int prevPercent = 0;

            @Override
            public void onCompletion(SpeedTestReport report) {
                runOnUiThread(() -> {
                    mMeaningUp.setText(String.valueOf(mbits(report.getTransferRateBit())));
                    float dataSpeed = mbits((report.getTransferRateBit()));


                    historyItem.umbps = mbits(report.getTransferRateBit());
                    historyItem.save();
                    runOnUiThread(() -> {
                        setFrame(3);
                    });
                });
            }

            @Override
            public void onError(SpeedTestError speedTestError, String errorMessage) {
                // called when a download/upload error occur
            }

            @Override
            public void onProgress(float percent, SpeedTestReport report) {
                runOnUiThread(() -> {
//                    mProgress.setText("[U PROGRESS] progress : " + percent + "%");

                    float dataSpeed = mbits((report.getTransferRateBit()));
                    mPointerSpeedometer.speedTo(dataSpeed);

                    int per = (int) percent;
                    if (per == 0) {
                        prevPercent = 0;
                    }
                    if (per != prevPercent) {
                        if (listData.size() >= 150) {
                            listData.remove(0);
                        }
                        if (dataSpeed < 0) {
                            listData.add(0f);
                        } else if (dataSpeed > 120) {
                            listData.add(120f);
                        } else {
                            listData.add(dataSpeed);
                        }

                        ArrayList<Entry> entries = new ArrayList<>();
                        for (int i = 0; i < listData.size(); i++) {
                            entries.add(new Entry((float) i, (float) listData.get(i)));
                        }
                        setChartDataListUp(entries);
                    }

                    prevPercent = per;


                });

            }
        });
        AsyncTask.execute(() -> {
            speedTestSocket.startUpload("http://ipv4.ikoula.testdebit.info/", 10000000);
        });

    }

    public String ping(String url) {
        String str = "";
        try {
            java.lang.Process process = Runtime.getRuntime().exec(
                    "ping -c 1 " + url);
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    process.getInputStream()));
            int i;
            char[] buffer = new char[4096];
            StringBuffer output = new StringBuffer();
            String op[] = new String[64];
            String delay[] = new String[8];
            while ((i = reader.read(buffer)) > 0)
                output.append(buffer, 0, i);
            reader.close();
            op = output.toString().split("\n");
            delay = op[1].split("time=");

            // body.append(output.toString()+"\n");
            str = delay[1];
            Log.i("Pinger", "Ping: " + delay[1]);
        } catch (IOException e) {
            // body.append("Error\n");
            e.printStackTrace();
        }
        return str;
    }


    /**
     * Setup event handlers and bind variables to values from xml
     */
    private void bindListeners() {

    }


    /**
     * 1 byte = 0.0078125 kilobits
     * 1 kilobits = 0.0009765625 megabit
     *
     * @param downloadTime in miliseconds
     * @param bytesIn      number of bytes downloaded
     * @return SpeedInfo containing current speed
     */
    private SpeedInfo calculate(final long downloadTime, final long bytesIn) {
        SpeedInfo info = new SpeedInfo();
        //from mil to sec
        long bytespersecond = (bytesIn / downloadTime) * 1000;
        double kilobits = bytespersecond * BYTE_TO_KILOBIT;
        double megabits = kilobits * KILOBIT_TO_MEGABIT;
        info.downspeed = bytespersecond;
        info.kilobits = kilobits;
        info.megabits = megabits;

        return info;
    }

    /**
     * Transfer Object
     *
     * @author devil
     */
    private static class SpeedInfo {
        public double kilobits = 0;
        public double megabits = 0;
        public double downspeed = 0;
    }



    ;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_speed_test, menu);
        return true;
    }

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

    private void setFrame(int index){
        if (index == 1){
            mBoxDataLine.setVisibility(View.GONE);
            mLinearPingType.setVisibility(View.GONE);
            mBackButton.setVisibility(View.GONE);
            mStartButtonFrame.setVisibility(View.VISIBLE);
            mProgressFrame.setVisibility(View.GONE);
            mCompletionFrame.setVisibility(View.GONE);
        }else if (index == 2){
            mBoxDataLine.setVisibility(View.VISIBLE);
            mLinearPingType.setVisibility(View.VISIBLE);
            mStartButtonFrame.setVisibility(View.GONE);
            mProgressFrame.setVisibility(View.VISIBLE);
            mCompletionFrame.setVisibility(View.GONE);
        }else if (index == 3){
            mBoxDataLine.setVisibility(View.VISIBLE);
            mLinearPingType.setVisibility(View.VISIBLE);
            mStartButtonFrame.setVisibility(View.GONE);
            mProgressFrame.setVisibility(View.GONE);
            mCompletionFrame.setVisibility(View.VISIBLE);
        };
    }

    private void initChart(){



        this.chart = (LineChart) findViewById(R.id.chart1);
        this.chart.getDescription().setEnabled(false);
        this.chart.setTouchEnabled(false);
        this.chart.setDragEnabled(false);
        this.chart.setScaleEnabled(false);
        XAxis xAxis = this.chart.getXAxis();
        xAxis.setAxisMaximum(150.0f);
        xAxis.setEnabled(false);
        YAxis axisLeft = this.chart.getAxisLeft();
        axisLeft.setDrawGridLines(false);
        axisLeft.setAxisLineColor(0);
        axisLeft.setTextSize(10.0f);
        axisLeft.setAxisMaximum(120.0f);
        axisLeft.setAxisMinimum(0.0f);
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
        this.chart.setData(new LineData(arrayList));

        LineDataSet lineDataSetUp = new LineDataSet(new ArrayList<>(), "upload");
        lineDataSetUp.setDrawIcons(false);
        lineDataSetUp.setDrawCircles(false);
        lineDataSetUp.setDrawValues(false);
        lineDataSetUp.setLineWidth(2.0f);
        lineDataSetUp.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        lineDataSetUp.setDrawFilled(true);
        arrayList.add(lineDataSetUp);
        this.chart.setData(new LineData(arrayList));

        this.chart.setDrawGridBackground(true);
        this.chart.setGridBackgroundColor(getResources().getColor(android.R.color.transparent));
        this.chart.getAxisLeft().setTextColor(getResources().getColor(R.color.amber_800));

        LineDataSet lineDataSet2 = (LineDataSet) this.chart.getData().getDataSetByLabel("download", true);
        lineDataSet2.setColor(getResources().getColor(R.color.light_blue_900));
        GradientDrawable dataSetGradient = new GradientDrawable();
        dataSetGradient.setGradientType(GradientDrawable.LINEAR_GRADIENT);
        dataSetGradient.setOrientation(GradientDrawable.Orientation.TOP_BOTTOM);
        dataSetGradient.setColors(new int[]{getResources().getColor(R.color.light_blue_900), 0});
        lineDataSet2.setFillDrawable(dataSetGradient);

        LineDataSet lineDataSet3 = (LineDataSet) this.chart.getData().getDataSetByLabel("upload", true);
        lineDataSet3.setColor(getResources().getColor(R.color.green_800));
        GradientDrawable dataSetGradientUp = new GradientDrawable();
        dataSetGradientUp.setGradientType(GradientDrawable.LINEAR_GRADIENT);
        dataSetGradientUp.setOrientation(GradientDrawable.Orientation.TOP_BOTTOM);
        dataSetGradientUp.setColors(new int[]{getResources().getColor(R.color.green_800), 0});
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


}
