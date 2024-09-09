package kr.ac.uc.home;

public class SudokuSolver {
    private int[][] board;

    public SudokuSolver(int[][] board) {
        this.board = board;
    }

    public boolean isValid(int[][] userBoard) {
        int sum = 0;
        // 행 검사
        for(int i = 0; i < 9; i++) {
            for(int j = 0; j < 9; j++) {
                sum += board[i][j];
            }
            if(sum != 45) return false;
            sum = 0;
        }
        // 열 검사
        for(int i = 0; i < 9; i++) {
            for(int j = 0; j < 9; j++) {
                sum += board[j][i];
            }
            if(sum != 45) return false;
            sum = 0;
        }

        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                sum += board[i][j];
        if (sum != 45) return false;
        sum = 0;

        for (int i = 0; i < 3; i++)
            for (int j = 3; j < 6; j++)
                sum += board[i][j];

        if (sum != 45) return false;
        sum = 0;

        for (int i = 0; i < 3; i++)
            for (int j = 6; j < 9; j++)
                sum += board[i][j];

        if (sum != 45) return false;
        sum = 0;

        ////////////////
        for (int i = 3; i < 6; i++)
            for (int j = 0; j < 3; j++)
                sum += board[i][j];
        if (sum != 45) return false;
        sum = 0;

        for (int i = 3; i < 6; i++)
            for (int j = 3; j < 6; j++)
                sum += board[i][j];

        if (sum != 45) return false;
        sum = 0;

        for (int i = 3; i < 6; i++)
            for (int j = 6; j < 9; j++)
                sum += board[i][j];

        if (sum != 45) return false;
        sum = 0;

        /////////////////////////
        for (int i = 6; i < 9; i++)
            for (int j = 0; j < 3; j++)
                sum += board[i][j];
        if (sum != 45) return false;
        sum = 0;

        for (int i = 6; i < 9; i++)
            for (int j = 3; j < 6; j++)
                sum += board[i][j];

        if (sum != 45) return false;
        sum = 0;

        for (int i = 6; i < 9; i++)
            for (int j = 6; j < 9; j++)
                sum += board[i][j];

        if (sum != 45) return false;
        sum = 0;

        return true;
    }
}

