package kr.ac.uc.home;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.FileInputStream;
import java.io.FileOutputStream;

public class Diary extends AppCompatActivity {
    final String[] header = {"이지플랜트", "식물 설정", "조명 설정", "일기장"};
    final String[] menu = {"홈", "식물 설정", "조명 설정", "일기장"};
    public String readDay = null; // 선택한 날짜를 저장할 변수
    public String tdate = null; // 임시 사용(날짜)
    public String str = null; // 일기 내용을 저장할 변수

    private TextView diaryTextView; // 일기 작성 여부 표기
    private EditText contextEditText; // 일기 내용 입력

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_diary);

        final Button btnHome = findViewById(R.id.btnHome); // 홈 버튼
        final Button btnPlant = findViewById(R.id.btnPlant); // 식물 설정 버튼
        final Button btnDiary = findViewById(R.id.btnDiary); // 일기장 버튼
        final Button btnLight = findViewById(R.id.btnLight); // 조명 설정 버튼
        final Button btnMenu = findViewById(R.id.btnMenu); // 메뉴 버튼
        final TextView title = findViewById(R.id.title); // 제목
        final CalendarView calendarView = findViewById(R.id.calendarView); // 달력
        final Button btnDirWrite = findViewById(R.id.btnDirWrite); // 작성 버튼
        diaryTextView = findViewById(R.id.diaryTextView); // 일기 작성 여부 표기
//        contextEditText = findViewById(R.id.contextEditText); // 내용 표기

        title.setText(header[3]);
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
                Intent intent = new Intent(Diary.this, MainActivity.class);
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
                Intent intent = new Intent(Diary.this, Plant.class);
                startActivity(intent);
                finish();
            }
        });

        // btnLight 클릭 이벤트
        btnLight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Diary.this, Light.class);
                startActivity(intent);
                finish();
            }
        });

        // btnDiary 클릭 이벤트
        btnDiary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Diary.this, Diary.class);
                startActivity(intent);
                finish();
            }
        });

        // 캘린더 날짜 선택 리스너 설정
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                // 날짜가 선택되면 관련 뷰의 가시성을 조정하고, 날짜를 표시함
                diaryTextView.setVisibility(View.VISIBLE);

                tdate = String.format("%d / %d / %d", year, month + 1, dayOfMonth); // 클릭시 날짜 표시
                diaryTextView.setText(tdate); // 일기 날짜 텍스트뷰에 설정
                readDay = String.format("%d-%02d-%02d.txt", year, month + 1, dayOfMonth); // 파일 이름 설정

//                contextEditText.setText(""); // 에디트텍스트 초기화
                checkDay(year, month, dayOfMonth); // 선택한 날짜의 일기를 체크
            }
        });

        btnDirWrite.setOnClickListener(v -> {
            // 커스텀 다이얼로그 레이아웃을 인플레이트
            View dialogView = getLayoutInflater().inflate(R.layout.diary_dialog, null);

            // AlertDialog 생성
            AlertDialog.Builder builder = new AlertDialog.Builder(Diary.this);
            builder.setView(dialogView);

            // 다이얼로그 객체 생성
            AlertDialog dialog = builder.create();

            // 커스텀 레이아웃의 뷰들을 가져옴
            EditText multiLineEditText = dialogView.findViewById(R.id.multiLineEditText);
            TextView dialog_message = dialogView.findViewById(R.id.dialog_message);
            Button btnSaveDir = dialogView.findViewById(R.id.btnSaveDir);
            Button btnCancelDir = dialogView.findViewById(R.id.btnCancelDir);

            dialog_message.setText(tdate);

            // 확인 버튼 클릭 리스너 설정
            btnSaveDir.setOnClickListener(view -> {
                // 입력된 텍스트를 가져와서 파일에 저장
                String result = multiLineEditText.getText().toString();
                saveDiary(readDay, result);
                dialog.dismiss(); // 다이얼로그 닫기
            });

            // 취소 버튼 클릭 리스너 설정
            btnCancelDir.setOnClickListener(view -> dialog.dismiss());

            dialog.show(); // 다이얼로그 표시
        });
    }

    public void checkDay(int cYear, int cMonth, int cDay) {
        readDay = String.format("%d-%02d-%02d.txt", cYear, cMonth + 1, cDay); // 파일 이름을 설정
        FileInputStream fis;

        try {
            fis = openFileInput(readDay); // 파일 열기

            byte[] fileData = new byte[fis.available()]; // 파일 데이터를 읽음
            fis.read(fileData);
            fis.close();

            str = new String(fileData); // 파일 내용을 문자열로 변환

            contextEditText.setVisibility(View.INVISIBLE); // 에디트텍스트 숨김
            diaryTextView.setVisibility(View.VISIBLE); // 텍스트뷰 표시
            diaryTextView.setText(str); // 텍스트뷰에 일기 내용 표시

        } catch (Exception e) {
            diaryTextView.setText("아직 일기를 작성하지 않았어요.");
            e.printStackTrace(); // 예외 발생 시 스택 트레이스를 출력
        }
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

    public void removeDiary(String readDay) {
        try {
            boolean deleted = deleteFile(readDay); // 파일 삭제 시도
            diaryTextView.setText("아직 일기를 작성하지 않았어요.");
            if (!deleted) {
                System.out.println("파일을 삭제하지 못했습니다."); // 파일 삭제 실패 시 메시지 출력
            }
        } catch (Exception e) {
            e.printStackTrace(); // 예외 발생 시 스택 트레이스를 출력
        }
    }

    // 일기를 저장하는 메소드
    @SuppressLint("WrongConstant")
    public void saveDiary(String readDay, String content) {
        FileOutputStream fos;
        try {
            fos = openFileOutput(readDay, MODE_NO_LOCALIZED_COLLATORS); // 파일 열기
            fos.write((content).getBytes()); // 파일에 내용 쓰기
            fos.close();
        } catch (Exception e) {
            e.printStackTrace(); // 예외 발생 시 스택 트레이스를 출력
        }
    }
}
