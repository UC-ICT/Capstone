package kr.ac.uc.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
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

public class Plant extends AppCompatActivity implements ButtonFragment.OnButtonClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_plant);

        final TextView title = findViewById(R.id.title); // 제목
        final TextView selectPlant = findViewById(R.id.selectPlant); // 식물 설정 문장

        //뷰 초기화 및 기타 코드
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.btnFragment, new ButtonFragment()) // Use fragment_container instead of main
                    .commit();
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.plant), (view, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return WindowInsetsCompat.CONSUMED;
        });
    }

    @Override
    public void onButtonClicked(int buttonId) { // 인터페이스 메소드 구현
        if (buttonId == R.id.btnHome) {
            Intent intent = new Intent(Plant.this, home.class);
            startActivity(intent);
            finish();
        } else if (buttonId == R.id.btnDiary) {
            Intent intent = new Intent(Plant.this, Diary.class);
            startActivity(intent);
            finish();
        } else if (buttonId == R.id.btnGame) {
            Intent intent = new Intent(Plant.this, Game.class);
            startActivity(intent);
            finish();
        }
    }
}