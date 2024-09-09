package kr.ac.uc.sudoku_;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class NormalScreen extends AppCompatActivity {

    GridLayout sudokuGridNormal;
    Button btnOkNormal, btnbackNormal;

    TextView tvStopwatchNormal, tvResultMessageNormal;
    private EditText[][] cells = new EditText[9][9];

    private int[][] initialBoard = {
            {0, 0, 0, 0, 6, 0, 0, 0, 0},
            {0, 0, 1, 7, 0, 0, 0, 0, 9},
            {5, 0, 0, 0, 0, 2, 0, 1, 0},

            {0, 8, 0, 0, 0, 0, 0, 0, 4},
            {0, 4, 3, 8, 0, 0, 0, 7, 0},
            {0, 0, 7, 0, 0, 0, 3, 0, 0},

            {0, 6, 0, 5, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 8, 0, 7},
            {3, 0, 0, 0, 9, 0, 4, 0, 0}
    };
    private int[][] board = new int[9][9];

    private int seconds = 0;
    private boolean isRunning = true;
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable runnable;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.normal_screen);

        sudokuGridNormal = findViewById(R.id.sudokuGridNormal);
        btnOkNormal = findViewById(R.id.btnOkNormal);
        btnbackNormal = findViewById(R.id.btnbackNormal);
        tvStopwatchNormal = findViewById(R.id.tvStopwatchNormal);
        tvResultMessageNormal = findViewById(R.id.tvResultMessageNormal);

        // 타이머 시작
        startTimer();

        int black = getResources().getColor(R.color.black);
        int white = getResources().getColor(R.color.white);

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                board[i][j] = initialBoard[i][j];
                EditText cell = new EditText(this);
                cell.setText(board[i][j] == 0 ? "" : String.valueOf(board[i][j]));
                cell.setGravity(android.view.Gravity.CENTER);
                cell.setBackgroundResource(android.R.drawable.edit_text);

                if (((i / 3) % 2 == (j / 3) % 2)) {
                    cell.setBackgroundColor(white);
                    cell.setTextColor(black);
                } else {
                    cell.setBackgroundColor(black);
                    cell.setTextColor(white);
                }

                sudokuGridNormal.addView(cell, new GridLayout.LayoutParams(
                        GridLayout.spec(i, 1f),
                        GridLayout.spec(j, 1f)
                ));
                cells[i][j] = cell;
            }
        }

        btnOkNormal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateBoard();
                kr.ac.uc.sudoku_.SudokuSolver solver = new kr.ac.uc.sudoku_.SudokuSolver(board);

                if (solver.isValid(board)) {
                    //Toast.makeText(NormalScreen.this, "정답!", Toast.LENGTH_SHORT).show();
                    stopTimer();
                    tvResultMessageNormal.setVisibility(View.VISIBLE);
                    tvResultMessageNormal.setText("정답! 시간: " + formatTime(seconds));
                    btnOkNormal.setVisibility(View.GONE);  // 정답 확인 버튼 숨기기
                }
                else {
                    Toast.makeText(NormalScreen.this, "실패.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnbackNormal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NormalScreen.this, SelectScreen.class);
                startActivity(intent);
                finish();
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void updateBoard() {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                String cellText = cells[i][j].getText().toString();
                board[i][j] = cellText.isEmpty() ? 0 : Integer.parseInt(cellText);
            }
        }
    }

    private void startTimer() {
        runnable = new Runnable() {
            @Override
            public void run() {
                if (isRunning) {
                    seconds++;
                    tvStopwatchNormal.setText(formatTime(seconds));
                    handler.postDelayed(this, 1000);
                }
            }
        };
        handler.post(runnable);
    }

    private void stopTimer() {
        isRunning = false;
        handler.removeCallbacks(runnable);
    }

    private String formatTime(int seconds) {
        int minutes = seconds / 60;
        int secs = seconds % 60;
        return String.format("%02d:%02d", minutes, secs);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopTimer();  // 액티비티 종료 시 타이머 중지
    }

}