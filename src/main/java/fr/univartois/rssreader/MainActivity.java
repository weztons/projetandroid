package fr.univartois.rssreader;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.shape.CornerFamily;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Xml;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    String url;
    SharedPreferences sharedPreferences;
    private static final String SHARE_PREF_NAME = "myprefs";
    private static final String KEY_URL = "name";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.load_btn);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Chargement des news", Snackbar.LENGTH_LONG).show();

                Downloader downloader = new Downloader();
                downloader.start();
            }
        } );

        ListView listView = findViewById(R.id.list_view);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                RssItem rssItem = (RssItem) adapterView.getItemAtPosition(position);

                Intent intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(rssItem.getLink()));
                startActivity(intent);

            }
        });

        sharedPreferences = getSharedPreferences(SHARE_PREF_NAME, MODE_PRIVATE);
        String name = sharedPreferences.getString(KEY_URL, null);

        if(name != null){
            url = name;
        }



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        lauchsettings();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void lauchsettings(){
        Intent myIntent = new Intent(MainActivity.this, settings.class);
        this.startActivity(myIntent);
    }


    class Downloader extends Thread{
        @Override
        public void run() {
            try {
                InputStream stream = new URL(url).openConnection().getInputStream();
                List<RssItem> news = new ArrayList<>();
                XmlPullParser parser = Xml.newPullParser();
                parser.setInput(stream, null);
                int eventType = parser.getEventType();
                boolean done = false;
                RssItem item = null;
                while (eventType != XmlPullParser.END_DOCUMENT && !done) {
                    String name = null;
                    switch (eventType) {
                        case XmlPullParser.START_DOCUMENT:
                            break;
                        case XmlPullParser.START_TAG:
                            name = parser.getName();
                            if (name.equalsIgnoreCase("item")) {
                                item = new RssItem();
                            } else if (item != null) {
                                if (name.equalsIgnoreCase("link")) {
                                    item.setLink(parser.nextText());
                                } else if (name.equalsIgnoreCase("description")) {
                                    item.setDescription( parser.nextText().trim() );
                                } else if (name.equalsIgnoreCase("pubDate")) {
                                    item.setPubDate(parser.nextText());
                                } else if (name.equalsIgnoreCase("title")) {
                                    item.setTitle( parser.nextText().trim() );
                                }
                            }
                            break;
                        case XmlPullParser.END_TAG:
                            name = parser.getName();
                            if (name.equalsIgnoreCase("item") && item != null) {
                                news.add(item);
                            } else if (name.equalsIgnoreCase("channel")) {
                                done = true;
                            }
                            break;
                    }
                    eventType = parser.next();
                }


                final RssItemAdapter adapter = new RssItemAdapter(
                        getApplicationContext(),
                        news );

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ListView listView = findViewById(R.id.list_view);
                        listView.setAdapter(adapter);
                    }
                });


            } catch (IOException e) {
                e.printStackTrace();
            }
            catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }
    }
}
