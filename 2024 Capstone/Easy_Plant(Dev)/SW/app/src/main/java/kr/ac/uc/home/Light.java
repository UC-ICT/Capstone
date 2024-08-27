package kr.ac.uc.home;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Light extends AppCompatActivity {
    private final String TAG = Light.class.getSimpleName();
    final static String[] header = {"이지플랜트", "식물 설정", "조명 설정", "일기장"};
    final static String[] menu = {"홈", "식물 설정", "조명 설정", "일기장"};

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_SEND)){
                String waterLevel = intent.getStringExtra("WaterLevel");
                String temperature = intent.getStringExtra("Temperature");
                String humidity = intent.getStringExtra("Humidity");

                // 여기서 Intent에 데이터가 잘 들어갔는지 확인
                Log.d(TAG, "WaterLevel: " + waterLevel);
                Log.d(TAG, "Temperature: " + temperature);
                Log.d(TAG, "Humidity: " + humidity);
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_light);

        IntentFilter filter = new IntentFilter(Intent.ACTION_SEND);
        registerReceiver(receiver, filter);


        final Button btnHome = findViewById(R.id.btnHome); // 홈 버튼
        final Button btnPlant = findViewById(R.id.btnPlant); // 식물 설정 버튼
        final Button btnDiary = findViewById(R.id.btnDiary); // 일기장 버튼
        final Button btnLight = findViewById(R.id.btnLight); // 조명 설정 버튼
        final Button btnMenu = findViewById(R.id.btnMenu); // 메뉴 버튼
        final TextView title = findViewById(R.id.title); // 제목



        title.setText(header[2]);
        btnHome.setText(menu[0]);
        btnPlant.setText(menu[1]);
        btnLight.setText(menu[2]);
        btnDiary.setText(menu[3]);


        // 초기 상태 설정: btnMenu를 제외한 모든 버튼 숨기기
        btnHome.setVisibility(View.GONE);
        btnPlant.setVisibility(View.GONE);
        btnLight.setVisibility(View.GONE);
        btnDiary.setVisibility(View.GONE);


        // btnMenu 클릭 이벤트
        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnMenu.setVisibility(View.GONE); // btnMenu 숨기기
                fadeInAnimation(btnHome);
                fadeInAnimation(btnPlant);
                fadeInAnimation(btnLight);
                fadeInAnimation(btnDiary);
            }
        });

        // btnHome 클릭 이벤트
        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Light.this, home.class);
                startActivity(intent);
                finish();
                // 다른 버튼들 숨기기
                fadeOutAnimation(btnHome);
                fadeOutAnimation(btnPlant);
                fadeOutAnimation(btnLight);
                fadeOutAnimation(btnDiary);
                btnMenu.setVisibility(View.VISIBLE); // btnMenu 보이기
            }
        });

        // btnPlant 클릭 이벤트
        btnPlant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Light.this, Plant.class);
                startActivity(intent);
                finish();
            }
        });

        // btnLight 클릭 이벤트
        btnLight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Light.this, Light.class);
                startActivity(intent);
                finish();
            }
        });

        // btnDiary 클릭 이벤트
        btnDiary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Light.this, Diary.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void fadeInAnimation(View view) {
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        view.setVisibility(View.VISIBLE);
        view.startAnimation(fadeIn);
    }

    private void fadeOutAnimation(View view) {
        Animation fadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out);
        view.startAnimation(fadeOut);
        view.setVisibility(View.GONE);
    }
}