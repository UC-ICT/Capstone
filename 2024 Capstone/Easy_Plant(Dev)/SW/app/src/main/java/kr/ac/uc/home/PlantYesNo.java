
package kr.ac.uc.home;



import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class PlantYesNo extends AppCompatActivity {

    TextView tvPlantYesNo;
    Button btnYES, btnNO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_plant_yes_no);

        tvPlantYesNo = findViewById(R.id.tvPlantYesNo);
        btnYES = findViewById(R.id.btnYES);
        btnNO = findViewById(R.id.btnNO);

        //뷰 초기화 및 기타 코드
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.btnFragment, new ButtonFragment())
                    .commit();
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.plantYesNo), (view, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return WindowInsetsCompat.CONSUMED;
        });

        // Handle Intent Data
        Intent intent = getIntent();
        String plantName = intent.getStringExtra("plantName");
        String dateKey = intent.getStringExtra("dateKey");
        int intdataKey = intent.getIntExtra("intdataKey", 0);

        tvPlantYesNo.setText(plantName + "을 심을까요?");

        btnYES.setOnClickListener(v -> {
            Intent intent2 = new Intent(PlantYesNo.this, home.class);
            intent2.putExtra("plantName", plantName);
            intent2.putExtra("dateKey", dateKey);
            intent2.putExtra("intdataKey", intdataKey);

            startActivity(intent2);
        });


    }
}

