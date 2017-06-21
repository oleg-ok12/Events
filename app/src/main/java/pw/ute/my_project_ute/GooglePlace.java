package pw.ute.my_project_ute;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Oleg on 2017-06-20.
 */

public class GooglePlace{
    public Double latitude;
    public Double longitude;
    public LatLng latLng;
    public String placeName;
    public String address;
    public String website;

    public GooglePlace(JSONObject placeObject) throws JSONException {
        JSONObject location = placeObject.getJSONObject("geometry").getJSONObject("location");
        latitude = Double.valueOf(location.getString("lat"));
        longitude = Double.valueOf(location.getString("lng"));
        latLng = new LatLng(latitude, longitude);
        placeName = placeObject.getString("name");
        address = placeObject.getString("vicinity");
    }
}
