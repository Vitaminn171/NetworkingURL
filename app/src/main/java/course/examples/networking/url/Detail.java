package course.examples.networking.url;

import android.app.ActionBar;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Objects;

/*
Tên: Lý Quốc An
MSSV: 3119410002
 */



public class Detail extends NetworkingURLActivity {
    private TextView textViewCountryName,textViewPopulation,textViewAreaInSqKm,textViewCapital;
    private ImageView imageViewFlag,imageViewMap;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail);
        textViewCountryName = findViewById(R.id.textViewCountryName);
        textViewPopulation = findViewById(R.id.textViewPopulation);
        textViewAreaInSqKm = findViewById(R.id.textViewAreaInSqKm);
        textViewCapital = findViewById(R.id.textViewCapital);
        imageViewFlag = findViewById(R.id.imageViewFlag);
        imageViewMap = findViewById(R.id.imageViewMap);

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        setDataFromIntent();
    }
    private void setDataFromIntent() {
        String countryName = (String) getIntent().getSerializableExtra("countryName");
        String population = (String) getIntent().getSerializableExtra("population");
        String areaInSqKm = (String) getIntent().getSerializableExtra("areaInSqKm");
        String capital = (String) getIntent().getSerializableExtra("capital");
        String urlMapImage = (String) getIntent().getSerializableExtra("urlMapImage");
        String urlFlagImage = (String) getIntent().getSerializableExtra("urlFlagImage");

        int numberPopulation = Integer.parseInt(population);
        int numberAreaInSqKm = Integer.parseInt(areaInSqKm.replace(".0", ""));

        DecimalFormat formatter = new DecimalFormat("#,###,###");
        String populationFormat = formatter.format(numberPopulation);
        String areaInSqKmFormat = formatter.format(numberAreaInSqKm);

        textViewCountryName.setText(countryName);
        textViewPopulation.setText(populationFormat);
        textViewAreaInSqKm.setText(areaInSqKmFormat);
        textViewCapital.setText(capital);

        Glide.with(getApplicationContext())
                .load(urlMapImage)
                .into(imageViewMap);
        Glide.with(getApplicationContext())
                .load(urlFlagImage)
                .into(imageViewFlag);

    }

    public boolean onOptionsItemSelected(MenuItem item){
        finish();
        return true;
    }

}
