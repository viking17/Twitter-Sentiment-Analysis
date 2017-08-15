package msrit.microsoftstudent.com.twitteranalyser;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ibm.watson.developer_cloud.tone_analyzer.v3.ToneAnalyzer;
import com.ibm.watson.developer_cloud.tone_analyzer.v3.model.ToneAnalysis;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Trend;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class Trends extends AppCompatActivity implements TrendsRecyclerAdapter.Myclick {

    HashMap<String,Integer> trendMap;
    HashMap<String,String> trendStringMap;
    HashMap<String,ToneAnalysis> trendToneMap;
    ArrayList<String> TrendsList;
    FloatingActionButton fb;
    TrendsRecyclerAdapter adapter;
    RecyclerView recyclerview;

    int country_select = 0;
    TextView country_name,trend_set;
    String[] countries = {"india","england","usa","australia","canada", "spain","new zealand","germany","france","china"  };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trends);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);

        trendMap = new HashMap<>();
        trendStringMap = new HashMap<>();
        trendToneMap = new HashMap<>();
        country_name = (TextView)findViewById(R.id.country_name);
        trend_set = (TextView)findViewById(R.id.trend_set);
        fb = (FloatingActionButton)findViewById(R.id.country_select);
        recyclerview = (RecyclerView)findViewById(R.id.recyclerview);

        trendMap.put("india",       23424848);
        trendMap.put("england",    24554868);
        trendMap.put("usa",         23424977);
        trendMap.put("australia",   23424748);
        trendMap.put("canada",      23424775);
        trendMap.put("spain",       23424950);
        trendMap.put("new zealand", 23424916);
        trendMap.put("germany",     23424829);
        trendMap.put("france",      23424819);
        trendMap.put("china",      23424781);


        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(Trends.this);
                LayoutInflater inflater = (LayoutInflater)Trends.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View theView = inflater.inflate(R.layout.country_select, null);

                final Spinner sp = (Spinner)theView.findViewById(R.id.spinner);
                ArrayAdapter<String> adap21 = new ArrayAdapter<>(Trends.this, android.R.layout.simple_spinner_item, countries);
                adap21.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                sp.setAdapter(adap21);

                builder.setTitle("Choose Country");
                builder.setView(theView)
                        .setPositiveButton("Set",new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                int country = sp.getSelectedItemPosition();
                                country_select = country;
                                country_name.setText(countries[country]);
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.show();
            }
        });
    }



    public void TrendsForCountry(int item){


        taskTrendAll tl = new taskTrendAll();
        tl.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR,countries[item]);

    }

    public void start(View view) {
        TrendsForCountry(country_select);
    }

    public void start_analysis(View view) {
        taskLocationAll t1 = new taskLocationAll();
        t1.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,TrendsList.get(0),TrendsList.get(1),TrendsList.get(2),TrendsList.get(3),TrendsList.get(4),TrendsList.get(5),TrendsList.get(6),TrendsList.get(7),TrendsList.get(8),TrendsList.get(9));
    }


    public class taskTrendAll extends AsyncTask<String, String, ArrayList<String> >
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }
        @Override
        protected ArrayList doInBackground(String... strings) {

            String urlPattern = "((https?|ftp|gopher|telnet|file|Unsure|http):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
            Pattern p = Pattern.compile(urlPattern, Pattern.CASE_INSENSITIVE);
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
            ArrayList<String>  s= new ArrayList<>();
            try {
                twitter4j.Trends trends = twitter.getPlaceTrends(trendMap.get(strings[0]));
                int count = 0;

                for(Trend trend : trends.getTrends()){
                    if(count < 10){
                        s.add(trend.getName());
                        count++;
                    }
                }
            } catch (TwitterException e) {
                e.printStackTrace();
            }
            return s;
        }
        @Override
        protected void onPostExecute(ArrayList<String> s) {
            getTrendAnalysis(s);
        }
    }

    public void getTrendAnalysis(ArrayList<String> s) {
        TrendsList = s;
        trend_set.append(TrendsList.toString());
    }

    public void setadapter() {
        recyclerview.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerview.setLayoutManager(layoutManager);

        adapter = new TrendsRecyclerAdapter(trendToneMap,TrendsList);
        adapter.setMyclick(this);
        recyclerview.setAdapter(adapter);

    }

    public class taskLocationAll extends AsyncTask<String, String, String>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }
        @Override
        protected String doInBackground(String... strings) {

            String urlPattern = "((https?|ftp|gopher|telnet|file|Unsure|http):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
            Pattern p = Pattern.compile(urlPattern, Pattern.CASE_INSENSITIVE);
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

            for (int cr = 0; cr < strings.length; cr++) {

                String s = "";
                try {
                    Query query = new Query(strings[cr]);
                    query.setCount(100);
                    QueryResult result;

                    result = twitter.search(query);
                    tweets = result.getTweets();
                    for (twitter4j.Status tweet : tweets) {
                        String withHyper = "";

                        if (tweet.getLang().equals("en")) {
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
                } catch (twitter4j.TwitterException e) {
                    e.printStackTrace();
                }
                publishProgress(strings[cr],s);
            }
            return "lol";
        }
        @Override
        protected void onPostExecute(String string) {
            setadapter();
        }

        @Override
        protected void onProgressUpdate(String... values) {
            getTrendTone(values[0],values[1]);
        }
    }

    public void getTrendTone(String trendname,String string) {

        ToneAnalyzer service = new ToneAnalyzer(ToneAnalyzer.VERSION_DATE_2016_05_19);
        service.setUsernameAndPassword("630971f3-0db7-4d4d-9982-c911ff0bff4a", "g1jRCEEXqYZK");
        ToneAnalysis tone;
        tone = service.getTone(string, null).execute();

        trendToneMap.put(trendname,tone);

        //trend_set.append("\n"+trendname+" \n "+tone.getSentencesTone().size());
    }

    @Override
    public void afterclick(int pos) {
        Toast.makeText(this,"clicked  "+pos,Toast.LENGTH_SHORT).show();

        if(pos==0){
            ToneAnalysisWatson toneAnalysisWatson = new ToneAnalysisWatson();
            toneAnalysisWatson.tone = trendToneMap.get(TrendsList.get(0));
            toneAnalysisWatson.setdocumenttones();
            Intent i = new Intent(Trends.this,TrendsDetailed.class);
            startActivity(i);
        }
        else if(pos==1){
            ToneAnalysisWatson toneAnalysisWatson = new ToneAnalysisWatson();
            toneAnalysisWatson.tone = trendToneMap.get(TrendsList.get(1));
            toneAnalysisWatson.setdocumenttones();
            Intent i = new Intent(Trends.this,TrendsDetailed.class);
            startActivity(i);
        }
        else if(pos==2){
            ToneAnalysisWatson toneAnalysisWatson = new ToneAnalysisWatson();
            toneAnalysisWatson.tone = trendToneMap.get(TrendsList.get(2));
            toneAnalysisWatson.setdocumenttones();
            Intent i = new Intent(Trends.this,TrendsDetailed.class);
            startActivity(i);
        }
        else if(pos==3){
            ToneAnalysisWatson toneAnalysisWatson = new ToneAnalysisWatson();
            toneAnalysisWatson.tone = trendToneMap.get(TrendsList.get(3));
            toneAnalysisWatson.setdocumenttones();
            Intent i = new Intent(Trends.this,TrendsDetailed.class);
            startActivity(i);
        }
        else if(pos==4){
            ToneAnalysisWatson toneAnalysisWatson = new ToneAnalysisWatson();
            toneAnalysisWatson.tone = trendToneMap.get(TrendsList.get(4));
            toneAnalysisWatson.setdocumenttones();
            Intent i = new Intent(Trends.this,TrendsDetailed.class);
            startActivity(i);
        }
        else if(pos==5){
            ToneAnalysisWatson toneAnalysisWatson = new ToneAnalysisWatson();
            toneAnalysisWatson.tone = trendToneMap.get(TrendsList.get(5));
            toneAnalysisWatson.setdocumenttones();
            Intent i = new Intent(Trends.this,TrendsDetailed.class);
            startActivity(i);
        }
        else if(pos==6){
            ToneAnalysisWatson toneAnalysisWatson = new ToneAnalysisWatson();
            toneAnalysisWatson.tone = trendToneMap.get(TrendsList.get(6));
            toneAnalysisWatson.setdocumenttones();
            Intent i = new Intent(Trends.this,TrendsDetailed.class);
            startActivity(i);
        }
        else if(pos==7){
            ToneAnalysisWatson toneAnalysisWatson = new ToneAnalysisWatson();
            toneAnalysisWatson.tone = trendToneMap.get(TrendsList.get(7));
            toneAnalysisWatson.setdocumenttones();
            Intent i = new Intent(Trends.this,TrendsDetailed.class);
            startActivity(i);
        }
        else if(pos==8){
            ToneAnalysisWatson toneAnalysisWatson = new ToneAnalysisWatson();
            toneAnalysisWatson.tone = trendToneMap.get(TrendsList.get(8));
            toneAnalysisWatson.setdocumenttones();
            Intent i = new Intent(Trends.this,TrendsDetailed.class);
            startActivity(i);
        }
        else if(pos==9){
            ToneAnalysisWatson toneAnalysisWatson = new ToneAnalysisWatson();
            toneAnalysisWatson.tone = trendToneMap.get(TrendsList.get(9));
            toneAnalysisWatson.setdocumenttones();
            Intent i = new Intent(Trends.this,TrendsDetailed.class);
            startActivity(i);
        }
    }

}
