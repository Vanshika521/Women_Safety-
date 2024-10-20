package com.android_development.women_safety;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import java.util.List;

public class Second extends AppCompatActivity {
    ImageButton btn1, btn2, btn3, btn4, btn5;
    MediaPlayer mediaPlayer;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_second);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btn1 = findViewById(R.id.img1);
        btn2 = findViewById(R.id.img2);
        btn3 = findViewById(R.id.img3);
        btn4 = findViewById(R.id.img4);
        btn5 = findViewById(R.id.img5);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Second.this, register.class);
                startActivity(intent);
            }
        });



        // Emergency call button functionality
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emergencyNumber = "tel:9729943453"; // Replace with actual emergency number
                // Check for call permission
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                    // Start the phone call
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse(emergencyNumber));
                    try {
                        startActivity(callIntent);
                    } catch (SecurityException e) {
                        Toast.makeText(getApplicationContext(), "Permission to make calls is denied.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Request permission if not granted
                    ActivityCompat.requestPermissions(Second.this, new String[]{Manifest.permission.CALL_PHONE}, 1);
                }
            }
        });

                // Send SMS button functionality
                btn3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String phoneNumber = "9729943453"; // Replace with the recipient's phone number
                        String message = "This is a safety alert! I need help at my current location."; // Customize the message

                        // Check for SMS permission
                        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.SEND_SMS)
                                == PackageManager.PERMISSION_GRANTED) {
                            sendSms(phoneNumber, message);
                        } else {
                            // Request SMS permission if not granted
                            ActivityCompat.requestPermissions(Second.this, new String[]{Manifest.permission.SEND_SMS}, 2);
                        }
                    }
                });


                mediaPlayer = MediaPlayer.create(this, R.raw.siren);

        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaPlayer != null) {
                    mediaPlayer.start(); // Play the sound
                }
            }
        });


        // Share location button functionality
        btn5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Check for location permission
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    fusedLocationClient.getLastLocation()
                            .addOnSuccessListener(Second.this, location -> {
                                if (location != null) {
                                    double latitude = location.getLatitude();
                                    double longitude = location.getLongitude();
                                    String message = "I need help! My current location: https://www.google.com/maps?q=" + latitude + "," + longitude;

                                    // Send the SMS
                                    sendSms("9729943453", message); // Replace with the recipient's phone number
                                } else {
                                    Toast.makeText(getApplicationContext(), "Unable to get current location", Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    // Request location permission if not granted
                    ActivityCompat.requestPermissions(Second.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 3);
                }
            }
        });


    }

    @SuppressWarnings("deprecation")
    private void sendSms(String phoneNumber, String message) {
        // Check for phone state permission
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_PHONE_STATE)
                == PackageManager.PERMISSION_GRANTED) {

            // Get the default SmsManager instance (This is recommended as it avoids subscription ID issues)
            SmsManager smsManager = SmsManager.getDefault();

            // Send the SMS
            smsManager.sendTextMessage(phoneNumber, null, message, null, null);
            Toast.makeText(getApplicationContext(), "SMS sent successfully", Toast.LENGTH_SHORT).show();

        } else {
            // Request READ_PHONE_STATE permission if not granted
            ActivityCompat.requestPermissions(Second.this, new String[]{Manifest.permission.READ_PHONE_STATE}, 4);
        }
    }





    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                String emergencyNumber = "tel:9729943453";
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse(emergencyNumber));
                try {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                        startActivity(callIntent);
                    }
                } catch (SecurityException e) {
                    Toast.makeText(this, "Permission to make phone calls denied.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Permission to make phone calls denied.", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == 2) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                String phoneNumber = "9729943453";
                String message = "This is a safety alert! I need help at my current location.";
                sendSms(phoneNumber, message);
            } else {
                Toast.makeText(this, "Permission to send SMS denied.", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == 3) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted for location
                // You may want to trigger the location sharing here if necessary
            } else {
                Toast.makeText(this, "Permission to access location denied.", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == 4) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted for READ_PHONE_STATE
                // You can re-trigger the SMS sending if needed
            } else {
                Toast.makeText(this, "Permission to read phone state denied.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release(); // Release the MediaPlayer resources
            mediaPlayer = null;
        }
    }
}
