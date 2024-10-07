import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;



public class MainActivity extends AppCompatActivity {

    private FusedLocationProviderClient fusedLocationClient;
    private Location staticLocation;
    private TextView locationTextView;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationTextView = findViewById(R.id.locationTextView);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        sharedPreferences = getSharedPreferences("StaticLocationPrefs", MODE_PRIVATE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            getLastLocation();
        }
    }

    private void getLastLocation() {
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    staticLocation = location;
                    saveLocation(staticLocation);
                    displayLocation(staticLocation);
                } else {
                    loadLocation();
                }
            }
        });
    }

    private void saveLocation(Location location) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("latitude", String.valueOf(location.getLatitude()));
        editor.putString("longitude", String.valueOf(location.getLongitude()));
        editor.apply();
    }

    private void loadLocation() {
        String latitude = sharedPreferences.getString("latitude", "0.0"); #check what are the positional attributes
        String longitude = sharedPreferences.getString("longitude", "0.0");
        staticLocation = new Location("");
        staticLocation.setLatitude(Double.parseDouble(latitude));
        staticLocation.setLongitude(Double.parseDouble(longitude));
        displayLocation(staticLocation);
    }

    private void displayLocation(Location location) {
        locationTextView.setText("Latitude: " + location.getLatitude() + ", Longitude: " + location.getLongitude());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
        }
    }
}
