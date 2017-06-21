package pw.ute.my_project_ute;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;


/**
 * Created by Oleg on 2017-06-20.
 */

public class PlacesAgent {
    private AsyncHttpClient agent;

    public PlacesAgent() {
        this.agent = new AsyncHttpClient();
    }

    public void getClubs (JsonHttpResponseHandler handler, double latitude,double longitude){
        String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="+latitude+","+longitude+"&radius=2000&type=night_club&key=AIzaSyBeZxPitp7UPkyDzYPS1rpLSMvObNcmA-Q";
        retrieveHttp( url, handler);
    }

    public void getGyms (JsonHttpResponseHandler handler, double latitude,double longitude){
        String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="+latitude+","+longitude+"&radius=2000&type=gym&key=AIzaSyBeZxPitp7UPkyDzYPS1rpLSMvObNcmA-Q";
        retrieveHttp( url,  handler);
    }

    public void getStadiums (JsonHttpResponseHandler handler, double latitude,double longitude){
        String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="+latitude+","+longitude+"&radius=2000&type=stadium&key=AIzaSyBeZxPitp7UPkyDzYPS1rpLSMvObNcmA-Q";
        retrieveHttp( url,  handler);
    }

    private void retrieveHttp(String url, JsonHttpResponseHandler handler){
        agent = new AsyncHttpClient();
        agent.get(url, handler);
    }

}
