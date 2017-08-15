package msrit.microsoftstudent.com.twitteranalyser;


import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    Button search;
    Button geosearch,trends;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        search = (Button)findViewById(R.id.search);
        geosearch = (Button)findViewById(R.id.geosearch);
        trends = (Button)findViewById(R.id.trends);

        geosearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isNetworkAvailable()){
                    Intent i = new Intent(MainActivity.this,GeoSearch.class);
                    startActivity(i);
                }
                else {
                    Toast.makeText(MainActivity.this,"No internet connection",Toast.LENGTH_SHORT).show();
                }

            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isNetworkAvailable()){
                    Intent i = new Intent(MainActivity.this,Search.class);
                    startActivity(i);
                }
                else {
                    Toast.makeText(MainActivity.this,"No internet connection",Toast.LENGTH_SHORT).show();
                }


            }
        });

        trends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isNetworkAvailable()){
                    Intent i = new Intent(MainActivity.this,Trends.class);
                    startActivity(i);
                }
                else {
                    Toast.makeText(MainActivity.this,"No internet connection",Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}



