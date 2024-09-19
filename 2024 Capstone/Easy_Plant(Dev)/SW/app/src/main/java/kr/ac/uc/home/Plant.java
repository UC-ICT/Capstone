

package kr.ac.uc.home;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Plant extends AppCompatActivity implements ButtonFragment.OnButtonClickListener {

    long readDay = System.currentTimeMillis(); // 현재시간
    Date date = new Date(readDay); // 현재시간 data지정
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); // 날짜 포맷 지정
    String getTime = sdf.format(date); // 날짜 포맷 지정

    LinearLayout sangchu_Layout ,ggatnip_Layout ,sweet_basil_Layout ,apple_mint_Layout;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_plant);



        LinearLayout linearButton1 = findViewById(R.id.sangchu_Layout);
        LinearLayout linearButton2 = findViewById(R.id.ggatnip_Layout);
        LinearLayout linearButton3 = findViewById(R.id.sweet_basil_Layout);
        LinearLayout linearButton4 = findViewById(R.id.apple_mint_Layout);

        sangchu_Layout = findViewById(R.id.sangchu_Layout);
        ggatnip_Layout = findViewById(R.id.ggatnip_Layout);
        sweet_basil_Layout = findViewById(R.id.sweet_basil_Layout);
        apple_mint_Layout = findViewById(R.id.apple_mint_Layout);

        // 뷰 초기화 및 기타 코드
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.btnFragment, new ButtonFragment()) // 프래그먼트 추가
                    .commit();
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.plant), (view, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return WindowInsetsCompat.CONSUMED;
        });


        checkplant();// 심은 식물 확인

        // 각 식물 버튼에 클릭 리스너 설정
        linearButton1.setOnClickListener(v -> {
            Log.d("plant", "Sangchu 버튼 클릭");
            showDialogWithIntent("상추", 1);
        });

        linearButton2.setOnClickListener(v -> {
            Log.d("plant", "Ggatnip 버튼 클릭");
            showDialogWithIntent("깻잎", 2);
        });

        linearButton3.setOnClickListener(v -> {
            Log.d("plant", "Sweet Basil 버튼 클릭");
            showDialogWithIntent("스위트 바질", 3);
        });

        linearButton4.setOnClickListener(v -> {
            Log.d("plant", "Apple Mint 버튼 클릭");
            showDialogWithIntent("애플 민트", 4);
        });
    }

    private void showDialogWithIntent(String plantName, int dataKey) {
        Intent intent = new Intent(Plant.this, PlantYesNo.class);
        intent.putExtra("plantName", plantName);
        intent.putExtra("dateKey", getTime);
        intent.putExtra("intdataKey", dataKey);
        dialogYesNo(intent);
    }

    public void dialogYesNo(Intent intent) {
        // 커스텀 다이얼로그 레이아웃을 인플레이트
        View dialogView = getLayoutInflater().inflate(R.layout.activity_plant_yes_no, null);

        // AlertDialog 생성
        AlertDialog.Builder builder = new AlertDialog.Builder(Plant.this);
        builder.setView(dialogView);

        // 다이얼로그 객체 생성
        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // 커스텀 레이아웃의 뷰들을 가져옴
        TextView tvPlantYesNo = dialogView.findViewById(R.id.tvPlantYesNo);
        Button btnYES = dialogView.findViewById(R.id.btnYES);
        Button btnNO = dialogView.findViewById(R.id.btnNO);

        // Intent로부터 데이터 가져오기
        String plantName = intent.getStringExtra("plantName");
        String dateKey = intent.getStringExtra("dateKey");
        int intdataKey = intent.getIntExtra("intdataKey", 0);

        // 다이얼로그에 텍스트 설정
        tvPlantYesNo.setText(plantName + "을 심을까요?");

        // YES 버튼 클릭 리스너 설정
        btnYES.setOnClickListener(view -> {
            Log.d("plant", "YES 버튼 클릭");
            Intent intent2 = new Intent(this, home.class);
            intent2.putExtra("plantName", plantName);
            intent2.putExtra("dateKey", dateKey);
            intent2.putExtra("intdataKey", intdataKey);


            //식물DATA 저장
            SharedPreferences sharedPreferences = getSharedPreferences("PlantData", MODE_PRIVATE);// 데이터 저장 ,앱내에서만 접근 가능
            SharedPreferences.Editor editor = sharedPreferences.edit();


            editor.putString("plantName", plantName);
            editor.putString("dateKey", dateKey);
            editor.putInt("intdataKey", intdataKey);
            editor.putInt("intdata", 1);


            editor.apply();

            startActivity(intent2);

            dialog.dismiss(); // 다이얼로그 닫기
        });

        // NO 버튼 클릭 리스너 설정
        btnNO.setOnClickListener(view -> {
            Log.d("plant", "NO 버튼 클릭");
            dialog.dismiss(); // 다이얼로그 닫기
        });

        dialog.show(); // 다이얼로그 표시
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

    public void checkplant() {
        SharedPreferences sharedPreferences = getSharedPreferences("PlantData", MODE_PRIVATE);
        int intdataKey;
        intdataKey = sharedPreferences.getInt("intdataKey", 0);

        Log.d("SharedPreferences", "intdataKey: " + intdataKey);



        if (intdataKey == 0) {
            sangchu_Layout.setBackgroundResource(R.drawable.rounded_border);
            ggatnip_Layout.setBackgroundResource(R.drawable.rounded_border);
            sweet_basil_Layout.setBackgroundResource(R.drawable.rounded_border);
            apple_mint_Layout.setBackgroundResource(R.drawable.rounded_border);
        }

        if (intdataKey == 1) {
            sangchu_Layout.setBackgroundResource(R.drawable.rounded_border_plant_check);
            ggatnip_Layout.setBackgroundResource(R.drawable.rounded_border);
            sweet_basil_Layout.setBackgroundResource(R.drawable.rounded_border);
            apple_mint_Layout.setBackgroundResource(R.drawable.rounded_border);
        }
        if (intdataKey == 2) {
            sangchu_Layout.setBackgroundResource(R.drawable.rounded_border);
            ggatnip_Layout.setBackgroundResource(R.drawable.rounded_border_plant_check);
            sweet_basil_Layout.setBackgroundResource(R.drawable.rounded_border);
            apple_mint_Layout.setBackgroundResource(R.drawable.rounded_border);
        }
        if (intdataKey == 3) {
            sangchu_Layout.setBackgroundResource(R.drawable.rounded_border);
            ggatnip_Layout.setBackgroundResource(R.drawable.rounded_border);
            sweet_basil_Layout.setBackgroundResource(R.drawable.rounded_border_plant_check);
            apple_mint_Layout.setBackgroundResource(R.drawable.rounded_border);
        }
        if (intdataKey == 4) {
            sangchu_Layout.setBackgroundResource(R.drawable.rounded_border);
            ggatnip_Layout.setBackgroundResource(R.drawable.rounded_border);
            sweet_basil_Layout.setBackgroundResource(R.drawable.rounded_border);
            apple_mint_Layout.setBackgroundResource(R.drawable.rounded_border_plant_check);
        }


    }




}
