package widgets.android.com.customwidgets;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import widgets.android.com.androidwidgets.pinview.PortablePinView;

public class MainActivity extends AppCompatActivity {

    private PortablePinView portablePinView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        portablePinView = findViewById(R.id.pinView);
        portablePinView.addOnValidatePinListener(new PortablePinView.ValidatePinListener() {
            @Override
            public void validatePin(String pin) {
                Toast.makeText(MainActivity.this, "pin entered: " + pin, Toast.LENGTH_LONG).show();
            }
        });
    }
}
