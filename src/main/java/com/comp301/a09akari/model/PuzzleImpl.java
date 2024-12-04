package com.comp301.a09akari.model;

public class PuzzleImpl implements Puzzle {
    private final CellType[][] grid;
    private final int[][] clueValues;
    private final int rows;
    private final int cols;

    public PuzzleImpl(int[][] board) {
        if (board == null || board.length == 0 || board[0].length == 0) {
            throw new IllegalArgumentException("Board cannot be null or empty");
        }

        this.rows = board.length;
        this.cols = board[0].length;

        this.grid = new CellType[rows][cols];
        this.clueValues = new int[rows][cols];

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                switch (board[r][c]) {
                    case 0:
                    case 1:
                    case 2:
                    case 3:
                    case 4:
                        this.grid[r][c] = CellType.CLUE;
                        this.clueValues[r][c] = board[r][c];
                        break;
                    case 5:
                        this.grid[r][c] = CellType.WALL;
                        this.clueValues[r][c] = -1;
                        break;
                    case 6:
                        this.grid[r][c] = CellType.CORRIDOR;
                        this.clueValues[r][c] = -1;
                        break;
                    default:
                        throw new IllegalArgumentException("Invalid cell value: " + board[r][c]);
                }
            }
        }
    }

    @Override
    public int getWidth() {
        return cols;
    }

    @Override
    public int getHeight() {
        return rows;
    }

    @Override
    public CellType getCellType(int r, int c) {
        if (r < 0 || r >= rows || c < 0 || c >= cols) {
            throw new IllegalArgumentException("Invalid row or column index");
        }
        return grid[r][c];
    }

    @Override
    public int getClue(int r, int c) {
        if (r < 0 || r >= rows || c < 0 || c >= cols) {
            throw new IllegalArgumentException("Invalid row or column index");
        }
        return clueValues[r][c];
    }
}
