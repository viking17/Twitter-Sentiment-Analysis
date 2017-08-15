package msrit.microsoftstudent.com.twitteranalyser;


import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.ibm.watson.developer_cloud.tone_analyzer.v3.ToneAnalyzer;
import com.ibm.watson.developer_cloud.tone_analyzer.v3.model.Tone;
import com.ibm.watson.developer_cloud.tone_analyzer.v3.model.ToneAnalysis;
import com.twitter.sdk.android.core.TwitterException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import twitter4j.GeoLocation;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class Search extends AppCompatActivity {

    Status status;
    TextView t1;
    Button bt,bt1;
    ToneAnalysisWatson toneAnalysisWatson;
    ToneAnalysis tone;
    String tweets;
    boolean tweets_obtained = true;
    Button toneclick;
    TextView textView;
    EditText edt;

    TextView dev;
    CardView emo,lan,social;

    PieChart pieChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);

        toneAnalysisWatson = new ToneAnalysisWatson();

        pieChart = (PieChart) findViewById(R.id.piechart);
        bt = (Button) findViewById(R.id.start);

        edt = (EditText)findViewById(R.id.searchlol);
        emo = (CardView)findViewById(R.id.emo);
        lan = (CardView)findViewById(R.id.lan);
        social = (CardView)findViewById(R.id.social);
        dev = (TextView)findViewById(R.id.dev);
        textView = (TextView) findViewById(R.id.textView);

        registerForContextMenu(pieChart);

        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String poop = edt.getText().toString();
                if(tweets_obtained) {
                    Toast.makeText(Search.this, "starting", Toast.LENGTH_SHORT).show();
                    tweets_obtained=false;
                    task tt = new task();
                    tt.execute(poop);

                }
                else {
                    Toast.makeText(Search.this, "process running", Toast.LENGTH_SHORT).show();
                    tweets = ToneAnalysisWatson.tweets;
                }

            }
        });

        emo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Search.this,TonesDetailedAnalysis.class);
                i.putExtra("tone_cat",0);
                startActivity(i);
            }
        });

        lan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Search.this,TonesDetailedAnalysis.class);
                i.putExtra("tone_cat",1);
                startActivity(i);
            }
        });

        social.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Search.this,TonesDetailedAnalysis.class);
                i.putExtra("tone_cat",2);
                startActivity(i);
            }
        });
    }

    public void gettonesetpie(){
        ToneAnalyzer service = new ToneAnalyzer(ToneAnalyzer.VERSION_DATE_2016_05_19);
        service.setUsernameAndPassword("630971f3-0db7-4d4d-9982-c911ff0bff4a", "g1jRCEEXqYZK");
        tone = service.getTone(tweets, null).execute();


       // bt.setText(tone.getSentencesTone().size()+"");
        ToneAnalysisWatson.tone = tone;
        dev.setText(tone.toString());
        // textView.append("\n"+tweets);
        // textView.append("\n"+tone.getSentencesTone().toString());



        toneAnalysisWatson.setdocumenttones();
        textView.setText("Most prominent sentiment = "+tone.getDocumentTone().getTones().get(ToneAnalysisWatson.sentiment_position).getName() + "\n\nMost prominent" +
                        " emotion = "+tone.getDocumentTone().getTones().get(ToneAnalysisWatson.sentiment_position).getTones().get(ToneAnalysisWatson.sentiment_tone_position).getName());

        pieChart.setUsePercentValues(true);
        pieChart.setRotationEnabled(true);
        pieChart.setCenterText("Tone analysis");
        pieChart.setCenterTextSize(7);
        pieChart.setHoleRadius(25f);
        pieChart.setTransparentCircleAlpha(0);
        pieChart.setDrawEntryLabels(true);

        ArrayList<PieEntry> yvalues = new ArrayList<>();
        ArrayList<String> x = new ArrayList<>();
        ArrayList<Integer> colours = new ArrayList<>();

        double total = toneAnalysisWatson.emotone + toneAnalysisWatson.lantone + toneAnalysisWatson.socialtone;
        yvalues.add(new PieEntry((int) (toneAnalysisWatson.emotone * 100 / total), 0));
        yvalues.add(new PieEntry((int) (toneAnalysisWatson.lantone * 100 / total), 1));
        yvalues.add(new PieEntry((int) (toneAnalysisWatson.socialtone * 100 / total), 2));

        colours.add(Color.GREEN);
        colours.add(Color.RED);
        colours.add(Color.YELLOW);

        PieDataSet dataSet = new PieDataSet(yvalues, "\nGreean-Emotion\n Red-Language\n Yellow-Social");
        dataSet.setSelectionShift(2);
        dataSet.setValueTextSize(10);
        dataSet.setColors(colours);

        Legend legend = pieChart.getLegend();
        legend.setForm(Legend.LegendForm.CIRCLE);
        legend.setPosition(Legend.LegendPosition.BELOW_CHART_LEFT);
        PieData pieData = new PieData(dataSet);
        pieChart.setData(pieData);
        pieChart.invalidate();
        //Toast.makeText(this,ToneAnalysisWatson.tweet_data.size()+"",Toast.LENGTH_LONG).show();
    }

    public class task extends AsyncTask<String, Void, String>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {

            String urlPattern = "((https?|ftp|gopher|telnet|file|Unsure|http):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
            Pattern p = Pattern.compile(urlPattern,Pattern.CASE_INSENSITIVE);
            //ToneAnalysis tone = new ToneAnalysis();
            List<twitter4j.Status> tweets;
            ConfigurationBuilder cb = new ConfigurationBuilder();

            cb.setDebugEnabled(true)
                    .setOAuthConsumerKey("LWY0wjNd6Yjgy8XN0xL0C0E8P")
                    .setOAuthConsumerSecret("FiIATMNToRlnekpBg4poXFaT2pNVyejIYDzveI2N9a1Ibjf4X7")
                    .setOAuthAccessToken("863833685346328576-xmfHmDYgEzI2ujeSwm2rcLpkBGwmfhI")
                    .setOAuthAccessTokenSecret("zr8y5BGEj4MrP43PcGeNoPsGjfQvkaITGh0uwjGCFGHKx");


            TwitterFactory tf = new TwitterFactory(cb.build());
            twitter4j.Twitter twitter = tf.getInstance();
            String s = "";
            int count = 0;
            try {
                Query query = new Query(strings[0]);
                QueryResult result;


                do {
                    result = twitter.search(query);
                    tweets = result.getTweets();
                    for (twitter4j.Status tweet : tweets) {
                        String withHyper = "";
                        if (tweet.getLang().equals("en")) {
                            count++;
                            withHyper = tweet.getText().toString();
                            Matcher m = p.matcher(withHyper);
                            int i = 0;
                            while (m.find()) {
                                withHyper = withHyper.replaceAll(m.group(i), "").trim();
                                i++;
                            }
                            s = s + withHyper;
                        }
                    }
                }
                while (count<50);

            } catch (twitter4j.TwitterException e) {
                e.printStackTrace();
            }
            return s;
        }


        @Override
        protected void onPostExecute(String string) {
            tweets = string;
            ToneAnalysisWatson.tweets = tweets;
            tweets_obtained = true;
            gettonesetpie();


        }

    }






}



