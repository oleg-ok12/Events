package pw.ute.my_project_ute;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * MainActivity, calls the Menu
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button showMapButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        showMapButton = (Button) findViewById(R.id.showMapBtn);
        showMapButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.showMapBtn:
                Intent intent = new Intent(this, MapsActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }
}
