package com.checkfuel.frontend;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.checkfuel.models.Post;
import com.checkfuel.something.R;
import com.checkfuel.utils.DatabaseManager;

import org.jetbrains.annotations.NotNull;

public class StationOkko extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_station_okko);
    }

    public void doCompare(View view) {
        Post post = DatabaseManager.getPost();

        if (post == null) {
            Toast toast = Toast.makeText(this, "Failed to connect to database, \n please check the internet connection or sign in in your user account", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            return;
        }

        double weight = post.getWeight();
        double realVolume = post.getVolumeFill();
        double temperature = post.getTemperature();
        double density = 1000 * (weight / realVolume) / (1 + temperature * 0.001);

        Intent intent = new Intent();

        if (density <= 800) {
            intent.setClass(StationOkko.this, GoodFuel.class);
        } else {
            intent.setClass(StationOkko.this, BadFuel.class);
        }
        intent.putExtra("density", density);
        startActivity(intent);
    }

    public void backToStations(@NotNull View view) {
        if (view.getId() == R.id.btnback) {
            Intent intent = new Intent(this, ChooseGasStation.class);
            startActivity(intent);
        }

    }


}
