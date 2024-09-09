package kr.ac.uc.home;

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
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Locale;

public class EasyDifficulty extends AppCompatActivity {

    GridLayout sudokuGridEasy;
    Button btnOkEasy, btnBackEasy;
    TextView tvStopwatchEasy, tvResultMessageEasy;
    private final EditText[][] cells = new EditText[9][9];
    private final int[][] initialBoard = {
            {5, 3, 0, 0, 7, 0, 0, 0, 0},
            {6, 0, 0, 1, 9, 5, 0, 0, 0},
            {0, 9, 8, 0, 0, 0, 0, 6, 0},
            {8, 0, 0, 0, 6, 0, 0, 0, 3},
            {4, 0, 0, 8, 0, 3, 0, 0, 1},
            {7, 0, 0, 0, 2, 0, 0, 0, 6},
            {0, 6, 0, 0, 0, 0, 2, 8, 0},
            {0, 0, 0, 4, 1, 9, 0, 0, 5},
            {0, 0, 0, 0, 8, 0, 0, 7, 9}
    };
    private final int[][] board = new int[9][9];

    private int seconds = 0;
    private boolean isRunning = true;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.easy_difficulty);

        sudokuGridEasy = findViewById(R.id.sudokuGridEasy);
        btnOkEasy = findViewById(R.id.btnOkEasy);
        btnBackEasy = findViewById(R.id.btnBackEasy);
        tvStopwatchEasy = findViewById(R.id.tvStopwatchEasy);
        tvResultMessageEasy = findViewById(R.id.tvResultMessageEasy);

        // 타이머 시작
        startTimer();

        //int black = getResources().getColor(R.color.black);
        //int white = getResources().getColor(R.color.white);

        int black = ContextCompat.getColor(this, R.color.black);
        int white = ContextCompat.getColor(this, R.color.white);

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

                sudokuGridEasy.addView(cell, new GridLayout.LayoutParams(
                        GridLayout.spec(i, 1f),
                        GridLayout.spec(j, 1f)
                ));
                cells[i][j] = cell;
            }
        }

        btnOkEasy.setOnClickListener(v -> {
            updateBoard();
            kr.ac.uc.home.SudokuSolver solver = new kr.ac.uc.home.SudokuSolver(board);
            if (solver.isValid(board)) {
                stopTimer();
                tvResultMessageEasy.setVisibility(View.VISIBLE);
                //tvResultMessageEasy.setText("정답! 시간: " + formatTime(seconds));
                tvResultMessageEasy.setText(getString(R.string.correct_message, formatTime(seconds)));
                btnOkEasy.setVisibility(View.GONE);
            } else {
                Toast.makeText(EasyDifficulty.this, "실패.", Toast.LENGTH_SHORT).show();
            }
        });

        btnBackEasy.setOnClickListener(v -> {
            Intent intent = new Intent(EasyDifficulty.this, SelectDifficulty.class);
            startActivity(intent);
            finish();
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.easy), (view, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return WindowInsetsCompat.CONSUMED;
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
                    tvStopwatchEasy.setText(formatTime(seconds));
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
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, secs);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopTimer();  // 액티비티 종료 시 타이머 중지
    }

}