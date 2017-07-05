package gomez.alejandro.teconfortasdatos;


import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

/**
 * Created by ALEJANDRO on 04/10/2016.
 */
public class ubicacion implements LocationListener {
    public double lat=0;
    public double lo=0;
    private Context ctx;
    protected Location mCurrentLocation;
    LocationManager locationManager;
    protected String mLastUpdateTime;
    String proveedor;
    boolean networkOn;

    public ubicacion(Context ctx) {
        this.ctx = ctx;
        locationManager= (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);
        proveedor=LocationManager.NETWORK_PROVIDER;
        networkOn=locationManager.isProviderEnabled(proveedor);
        if (ContextCompat.checkSelfPermission(ctx, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            locationManager.requestLocationUpdates(proveedor,1000,5,this);
            getLocation();
        } else {
            // Show rationale and request permission.
        }

    }
    private void getLocation(){
        if (networkOn){
            if (ContextCompat.checkSelfPermission(ctx, android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {

                Location lc=locationManager.getLastKnownLocation(proveedor);
                if (lc!=null){
                    lat=lc.getLatitude();
                    lo=lc.getLongitude();
                    StringBuilder builder=new StringBuilder();
                    builder.append("Latitud: ").append(lc.getLatitude());
                    builder.append("Longitud: ").append(lc.getLongitude());
                   // Toast.makeText(ctx,builder.toString(),Toast.LENGTH_LONG).show();
                }
            } else {
                // Show rationale and request permission.
            }


        }
    }


    @Override
    public void onLocationChanged(Location location) {
        getLocation();

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }


}
