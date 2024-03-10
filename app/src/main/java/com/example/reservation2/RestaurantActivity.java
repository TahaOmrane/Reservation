package com.example.reservation2;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class RestaurantActivity extends AppCompatActivity {

    Button kazukiSushiButton;
    Button luluHummusButton;
    Button mioBarelloButton;
    Button signoutButton;
    TextView dateText;

    String currentDate;
    static String choosenRestaurantName;
    static String databaseAccessName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);

        kazukiSushiButton = (Button) findViewById(R.id.kazukiSushiButton);
        luluHummusButton = (Button) findViewById(R.id.luluHummusButton);
        mioBarelloButton = (Button) findViewById(R.id.mioBarelloButton);
        signoutButton = (Button) findViewById(R.id.signoutButton);
        dateText = (TextView) findViewById(R.id.dateText);

        currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

        dateText.setText("Reservierungen für den " + currentDate.toString());

        kazukiSushiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RestaurantActivity.this, KazukiSushiActivity.class));
                choosenRestaurantName = "Kazuki Sushi";
                databaseAccessName = "kazukiSushi";
            }
        });

        luluHummusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RestaurantActivity.this, KazukiSushiActivity.class));
                choosenRestaurantName = "Lulu Hummus";
                databaseAccessName = "luluHummus";
            }
        });

         mioBarelloButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RestaurantActivity.this, KazukiSushiActivity.class));
                choosenRestaurantName = "Mio Barello";
                databaseAccessName = "mioBarello";
            }
        });

        signoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(RestaurantActivity.this, LoginActivity.class));
            }
        });
    }
    public static String getChoosenRestaurantName(){
        return choosenRestaurantName;
    }

    public static String getDatabaseAccessName(){
        return databaseAccessName;
    }
}

