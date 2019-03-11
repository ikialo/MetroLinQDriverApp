package com.example.isaac.metrolinqdriverapp;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    Button complete_btn, accept_btn;
    AutoCompleteTextView editText;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private DatabaseReference mDatabase,clientNameDB,requestDB_name;
    private TextView carName;

    private Double lat, lon;
    private boolean checkLocation = false;
    private List<String> clientList;
    String car = "noCar";

    private static final int PERMISSIONS_REQUEST = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        mDatabase = FirebaseDatabase.getInstance().getReference("Car Location");



        accept_btn = findViewById(R.id.signIn);
        editText = findViewById(R.id.actv);
        carName = findViewById(R.id.carNameTV);

        /// change this data base with the one you use to store all client names
        clientNameDB = FirebaseDatabase.getInstance().getReference("Cars");

        clientList = new ArrayList<>();






        clientNameDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                clientList.clear();

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){

                    if (postSnapshot!= null ) {

                            String name = postSnapshot.child("plateNumber").getValue().toString();
                            clientList.add(name);

                    }

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_expandable_list_item_1,clientList);
        editText.setAdapter(adapter);



        accept_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                clientNameDB.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for (DataSnapshot postSnapshot: dataSnapshot.getChildren()){

                            if (postSnapshot.child("plateNumber").getValue().equals(editText.getText().toString())){

                                car = editText.getText().toString();

                                clientNameDB.child(postSnapshot.getKey()).child("inUse").setValue("true");


                                carName.setText(editText.getText());
                                break;

                            }
                            else if(editText.getText().toString().equals("noCar")){

                                if (postSnapshot.child("plateNumber").getValue().equals(car)){

                                    clientNameDB.child(postSnapshot.getKey()).child("inUse").setValue("false");

                                }

                                car = "noCar";

                            }
                            else{


                                car = "noCar";
                                carName.setText(car);
                            }


                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
//
//                for (int i=0; i<clientList.size(); i++ ){
//
//
//
//
//                }
            }
        });


        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            finish();
        }

        //Check whether this app has access to the location permission//


        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);

//If the location permission has been granted, then start the TrackerService//

        if (permission == PackageManager.PERMISSION_GRANTED) {
            startTrackerService();
        } else {

//If the app doesn’t currently have access to the user’s location, then request access//

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST);
        }


//
//
//        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
//
//        locationListener = new LocationListener() {
//            @Override
//            public void onLocationChanged(Location location) {
//
//
//                Log.d("WHICH_CAR", "onLocationChanged: "+ car);
//                if (!car.equals("noCar")) {
//
//
//                    carName.setText(location.getLatitude()+","+location.getLongitude());
//                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
//
//                    mDatabase.child("LATLONG").child(car).setValue(latLng);
//
//                }
//
//            }
//
//            @Override
//            public void onStatusChanged(String provider, int status, Bundle extras) {
//
//            }
//
//            @Override
//            public void onProviderEnabled(String provider) {
//
//            }
//
//            @Override
//            public void onProviderDisabled(String provider) {
//
//            }
//        };
//
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//
//            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
//            return;
//        }
//        else{
//
//        }
//        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
//
////        complete_btn.setOnClickListener(this);
////        accept_btn.setOnClickListener(this);

    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//
//        if (grantResults.length >0 && grantResults[0] ==PackageManager.PERMISSION_GRANTED){
//
//
//            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
//                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
//
//            }
//        }
//    }
//
//    @Override
//    public void onClick(View v) {
//
//        if(v == complete_btn){
//
//            checkLocation = true;
//
//
//            //startService(new Intent(this, MetroService.class));
//        }else if(v == accept_btn){
//            // stopService(new Intent(this, MetroService.class));
//        }
//
//    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[]
            grantResults) {

//If the permission has been granted...//

        if (requestCode == PERMISSIONS_REQUEST && grantResults.length == 1
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            //...then start the GPS tracking service//

            startTrackerService();
        } else {

//If the user denies the permission request, then display a toast with some more information//

            Toast.makeText(this, "Please enable location services to allow GPS tracking", Toast.LENGTH_SHORT).show();
        }
    }

//Start the TrackerService//


    private void startTrackerService() {


            startService(new Intent(this, TrackingService.class));


////Notify the user that tracking has been enabled//

        Toast.makeText(this, "GPS tracking enabled", Toast.LENGTH_SHORT).show();

//Close MainActivity//

        // finish();
    }

}
