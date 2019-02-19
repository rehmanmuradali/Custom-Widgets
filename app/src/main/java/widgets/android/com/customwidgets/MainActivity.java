package widgets.android.com.customwidgets;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import widgets.android.com.androidwidgets.pinview.PortablePinView;

public class MainActivity extends AppCompatActivity {

    private PortablePinView portablePinView;
    private Button btnValidate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        portablePinView = findViewById(R.id.pinView);
        btnValidate = findViewById(R.id.btnValidate);

        portablePinView.addOnValidatePinListener(new PortablePinView.ValidatePinListener() {
            @Override
            public void validatePin(String pin) {
                Toast.makeText(MainActivity.this, "pin entered: " + pin, Toast.LENGTH_LONG).show();
            }
        });
        btnValidate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Current text: " + portablePinView.getText(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
