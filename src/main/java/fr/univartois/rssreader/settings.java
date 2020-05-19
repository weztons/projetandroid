package fr.univartois.rssreader;

import android.app.AppComponentFactory;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.Tag;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.navigation.fragment.NavHostFragment;

import org.w3c.dom.Text;

public class settings extends AppCompatActivity {

    EditText texte;
    Button btnValib;
    SharedPreferences sharedPreferences;

    private static final String SHARE_PREF_NAME = "myprefs";
    private static final String KEY_URL = "name";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting);

        getSupportActionBar().setTitle("Paramètres");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        texte = (EditText) findViewById(R.id.texte);
        btnValib = (Button) findViewById(R.id.btnValid);

        sharedPreferences = getSharedPreferences(SHARE_PREF_NAME, MODE_PRIVATE);


        btnValib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(KEY_URL, texte.getText().toString());
                editor.apply();
                Intent intent = new Intent(settings.this, MainActivity.class);
                startActivity(intent);
                Toast.makeText(settings.this, "Changement effectué", Toast.LENGTH_LONG).show();
            }
        });

    }



}
