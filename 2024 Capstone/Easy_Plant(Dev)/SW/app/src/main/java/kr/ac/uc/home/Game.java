package kr.ac.uc.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Game extends AppCompatActivity implements ButtonFragment.OnButtonClickListener {

    Button btnStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_game);

        btnStart = findViewById(R.id.btnStart);

        btnStart.setOnClickListener(v -> {
            Intent intent = new Intent(Game.this, SelectDifficulty.class);
            startActivity(intent);
        });

        //뷰 초기화 및 기타 코드
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.btnFragment, new ButtonFragment()) // Use fragment_container instead of main
                    .commit();
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.game), (view, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return WindowInsetsCompat.CONSUMED;
        });
    }

    @Override
    public void onButtonClicked(int buttonId) { // 인터페이스 메소드 구현
        if (buttonId == R.id.btnHome) {
            Intent intent = new Intent(Game.this, home.class);
            startActivity(intent);
            finish();
        } else if (buttonId == R.id.btnDiary) {
            Intent intent = new Intent(Game.this, Diary.class);
            startActivity(intent);
            finish();
        } else if (buttonId == R.id.btnPlant) {
            Intent intent = new Intent(Game.this, Plant.class);
            startActivity(intent);
            finish();
        }
    }
}