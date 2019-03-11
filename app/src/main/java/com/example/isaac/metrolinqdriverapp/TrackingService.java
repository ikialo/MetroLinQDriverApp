package com.example.isaac.metrolinqdriverapp;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class TrackingService extends Service {
    private static final String TAG = TrackingService.class.getSimpleName();

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

       // startForeground(101, getNotification());
        buildNotification();
        requestLocationUpdates();
    }

//Create the persistent notification//

    private void buildNotification() {

        String chanID;


        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            chanID = createNotificationChannelId("My_service", "my_background_service");

        }
        else{
            chanID = "";
        }

        String stop = "stop";
        registerReceiver(stopReceiver, new IntentFilter(stop));
        PendingIntent broadcastIntent = PendingIntent.getBroadcast(
                this, 0, new Intent(stop), PendingIntent.FLAG_UPDATE_CURRENT);
        // Create the persistent notification


        Notification.Builder builder = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            builder = new Notification.Builder(this, chanID)//Notification.Builder(this)
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText("the")


    //Make this notification ongoing so it can’t be dismissed by the user//

                    .setOngoing(true)
                    .setContentIntent(broadcastIntent)
                    .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark);
        }
        startForeground(1025, builder.build());



    }

    //Create the persistent notification//

//    private void buildNotification() {
//        String stop = "stop";
//        registerReceiver(stopReceiver, new IntentFilter(stop));
//        PendingIntent broadcastIntent = PendingIntent.getBroadcast(
//                this, 0, new Intent(stop), PendingIntent.FLAG_UPDATE_CURRENT);
//
//// Create the persistent notification//
//        Notification.Builder builder = new Notification.Builder(this)
//                .setContentTitle(getString(R.string.app_name))
//                .setContentText("tracking")
//
////Make this notification ongoing so it can’t be dismissed by the user//
//
//                .setOngoing(true)
//                .setContentIntent(broadcastIntent)
//                .setSmallIcon(R.drawable.ic_launcher_background);
//        startForeground(1, builder.build());
//    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private String createNotificationChannelId(String channelid, String channelName) {

        NotificationChannel chan = new NotificationChannel(channelid, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.YELLOW);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

       NotificationManager service =  getSystemService(NotificationManager.class);

       service.createNotificationChannel(chan);

       return channelid;


    }

    protected BroadcastReceiver stopReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

//Unregister the BroadcastReceiver when the notification is tapped//

            unregisterReceiver(stopReceiver);

//Stop the Service//

            //
            stopSelf();
        }
    };


//    private Notification getNotification() {
//        NotificationChannel channel = null;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            channel = new NotificationChannel(
//                    "channel_01",
//                    "My Channel",
//                    NotificationManager.IMPORTANCE_DEFAULT
//            );
//        }
//
//        NotificationManager notificationManager = null;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            notificationManager = getSystemService(NotificationManager.class);
//
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                notificationManager.createNotificationChannel(channel);
//            }
//        }
//
//        Notification.Builder builder = null;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            builder = new Notification.Builder(getApplicationContext(), "channel_01");
//        }
//
//        return builder.build();
//    }

    private void loginToFirebase() {

//Authenticate with Firebase, using the email and password we created earlier//

        // String email = getString(R.string.test_email);
        //String password = getString(R.string.test_password);

//Call OnCompleteListener if the user is signed in successfully//


//        FirebaseAuth.getInstance().signInWithEmailAndPassword(
//                email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//            @Override
//            public void onComplete(Task<AuthResult> task) {

//If the user has been authenticated...//

        //   if (task.isSuccessful()) {

//...then call requestLocationUpdates//

        requestLocationUpdates();
        //  } else {

//If sign in fails, then log the error//

        //       Log.d(TAG, "Firebase authentication failed");
        //     }
        // }
        //  });
    }

//Initiate the request to track the device's location//

    private void requestLocationUpdates() {
        LocationRequest request = new LocationRequest();

//Specify how often your app should request the device’s location//

        request.setInterval(1000);

//Get the most accurate location data available//

        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(this);
        final String path = "Bus Location";
        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);

//If the app currently has access to the location permission...//

        if (permission == PackageManager.PERMISSION_GRANTED) {

//...then request location updates//

            client.requestLocationUpdates(request, new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {

//Get a reference to the database, so your app can perform read and write operations//

                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference(path);
                    Location location = locationResult.getLastLocation();
                    if (location != null) {

//Save the location data to the database//

                        LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());

                        Log.d("TEST", "onLocationResult: "+ location.getLongitude());
                        ref.setValue(latLng);
                    }
                }
            }, null);
        }
    }
}
