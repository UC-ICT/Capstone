package kr.ac.uc.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;

public class home extends AppCompatActivity {
    final static String[] header = {"이지플랜트", "식물 설정", "조명 설정", "일기장"};
    final static String[] menu = {"홈", "식물 설정", "조명 설정", "일기장"};

    long readDay = System.currentTimeMillis();// 현재시간
    Date date = new Date(readDay);// 현재시간 data지정
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");// 날짜 포맷 지정
    String getTime = sdf.format(date);// 날짜 포맷 지정

    int waterLevel = 0; // 물수위 변수

    final int[] arrwaterlevel = {R.drawable.waterlvel1, R.drawable.waterlvel2, R.drawable.waterlvel3, R.drawable.waterlvel4}; // 수정 완료!

    ImageView ivPlant, ivwaterLevel;
    TextView tvPlantedPlantName, tvPlantedDay, tvgrowDay, tvCondition, tvwaterLevelLabel, tvdiary;
    EditText etWaterLevelInput;
    Button btnWaterLeveltest, btnMenu, btnHome, btnPlant, btnDiary, btnLight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        // View 초기화
        final TextView title = findViewById(R.id.title); // 제목
        btnMenu = findViewById(R.id.btnMenu); // 동그란 버튼
        btnHome = findViewById(R.id.btnHome); // 홈 버튼
        btnPlant = findViewById(R.id.btnPlant); // 식물 설정 버튼
        btnDiary = findViewById(R.id.btnDiary); // 일기장 버튼
        btnLight = findViewById(R.id.btnLight); // 조명 설정 버튼

        tvwaterLevelLabel = findViewById(R.id.tvwaterLevelLabel); // 물수위 텍스트
        ivwaterLevel = findViewById(R.id.ivwaterLevel); // 물 수위 이미지
        etWaterLevelInput = findViewById(R.id.etWaterLevelInput); // 물 수위 텍스트 입력
        btnWaterLeveltest = findViewById(R.id.btnWaterLeveltest); // 물수위 테스트 버튼

        ivPlant = findViewById(R.id.ivPlant); // 심은 식물 이미지
        tvPlantedPlantName = findViewById(R.id.tvPlantedPlantName); // 심은 식물 이름
        tvPlantedDay = findViewById(R.id.tvPlantedDay); // 심은 날짜
        tvgrowDay = findViewById(R.id.tvgrowDay); // 키운 날짜
        tvCondition = findViewById(R.id.tvCondition); // 식물 상태
        tvdiary = findViewById(R.id.tvdiary); // 일기장 작성 여부

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
        btnWaterLeveltest.setOnClickListener(v -> waterlevelChange()); // 수정 완료!

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
                Intent intent = new Intent(home.this, home.class);
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
                Intent intent = new Intent(home.this, Plant.class);
                startActivity(intent);
                finish();
            }
        });

        // btnLight 클릭 이벤트
        btnLight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(home.this, Light.class);
                startActivity(intent);
                finish();
            }
        });

        // btnDiary 클릭 이벤트
        btnDiary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(home.this, Diary.class);
                startActivity(intent);
                finish();
            }
        });

        checkTodayDiary();
        growDay();
    }

    // 버튼 클릭 애니메이션(In)
    private void fadeInAnimation(View view) {
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        view.setVisibility(View.VISIBLE);
        view.startAnimation(fadeIn);
    }

    // 버튼 클릭 애니메이션(Out)
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
            return; // 수정 완료!
        }

        if (waterLevel == 0) {
            tvwaterLevelLabel.setText("물을 채워주세요");
            ivwaterLevel.setImageResource(arrwaterlevel[3]);
        } else if (waterLevel >= 30 && waterLevel <= 60) {
            tvwaterLevelLabel.setText("부족해요");
            ivwaterLevel.setImageResource(arrwaterlevel[2]);
        } else if (waterLevel > 60 && waterLevel < 100) {
            tvwaterLevelLabel.setText("충분해요");
            ivwaterLevel.setImageResource(arrwaterlevel[1]);
        } else if (waterLevel >= 100) {
            tvwaterLevelLabel.setText("다 채웠어요");
            ivwaterLevel.setImageResource(arrwaterlevel[0]);
        }
    }

    public void checkTodayDiary() {// 오늘 일기 확인
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1; // 0부터 시작하므로 1 더함
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);//날짜

        String todayFileName = String.format("%d-%02d-%02d.txt", year, month, dayOfMonth); // 오늘 날짜 파일 이름 설정
        File file = new File(getFilesDir(), todayFileName); // 파일 객체 생성

        if (file.exists()) { // 파일이 존재하는지 확인
            tvdiary.setVisibility(View.VISIBLE); // 텍스트뷰 표시
            tvdiary.setText("오늘 일기가 있습니다."); // 일기가 있음을 표시
        } else {
            tvdiary.setVisibility(View.VISIBLE); // 텍스트뷰 표시
            tvdiary.setText("오늘 일기가 없습니다."); // 일기가 없음을 표시
        }
    }

    public void growDay() {
        LocalDate startDate = LocalDate.of(2024, 7, 1);//임시로 식물 심은날짜 지정
        LocalDate endDate = LocalDate.now();//현재 날짜

        tvPlantedDay.setText("심은 날짜 : " + startDate);//심은 날짜

        long growDay = ChronoUnit.DAYS.between(startDate, endDate);//일수 차이 계산

        tvgrowDay.setText("키운 날짜 : " + growDay + "일"); //키운 날짜 표시
    }
}