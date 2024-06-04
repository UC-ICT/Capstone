package kr.ac.uc.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {
    final static String[] header = {"이지플랜트", "식물 설정", "조명 설정", "일기장"};
    final static String[] menu = {"홈", "식물 설정", "조명 설정", "일기장"};


    final int[] arrwaterlevel = {
                    R.drawable.waterlvel1, R.drawable.waterlvel2,
                    R.drawable.waterlvel3, R.drawable.waterlvel4
            };

            ImageView ivPlant, ivwaterLevel;
            TextView tvPlantedPlantName, tvPlantedDay, tvgrownDay, tvCondition, tvwaterLevelLabel;
            EditText etWaterLevelInput;
            Button btnWaterLeveltest;

            @Override
            protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
                setContentView(R.layout.activity_main);

                final TextView title = findViewById(R.id.title); // 제목
                final Button btnMenu = findViewById(R.id.btnMenu); // 동그란 버튼
                final Button btnHome = findViewById(R.id.btnHome); // 홈 버튼
                final Button btnPlant = findViewById(R.id.btnPlant); // 식물 설정 버튼
                final Button btnDiary = findViewById(R.id.btnDiary); // 일기장 버튼
                final Button btnLight = findViewById(R.id.btnLight); // 조명 설정 버튼

                tvwaterLevelLabel = findViewById(R.id.tvwaterLevelLabel); // 물수위 텍스트
                ivwaterLevel = findViewById(R.id.ivwaterLevel); // 물 수위 이미지
                etWaterLevelInput = findViewById(R.id.etWaterLevelInput); // 물 수위 텍스트 입력
                btnWaterLeveltest = findViewById(R.id.btnWaterLeveltest); // 물수위 테스트 버튼

                title.setText(header[0]);
                btnHome.setText(menu[0]);
                btnPlant.setText(menu[1]);
                btnLight.setText(menu[2]);
                btnDiary.setText(menu[3]);

                ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
                    Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                    v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                    return insets;
                });

                // 초기 상태 설정: btnMenu를 제외한 모든 버튼 숨기기
                btnHome.setVisibility(View.GONE);
                btnPlant.setVisibility(View.GONE);
                btnLight.setVisibility(View.GONE);
                btnDiary.setVisibility(View.GONE);

                //물 수위 센서 테스트 버튼
                btnWaterLeveltest.setOnClickListener(v -> waterlevelChange() );

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
                        Intent intent = new Intent(MainActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
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
                        Intent intent = new Intent(MainActivity.this, Plant.class);
                        startActivity(intent);
                        finish();
                    }
                });

                // btnLight 클릭 이벤트
                btnLight.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, Light.class);
                        startActivity(intent);
                        finish();
                    }
                });

                // btnDiary 클릭 이벤트
                btnDiary.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, Diary.class);
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

            private void waterlevelChange() {// 물수위 센서변화
                String waterLevelSensor = etWaterLevelInput.getText().toString();
                int waterLevel = 0;

                try {
                    // 문자열을 int로 변환
                    waterLevel = Integer.parseInt(waterLevelSensor);
                } catch (NumberFormatException e) {
                    // 입력된 문자열이 숫자가 아닐 경우 예외 처리
                    e.printStackTrace();
                    Toast.makeText(this, "유효한 숫자를 입력하세요.", Toast.LENGTH_SHORT).show();
                }

                if (waterLevel == 0) {
                    tvwaterLevelLabel.setText("물을 채워주세요");
                    ivwaterLevel.setImageResource(arrwaterlevel[3]);
                } else if (waterLevel >= 30 && waterLevel <=60) {
                    tvwaterLevelLabel.setText("부족해요");
                    ivwaterLevel.setImageResource(arrwaterlevel[2]);
                } else if (waterLevel > 60 && waterLevel <100) {
                    tvwaterLevelLabel.setText("충분해요");
                    ivwaterLevel.setImageResource(arrwaterlevel[1]);
                } else if (waterLevel >= 100) {
                    tvwaterLevelLabel.setText("다 채웠어요");
                    ivwaterLevel.setImageResource(arrwaterlevel[0]);
                }

            }
        }







// menu 버튼을 누르면 기능 버튼들이 나오고
// 사용자가 버튼 외의 화면을 눌렀을 때 or 기능 버튼 중 하나를 눌렀을 때
// 버튼 초기화
// 이 기능들 구현후 Intent 기능 구현
