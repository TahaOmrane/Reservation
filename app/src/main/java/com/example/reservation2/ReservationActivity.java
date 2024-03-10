package com.example.reservation2;

import static com.example.reservation2.RestaurantActivity.getChoosenRestaurantName;
import static com.example.reservation2.RestaurantActivity.getDatabaseAccessName;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class ReservationActivity extends AppCompatActivity {

    DatabaseReference mDatabase;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;

    Button confirmReservationButton;
    Button backButton;
    EditText kazukiSushiNameInput;
    EditText kazukiSushiNumberInput;
    TextView firstCountText;
    TextView secondCountText;
    TextView thirdCountText;
    TextView choosenRestaurantText;

    String generatedReservationId;
    String dropdownSelectedValue;
    int firstCount = 30;
    int secondCount = 30;
    int thirdCount = 30;

    Spinner dropdown;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        dropdown = findViewById(R.id.timeSpinner);
        confirmReservationButton= (Button) findViewById(R.id.kazukiSushiConfirmReservationButton);
        backButton = (Button) findViewById(R.id.backButton);
        kazukiSushiNameInput = (EditText) findViewById(R.id.kazukiSushiNameInput);
        kazukiSushiNumberInput = (EditText) findViewById(R.id.kazukiSushiNumberInput);
        firstCountText = (TextView) findViewById(R.id.firstCountText);
        secondCountText = (TextView) findViewById(R.id.secondCountText);
        thirdCountText = (TextView) findViewById(R.id.thirdCountText);
        choosenRestaurantText = (TextView) findViewById(R.id.choosenRestaurantText);

        choosenRestaurantText.setText(getChoosenRestaurantName());

        String[] items = new String[]{"18:00-20:00", "20:00-22:00", "22:00-00:00"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);

        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?>arg0, View view, int arg2, long arg3) {
                dropdownSelectedValue =dropdown.getSelectedItem().toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });

        getReservationData();

        generatedReservationId = "Reservation" + java.util.UUID.randomUUID();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ReservationActivity.this, RestaurantActivity.class));
            }
        });

        confirmReservationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("firebase", String.valueOf(firstCount));
                if(!kazukiSushiNumberInput.getText().toString().equals("") && !dropdownSelectedValue.equals("") && !kazukiSushiNameInput.getText().toString().equals("")){
                    if(checkReservation(kazukiSushiNumberInput.getText().toString())){
                        writeNewReservation(generatedReservationId,currentUser.getEmail().toString(),kazukiSushiNameInput.getText().toString()
                                ,kazukiSushiNumberInput.getText().toString(), dropdownSelectedValue);
                        Toast.makeText(ReservationActivity.this, "Reservation erfolgreich!",
                                Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(ReservationActivity.this, RestaurantActivity.class));
                    } else {
                        Toast.makeText(ReservationActivity.this, "Nich mehr genügend plätze für den gewählten Zeitslot",
                                Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(ReservationActivity.this, "Alle informationen müssen angegeben werden",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void writeNewReservation(String reservationId, String email, String name, String number, String time) {
        Reservation reservation = new Reservation(email, name, number, time);
        mDatabase.child(getDatabaseAccessName()).child(reservationId).setValue(reservation);
    }

    public void getReservationData(){
        mDatabase.child(getDatabaseAccessName()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    DataSnapshot dataSnapshot = task.getResult();
                    Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                    List<DataSnapshot> snapshotList = new ArrayList<>();
                    children.forEach(snapshotList::add);

                    for (DataSnapshot d : snapshotList){
                        Reservation r = d.getValue(Reservation.class);
                        if(r.time.equals("18:00-20:00")){
                            int i = Integer.valueOf(r.number);
                            firstCount -= i;
                            Log.d("firebase", String.valueOf(firstCount));
                        } else if(r.time.equals("20:00-22:00")){
                            int i = Integer.valueOf(r.number);
                            secondCount -= i;
                            Log.d("firebase", String.valueOf(firstCount));
                        } else if(r.time.equals("22:00-00:00")){
                            int i = Integer.valueOf(r.number);
                            thirdCount -= i;
                            Log.d("firebase", String.valueOf(firstCount));
                        }
                    }
                    String firstMsg = "18:00-20:00 noch " + firstCount + " Plätze verfügbar";
                    firstCountText.setText(firstMsg);
                    String secondMsg = "20:00-22:00 noch " + secondCount + " Plätze verfügbar";
                    secondCountText.setText(secondMsg);
                    String thirdMsg = "22:00-00:00 noch " + thirdCount + " Plätze verfügbar";
                    thirdCountText.setText(thirdMsg);
                }
            }

        });
    }

    public boolean checkReservation(String number){
        if(dropdownSelectedValue.equals("18:00-20:00")){
            int count = firstCount - Integer.valueOf(number);
            if(count < 0){
                return false;
            }
        }else if(dropdownSelectedValue.equals("20:00-22:00")){
            int count = secondCount - Integer.valueOf(number);
            if(count < 0){
                return false;
            }
        }else if(dropdownSelectedValue.equals("22:00-00:00")){
            int count = thirdCount - Integer.valueOf(number);
            if(count < 0){
                return false;
            }
        }
        return true;
    }

}
