package pw.ute.my_project_ute;

import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String LOG_TAG = "MAP_ACTIVITY_LOG";

    private static final String ORANGE_API_URL = "https://apitest.orange.pl/Localization/v1/GeoLocation?msisdn=48503610796&apikey=qr1d7R3Ag3gop06s1bzRuySh7fxukfSA";
    private static final String API_KEY_DANE_PO_WARSZAWSKU = "7e07d495-24db-4f5a-8a7a-af13da0acc6f";
    private static final String ID_BOISKA_DANE_PO_WARSZAWSKU = "d80c7139-f34d-418e-b867-977d7eac4db1";

    private GoogleMap mMap;

    private LocationManager locationManager;
    android.location.LocationListener locationListener;

    private float latitude;
    private float longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        if (locationManager != null) {
            try {
                locationManager.removeUpdates(locationListener);
            } catch (Exception ex) {
                Log.i("T", "fail to remove location listners, ignore", ex);
            }

        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng warsaw = new LatLng(52.2296756, 21.012228999999934);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(warsaw));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(12.0f));

        //Uzycie API Orange
        orangeApiLokalization(ORANGE_API_URL);

    }

    /**
     * Lokalizacja za pomoca orange api
     * @return
     */
    private void orangeApiLokalization(String url){

        RequestQueue queue = Volley.newRequestQueue(this);

        final JSONObject[] responseJson = {null};
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            responseJson[0] = response;
                            Log.d(LOG_TAG, response.toString());
                            String sLatitude = response.getString("latitude");
                            String sLongitude = response.getString("longitude");

                            latitude = parseLat(sLatitude);
                            longitude = parseLng(sLongitude);

                            LatLng pos = new LatLng(latitude,longitude);
                            mMap.addMarker(new MarkerOptions().position(pos));
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(pos));
                            mMap.animateCamera(CameraUpdateFactory.zoomTo( 15.0f ));

                            showGymsOnMap(latitude, longitude);
                            showClubsOnMap(latitude, longitude);
                            showStadiumsOnMap(latitude, longitude);

                        } catch(JSONException exc) {
                            Log.e(LOG_TAG, "JSON Exception", exc);
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast toast = Toast.makeText(getApplicationContext(), "Didn't work", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }
        );

        // add it to the RequestQueue
        queue.add(getRequest);

    }

    /**
     * Parsuje dane lokalizacji z Orange API response
     * @param latitude
     * @return
     */
    private float parseLat(String latitude){
        if (latitude.contains("N")){
            return Float.parseFloat(latitude.substring(0, latitude.length() - 1));
        } else {
            return 0 - Float.parseFloat(latitude.substring(0, latitude.length() - 1));
        }
    }

    /**
     * Parsuje dane lokalizacji z Orange API response
     * @param longtitude
     * @return
     */
    private float parseLng(String longtitude){
        if (longtitude.contains("E")){
            return Float.parseFloat(longtitude.substring(0, longtitude.length() -1 ));
        } else {
            return 0 - Float.parseFloat(longtitude.substring(0, longtitude.length() -1));
        }
    }

    /**
     * Zaznacza miejsca na mapie
     * @param response
     * @param markerColor
     */
    private void putPlacesOnMap(JSONObject response, float markerColor){
        try {
            JSONArray array = response.getJSONArray("results");
            for (int i = 0; i < array.length(); i++) {
                GooglePlace googlePlace = new GooglePlace(array.getJSONObject(i));
                mMap.addMarker(new MarkerOptions()
                        .position(googlePlace.latLng)
                        .title(googlePlace.placeName)
                        .snippet(googlePlace.address)
                        .icon(BitmapDescriptorFactory.defaultMarker(markerColor)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Pobieranie obiektow sportowych i klubow w okolicy
     * @param latitude
     * @param longitude
     */
    private void showGymsOnMap(double latitude, double longitude) {
        PlacesAgent agent = new PlacesAgent();
        agent.getGyms(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                putPlacesOnMap(response, BitmapDescriptorFactory.HUE_BLUE);
            }

            @Override
            public void onFailure(int i, Header[] headers, String response, Throwable throwable) {
                //TODO show alert error
            }
        }, latitude, longitude);
    }

    /**
     * Pobieranie obiektow sportowych i klubow w okolicy
     * @param latitude
     * @param longitude
     */
    private void showStadiumsOnMap(double latitude, double longitude) {
        PlacesAgent agent = new PlacesAgent();
        agent.getStadiums(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                putPlacesOnMap(response, BitmapDescriptorFactory.HUE_ORANGE);
            }

            @Override
            public void onFailure(int i, Header[] headers, String response, Throwable throwable) {
                //TODO show alert error
            }
        }, latitude, longitude);
    }

    /**
     * Pobieranie obiektow sportowych i klubow w okolicy
     * @param latitude
     * @param longitude
     */
    private void showClubsOnMap(double latitude, double longitude) {
        PlacesAgent agent = new PlacesAgent();
        agent.getClubs(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                putPlacesOnMap(response, BitmapDescriptorFactory.HUE_GREEN);
            }

            @Override
            public void onFailure(int i, Header[] headers, String response, Throwable throwable) {
                //TODO show alert error
            }
        }, latitude, longitude);
    }

}
