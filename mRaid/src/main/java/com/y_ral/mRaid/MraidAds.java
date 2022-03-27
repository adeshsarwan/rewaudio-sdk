package com.y_ral.mRaid;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.ViewStub;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MraidAds {
    private Context context;
    private ViewStub viewStub;
    private MediaPlayer mediaPlayer;
    private String advertId = null;
    private String ua;
    private WebView webView;
    private int length;
    private String publisher_id;
    private String adspot_id;
    private int height;
    private int width;
    private AudioManager audioManager;
    private View inflated;
    private ImageView imageView;
    private String click_url = "";
    private String img_url;
    private String image_tracking_url;
    private String impid;
    private String crid;
    private String cid;
    private int x;
    RequestOptions requestOptions = new RequestOptions();
    public MraidAds(Context context, ViewStub viewStub, String publisher_id, String adspot_id, int width, int height) {
        this.context = context;
        this.viewStub = viewStub;
        this.publisher_id = publisher_id;
        this.adspot_id = adspot_id;
        this.width = width;
        this.height = height;
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        viewStub.setLayoutResource(R.layout.ads);
        inflated = viewStub.inflate();
        imageView = inflated.findViewById(R.id.image);
    }

    public int getConnection() {
        int conn = 0;
        ConnectivityManager connMgr =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        for (Network network : connMgr.getAllNetworks()) {
            NetworkInfo networkInfo = connMgr.getNetworkInfo(network);
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                conn = 3;
            }
            if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                conn = 2;
            }
        }
        return conn;
    }

    public void loadGAds() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer = null;
        }
        AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                AdvertisingIdClient.Info idInfo = null;
                try {
                    idInfo = AdvertisingIdClient.getAdvertisingIdInfo(context);
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    advertId = idInfo.getId();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
                Log.d("advertId", advertId);

                return advertId;
            }

            @Override
            protected void onPostExecute(String advertId) {
                // Toast.makeText(context, advertId, Toast.LENGTH_LONG).show();
                webView = new WebView(context);
                ua = webView.getSettings().getUserAgentString();
                loadAds();
            }

        };
        task.execute();
    }

    public void loadAds() {
        if (advertId == null)
            return;
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String unique_id = Settings.Secure.getString(context.getContentResolver(),
                            Settings.Secure.ANDROID_ID) + "-" + System.currentTimeMillis();
                    String requestObj = "{ \"publisher_id\": \"" + publisher_id + "\", \"adspot_id\": \"" + adspot_id + "\", \"coppa\": 0, \"imp\": [ { \"id\": \"" + unique_id + "\", \"instl\": 0, \"audio\": { \"minduration\": 10, \"maxduration\": 20, \"startdelay\": 0, \"audio_mimes\": [ \"audio/mp3\" ] }, \"image\": { \"h\": " + height + ", \"w\": " + width + ", \"mimes\": [ \"image/gif\", \"image/jpeg\", \"image/png\", \"text/javascript\" ] } } ], \"site\": { \"domain\": \"junk1.com\" }, \"device\": { \"didsha1\": \"132079238ec783b0b89dff308e1f9bdd08576273\", \"ua\": \"" + ua + "\", \"connectiontype\": " + getConnection() + ", \"geo\": { \"lat\": 42.378, \"lon\": -71.227 }, \"devicetype\": 1 }, \"user\": { \"googleadid\": \"" + advertId + "\", \"yob\": 1961, \"gender\": \"F\" }, \"id\": \"3b99d0d9-1bff-ff85-882b-3c732f1e6da4\" }";
                    URL url = new URL("http://172.105.41.62:8080/rtb/bids/nexage");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                    conn.setRequestProperty("Accept", "application/json");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);
                    Log.i("JSONrequestObj", requestObj);
                    DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                    //os.writeBytes(URLEncoder.encode(jsonParam.toString(), "UTF-8"));
                    os.writeBytes(requestObj);

                    os.flush();
                    os.close();
                    BufferedReader br = null;
                    String strCurrentLine = "";
                    String strCurrentLines = "";
                    if (conn.getResponseCode() == 200) {
                        br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        while ((strCurrentLine = br.readLine()) != null) {
                            strCurrentLines = strCurrentLine;
                            System.out.println(strCurrentLine);
                        }
                        Log.i("STATUSMSG", strCurrentLines);
                    } else {
                        br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                        while ((strCurrentLine = br.readLine()) != null) {
                            strCurrentLines = strCurrentLine;
                            System.out.println(strCurrentLine);
                        }
                        Log.i("STATUSMSG", strCurrentLines);

                    }
                    String finalStrCurrentLines = strCurrentLines;
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Log.e("finalStrCurrentLines", finalStrCurrentLines);
                                JSONObject jsonObject = new JSONObject(finalStrCurrentLines);
                                JSONArray jsonArray = jsonObject.getJSONArray("seatbid");
                                JSONObject jsonObject1 = jsonArray.getJSONObject(0);
                                JSONArray jsonArray1 = jsonObject1.getJSONArray("bid");
                                JSONObject jsonObject2 = jsonArray1.getJSONObject(0);
                                img_url = jsonObject2.getString("img_url");
                                impid = jsonObject2.getString("impid");
                                crid = jsonObject2.getString("crid");
                                cid = jsonObject2.getString("cid");
                                image_tracking_url = jsonObject2.getString("image_tracking_url");
                                click_url = jsonObject2.getString("click_url");
                                String audio_url = jsonObject2.getString("audio_url");
                                Log.e("img_url", jsonObject2.getString("img_url"));
                                new DownloadFileFromURL(context).execute(audio_url);
                                // to enable javascripts

                            } catch (JSONException err) {
                                Log.d("Error", err.toString());
                            }
                        }
                    });
                    conn.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }


    private void playAudio(String audioUrl) {
        Log.d("audioUrl", audioUrl);


        // initializing media player
        mediaPlayer = new MediaPlayer();

        // below line is use to set the audio
        // stream type for our media player.
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        // below line is use to set our
        // url to our media player.
        try {
            mediaPlayer.setDataSource(audioUrl);
            // mediaPlayer.setDataSource("https://www.learningcontainer.com/wp-content/uploads/2020/02/Kalimba.mp3");
            // below line is use to prepare
            // and start our media player.
            mediaPlayer.prepare();
            mediaPlayer.start();
            sendDataToServer(AppConstant.start_impression);
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                public void onCompletion(MediaPlayer mp) {
                    sendDataToServer(AppConstant.complete_impression);
                }
            });
            long totalDuration = mediaPlayer.getDuration() / 1000;
            long factor = totalDuration / 3;
            x = 1;
            final Handler handler = new Handler();
            new Handler().postDelayed(new Runnable() {
                @SuppressLint("LongLogTag")
                @Override
                public void run() {
                    if (mediaPlayer != null && x < 4) {
                        long currentDuration = (mediaPlayer.getCurrentPosition() / 1000) + 1;
                        Log.e("impression factor", (factor * x) + "");
                        Log.e("impression currentDuration", currentDuration + "");
                        if ((factor * x) == currentDuration) {
                            Log.e("impression", AppConstant.impression_array[x - 1]);
                            sendDataToServer(AppConstant.impression_array[x - 1]);
                            x++;
                        }
                        handler.postDelayed(this, 1000);
                    }
                }
            }, 1000);


        } catch (IOException e) {
            e.printStackTrace();
        }
        // below line is use to display a toast message.
    }

    class DownloadFileFromURL extends AsyncTask<String, String, String> {
        private Context context;

        public DownloadFileFromURL(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... sUrl) {
            // take CPU lock to prevent CPU from going off if the user
            // presses the power button during download
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, getClass().getName());
            wl.acquire();
            try {
                InputStream input = null;
                OutputStream output = null;
                HttpURLConnection connection = null;
                try {
                    URL url = new URL(sUrl[0]);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.connect();
                    // expect HTTP 200 OK, so we don't mistakenly save error report
                    // instead of the file
                    if (connection.getResponseCode() != HttpURLConnection.HTTP_OK)
                        return "Server returned HTTP " + connection.getResponseCode()
                                + " " + connection.getResponseMessage();
                    // this will be useful to display download percentage
                    // might be -1: server did not report the length
                    int fileLength = connection.getContentLength();
                    // download the file
                    input = connection.getInputStream();
                    File file = new File(Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_DOWNLOADS),
                            "file_name.mp3"
                    );
                    //ew File(root, "file_name.mp3");
                    output = new FileOutputStream(file.getAbsolutePath());
                    byte data[] = new byte[4096];
                    long total = 0;
                    int count;
                    while ((count = input.read(data)) != -1) {
                        // allow canceling with back button
                        if (isCancelled())
                            return null;
                        total += count;
                        // publishing the progress....
//                        if (fileLength > 0)
//                            pDialog.setProgress((int) (total * 100 / fileLength));
// only if total length is known
                        //  publishProgress((long) (total * 100 / fileLength));
                        output.write(data, 0, count);
                    }
                } catch (Exception e) {
                    return e.toString();
                } finally {
                    try {
                        if (output != null)
                            output.close();
                        if (input != null)
                            input.close();
                    } catch (IOException ignored) {
                    }
                    if (connection != null)
                        connection.disconnect();
                }
            } finally {
                wl.release();
            }
            return null;
        }

        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(String.valueOf(progress));
            // if we get here, length is known, now set indeterminate to false
