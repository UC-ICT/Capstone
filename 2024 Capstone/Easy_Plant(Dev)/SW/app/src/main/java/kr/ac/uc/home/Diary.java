package kr.ac.uc.home;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.lights.Light;
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
import java.util.Calendar;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.view.Menu;

public class Diary extends AppCompatActivity implements ButtonFragment.OnButtonClickListener {

    public String readDay = null; // 선택한 날짜를 저장할 변수
    public String tDate = null; // 다이얼 로그 창에서 날짜 표기
    public String str = null; // 일기 내용을 저장할 변수
    public Button btnDirWrite, btnDirDel;   //작성, 삭제 버튼
    private TextView diaryTextView; // 일기 작성 여부 표기

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_diary);

        final TextView title = findViewById(R.id.title); // 제목
        final CalendarView calendarView = findViewById(R.id.calendarView); // 달력

        btnDirWrite = findViewById(R.id.btnDirWrite); // 작성 버튼
        btnDirDel= findViewById(R.id.btnDirDel); // 일기 삭제 버튼
        diaryTextView = findViewById(R.id.diaryTextView); // 일기 작성 여부 표기



        //뷰 초기화 및 기타 코드
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.btnFragment, new ButtonFragment()) // Use fragment_container instead of main
                    .commit();
        }


        // 캘린더 날짜 선택 리스너 설정
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                // 날짜가 선택되면 관련 뷰의 가시성을 조정하고, 날짜를 표시함
                diaryTextView.setVisibility(View.VISIBLE);

                readDay = String.format("%d-%02d-%02d.txt", year, month + 1, dayOfMonth); // 파일 이름 설정
                diaryTextView.setText(readDay); // 일기 날짜 텍스트뷰에 설정

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
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            // 커스텀 레이아웃의 뷰들을 가져옴
            EditText multiLineEditText = dialogView.findViewById(R.id.multiLineEditText);
            TextView dialog_message = dialogView.findViewById(R.id.dialog_message);
            Button btnSaveDir = dialogView.findViewById(R.id.btnSaveDir);
            Button btnCancelDir = dialogView.findViewById(R.id.btnCancelDir);

            dialog_message.setText(tDate);

            // 확인 버튼 클릭 리스너 설정
            btnSaveDir.setOnClickListener(view -> {
                // 입력된 텍스트를 가져와서 파일에 저장
                String result = multiLineEditText.getText().toString();
                saveDiary(readDay, result);
                diaryTextView.setText(result);
                btnDirDel.setVisibility(view.VISIBLE);
                dialog.dismiss(); // 다이얼로그 닫기
            });

            // 취소 버튼 클릭 리스너 설정
            btnCancelDir.setOnClickListener(view -> dialog.dismiss());

            dialog.show(); // 다이얼로그 표시
        });
        // 현재 날짜를 사용하여 checkDay 메소드 호출
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        // 텍스트뷰 초기화
        tDate = String.format("%d-%02d-%02d", year, month + 1, dayOfMonth);
        diaryTextView.setText(readDay); // 일기 날짜 텍스트뷰에 설정
        readDay = String.format("%d-%02d-%02d.txt", year, month + 1, dayOfMonth); // 파일 이름 설정

        checkDay(year, month, dayOfMonth); // 현재 날짜의 일기를 체크

        //뷰 초기화 및 기타 코드
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.btnFragment, new ButtonFragment()) // Use fragment_container instead of main
                    .commit();
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.diary), (view, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return WindowInsetsCompat.CONSUMED;
        });
    }

    @Override
    public void onButtonClicked(int buttonId) { // 인터페이스 메소드 구현
        if (buttonId == R.id.btnHome) {
            Intent intent = new Intent(Diary.this, home.class);
            startActivity(intent);
            finish();
        } else if (buttonId == R.id.btnPlant) {
            Intent intent = new Intent(Diary.this, Plant.class);
            startActivity(intent);
            finish();
        } else if (buttonId == R.id.btnGame) {
            Intent intent = new Intent(Diary.this, Game.class);
            startActivity(intent);
            finish();
        }
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
            diaryTextView.setVisibility(View.VISIBLE); // 텍스트뷰 표시
            diaryTextView.setText(str); // 텍스트뷰에 일기 내용 표시
            btnDirDel.setVisibility(View.VISIBLE); // 삭제 버튼 숨김

            // 삭제 버튼 클릭 리스너 설정
            btnDirDel.setOnClickListener(v -> {
                btnDirWrite.setVisibility(View.VISIBLE); // 저장 버튼 표시
                btnDirDel.setVisibility(View.GONE); // 삭제 버튼 숨김
                removeDiary(readDay); // 일기 삭제
            });

        } catch (Exception e) {
            diaryTextView.setText("아직 일기를 작성하지 않았어요.");
            btnDirDel.setVisibility(View.GONE); // 삭제 버튼 숨김
            e.printStackTrace(); // 예외 발생 시 스택 트레이스를 출력
        }
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