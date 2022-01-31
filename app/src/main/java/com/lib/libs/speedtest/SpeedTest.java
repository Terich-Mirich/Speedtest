package com.lib.libs.speedtest;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import fr.bmartel.speedtest.SpeedTestReport;
import fr.bmartel.speedtest.SpeedTestSocket;
import fr.bmartel.speedtest.inter.ISpeedTestListener;
import fr.bmartel.speedtest.model.SpeedTestError;

public class SpeedTest extends AppCompatActivity {

    private static final int SOCKET_TIMEOUT = 5000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mDecimalFormater = new DecimalFormat("##.##");
        //Request the progress bar to be shown in the title
        requestWindowFeature(Window.FEATURE_PROGRESS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speed_test);
        bindListeners();
    }

    private void downloadTest(){
        SpeedTestSocket speedTestSocket = new SpeedTestSocket();
        speedTestSocket.setSocketTimeout(SOCKET_TIMEOUT);
        speedTestSocket.addSpeedTestListener(new ISpeedTestListener() {


            @Override
            public void onCompletion(SpeedTestReport report) {
                // called when download/upload is complete
//                System.out.println("[D COMPLETED] rate in octet/s : " + report.getTransferRateOctet());
//                System.out.println("[D COMPLETED] rate in bit/s   : " + report.getTransferRateBit());
                mTransferRateOctet.setText(String.format("[D COMPLETED] rate in octet/s   : " + report.getTransferRateOctet()));
                mTransferRateBit.setText(String.format("[D COMPLETED] rate in bit/s   : " + report.getTransferRateBit()));
                uploadTest();
            }

            @Override
            public void onError(SpeedTestError speedTestError, String errorMessage) {
                // called when a download/upload error occur
                System.out.println("HUI");
            }

            @Override
            public void onProgress(float percent, SpeedTestReport report) {
                // called to notify download/upload progress
//                System.out.println("[D PROGRESS] progress : " + percent + "%");
//                System.out.println("[D PROGRESS] rate in octet/s : " + report.getTransferRateOctet());
//                System.out.println("[D PROGRESS] rate in bit/s   : " + report.getTransferRateBit());
                mProgress.setText("[D PROGRESS] progress : " + percent + "%");
                mTransferRateOctet.setText(String.format("[D PROGRESS] rate in octet/s   : " + report.getTransferRateOctet()));
                mTransferRateBit.setText(String.format("[D PROGRESS] rate in bit/s   : " + report.getTransferRateBit()));
            }
        });
        AsyncTask.execute(() -> {
            speedTestSocket.startDownload("http://ipv4.ikoula.testdebit.info/10M.iso");
        });

    }

    private void uploadTest(){

        SpeedTestSocket speedTestSocket = new SpeedTestSocket();
        //set timeout for download
        speedTestSocket.setSocketTimeout(SOCKET_TIMEOUT);
        speedTestSocket.addSpeedTestListener(new ISpeedTestListener() {

            @Override
            public void onCompletion(SpeedTestReport report) {
                // called when download/upload is complete
//                System.out.println("[U COMPLETED] rate in octet/s : " + report.getTransferRateOctet());
//                System.out.println("[U COMPLETED] rate in bit/s   : " + report.getTransferRateBit());
                mTransferRateOctetUp.setText(String.format("[U COMPLETED] rate in octet/s   : " + report.getTransferRateOctet()));
                mTransferRateBitUp.setText(String.format("[U COMPLETED] rate in bit/s   : " + report.getTransferRateBit()));

            }

            @Override
            public void onError(SpeedTestError speedTestError, String errorMessage) {
                // called when a download/upload error occur
            }

            @Override
            public void onProgress(float percent, SpeedTestReport report) {
                // called to notify download/upload progress
//                System.out.println("[U PROGRESS] progress : " + percent + "%");
//                System.out.println("[U PROGRESS] rate in octet/s : " + report.getTransferRateOctet());
//                System.out.println("[U PROGRESS] rate in bit/s   : " + report.getTransferRateBit());
                mProgress.setText("[U PROGRESS] progress : " + percent + "%");
                mTransferRateOctetUp.setText(String.format("[U PROGRESS] rate in octet/s   : " + report.getTransferRateOctet()));
                mTransferRateBitUp.setText(String.format("[U PROGRESS] rate in bit/s   : " + report.getTransferRateBit()));
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
        mBtnStart = (Button) findViewById(R.id.btnStart);
        mTxtSpeed = (TextView) findViewById(R.id.speed);
        mTxtConnectionSpeed = (TextView) findViewById(R.id.connectionspeeed);
        mTxtProgress = (TextView) findViewById(R.id.progress);
        mTxtNetwork = (TextView) findViewById(R.id.networktype);
        mTransferRateBit = (TextView) findViewById(R.id.TransferRateOctet);
        mTransferRateOctet = (TextView) findViewById(R.id.TransferRateBit);
        mProgress = (TextView) findViewById(R.id.Progress);
        mTransferRateOctetUp = (TextView) findViewById(R.id.TransferRateOctetUp);
        mTransferRateBitUp = (TextView) findViewById(R.id.TransferRateBitUp);
        mPing = (TextView) findViewById(R.id.ping);

        mBtnStart.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View view) {
                setProgressBarVisibility(true);
                mTxtSpeed.setText("Test started");
                mBtnStart.setEnabled(false);
                mTxtNetwork.setText(R.string.network_detecting);
                mTransferRateBit.setText("Test l");
               // new Thread(mWorker).start();
                String str = ping("www.google.com");
                System.out.println("HUI^    --------------- "+str);
                mPing.setText(str);
                downloadTest();

            }
        });
    }


    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(final Message msg) {
            double progress = 0;
            switch (msg.what) {
                case MSG_UPDATE_STATUS:
                    final SpeedInfo info1 = (SpeedInfo) msg.obj;
                    mTxtSpeed.setText(String.format(getResources().getString(R.string.update_speed), mDecimalFormater.format(info1.kilobits)));
                    // Title progress is in range 0..10000
                    setProgress(100 * msg.arg1);
                    mTxtProgress.setText(String.format(getResources().getString(R.string.update_downloaded), msg.arg1));
                    break;
                case MSG_UPDATE_CONNECTION_TIME:
                    mTxtConnectionSpeed.setText(String.format(getResources().getString(R.string.update_connectionspeed), msg.arg1));
                    break;
                case MSG_COMPLETE_STATUS:
                    final SpeedInfo info2 = (SpeedInfo) msg.obj;
                    mTxtSpeed.setText(String.format(getResources().getString(R.string.update_downloaded_complete), info2.kilobits));
                    mTxtProgress.setText(String.format(getResources().getString(R.string.update_downloaded), 100));

                    if (networkType(info2.kilobits) == 1) {
                        mTxtNetwork.setText(R.string.network_3g);
                    } else {
                        mTxtNetwork.setText(R.string.network_edge);
                    }

                    mBtnStart.setEnabled(true);
                    setProgressBarVisibility(false);
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    };

    /**
     * Our Slave worker that does actually all the work
     */
    private final Runnable mWorker = new Runnable() {

        @Override
        public void run() {
            InputStream stream = null;
            try {
                int bytesIn = 0;
                String downloadFileUrl = "http://speedtest.ftp.otenet.gr/files/test1Mb.db";
                long startCon = System.currentTimeMillis();
                URL url = new URL(downloadFileUrl);
                URLConnection con = url.openConnection();
                con.setUseCaches(false);
                long connectionLatency = System.currentTimeMillis() - startCon;
                stream = con.getInputStream();

                Message msgUpdateConnection = Message.obtain(mHandler, MSG_UPDATE_CONNECTION_TIME);
                msgUpdateConnection.arg1 = (int) connectionLatency;
                mHandler.sendMessage(msgUpdateConnection);

                long start = System.currentTimeMillis();
                int currentByte = 0;
                long updateStart = System.currentTimeMillis();
                long updateDelta = 0;
                int bytesInThreshold = 0;

                while ((currentByte = stream.read()) != -1) {
                    bytesIn++;
                    bytesInThreshold++;
                    if (updateDelta >= UPDATE_THRESHOLD) {
                        int progress = (int) ((bytesIn / (double) EXPECTED_SIZE_IN_BYTES) * 100);
                        Message msg = Message.obtain(mHandler, MSG_UPDATE_STATUS, calculate(updateDelta, bytesInThreshold));
                        msg.arg1 = progress;
                        msg.arg2 = bytesIn;
                        mHandler.sendMessage(msg);
                        //Reset
                        updateStart = System.currentTimeMillis();
                        bytesInThreshold = 0;
                    }
                    updateDelta = System.currentTimeMillis() - updateStart;
                }

                long downloadTime = (System.currentTimeMillis() - start);
                //Prevent AritchmeticException
                if (downloadTime == 0) {
                    downloadTime = 1;
                }

                Message msg = Message.obtain(mHandler, MSG_COMPLETE_STATUS, calculate(downloadTime, bytesIn));
                msg.arg1 = bytesIn;
                mHandler.sendMessage(msg);
            } catch (MalformedURLException e) {
                Log.e(TAG, e.getMessage());
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            } finally {
                try {
                    if (stream != null) {
                        stream.close();
                    }
                } catch (IOException e) {
                    //Suppressed
                }
            }

        }
    };

    /**
     * Get Network type from download rate
     *
     * @return 0 for Edge and 1 for 3G
     */
    private int networkType(final double kbps) {
        int type = 1;//3G
        //Check if its EDGE
        if (kbps < EDGE_THRESHOLD) {
            type = 0;
        }
        return type;
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


    //Private fields
    private static final String TAG = SpeedTest.class.getSimpleName();
    private static final int EXPECTED_SIZE_IN_BYTES = 1048576;//1MB 1024*1024

    private static final double EDGE_THRESHOLD = 176.0;
    private static final double BYTE_TO_KILOBIT = 0.0078125;
    private static final double KILOBIT_TO_MEGABIT = 0.0009765625;

    private Button mBtnStart;
    private TextView mTxtSpeed;
    private TextView mTxtConnectionSpeed;
    private TextView mTxtProgress;
    private TextView mTxtNetwork;
    private TextView mTransferRateBit;
    private TextView mTransferRateOctet;
    private TextView mProgress;
    private TextView mTransferRateOctetUp;
    private TextView mTransferRateBitUp;
    private TextView mPing;

    private final int MSG_UPDATE_STATUS = 0;
    private final int MSG_UPDATE_CONNECTION_TIME = 1;
    private final int MSG_COMPLETE_STATUS = 2;

    private final static int UPDATE_THRESHOLD = 300;


    private DecimalFormat mDecimalFormater;

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
}