//            pDialog.setIndeterminate(false);
//            pDialog.setMax(100);
//            pDialog.setProgress(progress[0]);
        }

        @Override
        protected void onPostExecute(String result) {
           // pDialog.dismiss();
            if (result != null) {
                Toast.makeText(context, "Download error: " + result, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(context, "File downloaded", Toast.LENGTH_SHORT).show();
                webView.getSettings().setJavaScriptEnabled(true);
                webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
                webView.getSettings().setBuiltInZoomControls(true);
                // zoom if you want
                webView.getSettings().setSupportZoom(true);
                // to support url redirections
                webView.setWebViewClient(new WebViewClient());
                // extra settings
                webView.getSettings().setLoadWithOverviewMode(false);
                webView.getSettings().setUseWideViewPort(true);
                webView.setScrollContainer(true);
                // setting for lollipop and above
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
                    webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
                }
                webView.loadUrl(img_url);
                inflated.findViewById(R.id.rel).setVisibility(View.VISIBLE);
                requestOptions = requestOptions.placeholder(R.mipmap.ic_launcher);
                Glide.with(context)
                        .load(img_url)
                        .apply(requestOptions)
                        .into(imageView);

                // Storing the data in file with name as geeksData.txt
                File file = new File(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DOWNLOADS),
                        "file_name.mp3"
                );
                //new File(root, "file_name.mp3");
                Log.e("requestObj", file.getAbsolutePath());
                playAudio(file.getAbsolutePath());
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(click_url));
                        context.startActivity(i);
                        sendDataToServer(AppConstant.click);
                    }
                });
                inflated.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        inflated.findViewById(R.id.rel).setVisibility(View.GONE);
                        mediaPlayer.stop();
                        sendDataToServer(AppConstant.pause);
                    }
                });
                ImageView volume = inflated.findViewById(R.id.volume);
                int music_volume_level = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                if (music_volume_level != 0)
                    Glide.with(context).load(R.drawable.volume_high).apply(requestOptions).into(volume);
                 else
                    Glide.with(context).load(R.drawable.volume_mute).apply(requestOptions).into(volume);

                volume.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int music_volume_level = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                        if (music_volume_level == 0) {
                            Glide.with(context).load(R.drawable.volume_high).apply(requestOptions).into(volume);
                            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 90, 0);
                            sendDataToServer(AppConstant.mute);
                        } else {
                            Glide.with(context).load(R.drawable.volume_mute).apply(requestOptions).into(volume);
                            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
                            sendDataToServer(AppConstant.unmute);
                        }
                    }
                });
            }
        }
    }

    public void sendDataToServer(String event_name) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String requestObj = "{ \"impression_id\": \"" + impid + "\", \"campaign_id\": \"" + cid + "\", \"creative_id\": \"" + crid + "\", \"event_name\": \"" + event_name + "\", \"volume_level\": \"" + getVolumeLevel() + "\"}";
                    Log.e("requestObjs", requestObj);
                    URL url = new URL(image_tracking_url);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                    conn.setRequestProperty("Accept", "application/json");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);
                    Log.i("JSON", requestObj);
                    DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                    //os.writeBytes(URLEncoder.encode(jsonParam.toString(), "UTF-8"));
                    os.writeBytes(requestObj);

                    os.flush();
                    os.close();
                    BufferedReader br = null;
                    String strCurrentLine = "";
                    String strCurrentLines = "";
                    if (conn.getResponseCode() == 200) {
                        br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        while ((strCurrentLine = br.readLine()) != null) {
                            strCurrentLines = strCurrentLine;
                            System.out.println(strCurrentLine);
                        }
                        Log.i("STATUSMSG", strCurrentLines);
                    } else {
                        br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                        while ((strCurrentLine = br.readLine()) != null) {
                            strCurrentLines = strCurrentLine;
                            System.out.println(strCurrentLine);
                        }
                        Log.i("STATUSMSG", strCurrentLines);

                    }
                    String finalStrCurrentLines = strCurrentLines;
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Log.e("finalStrCurrentLines", finalStrCurrentLines);
                                Log.e("getResponseCode", conn.getResponseMessage() + "");
                            } catch (Exception err) {
                                Log.d("Error", err.toString());
                            }
                        }
                    });
                    conn.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    private int getVolumeLevel() {
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        int volumeLevel = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        int maxVolumeLevel = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        return (int) (((float) volumeLevel / maxVolumeLevel) * 100);
    }

    public void resumeAudio() {
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.seekTo(length);
            mediaPlayer.start();
            sendDataToServer(AppConstant.unmute);
        }
    }

    public void pauseAudio() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            length = mediaPlayer.getCurrentPosition();
            sendDataToServer(AppConstant.mute);
        }
    }
}
