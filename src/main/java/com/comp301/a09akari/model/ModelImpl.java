package com.comp301.a09akari.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ModelImpl implements Model {
    private final PuzzleLibrary library;
    private int currentPuzzleIndex;
    private final Set<int[]> lamps;
    private final List<ModelObserver> observers;

    public ModelImpl(PuzzleLibrary library) {
        if (library == null || library.size() == 0) {
            throw new IllegalArgumentException("Library cannot be null or empty");
        }
        this.library = library;
        this.currentPuzzleIndex = 0;
        this.lamps = new HashSet<>();
        this.observers = new ArrayList<>();
    }

    @Override
    public void addLamp(int r, int c) {
        Puzzle active = getActivePuzzle();
        if (r < 0 || r >= active.getHeight() || c < 0 || c >= active.getWidth()) {
            throw new IllegalArgumentException("Out of bounds");
        }

        if (active.getCellType(r, c) != CellType.CORRIDOR) {
            throw new IllegalArgumentException("Must be a corridor");
        }

        lamps.add(new int[]{r, c});
        notifyObservers();
    }

    @Override
    public void removeLamp(int r, int c) {
        Puzzle active = getActivePuzzle();
        if (r < 0 || r >= active.getHeight() || c < 0 || c >= active.getWidth()) {
            throw new IllegalArgumentException("Out of bounds");
        }

        if (active.getCellType(r, c) != CellType.CORRIDOR) {
            throw new IllegalArgumentException("Must be a corridor");
        }

        lamps.removeIf(lamp -> lamp[0] == r && lamp[1] == c);
        notifyObservers();
    }

    @Override
    public boolean isLit(int r, int c) {
        Puzzle active = getActivePuzzle();
        if (active.getCellType(r, c) == CellType.WALL || active.getCellType(r, c) == CellType.CLUE) {
            return false;
        }

        if (isLamp(r, c)) {
            return true;
        }

        for (int col = 0; col < active.getWidth(); col++) {
            if (col != c && isLamp(r, col)) {
                if (clearPath(r, c, r, col)) {
                    return true;
                }
            }
        }

        for (int row = 0; row < active.getHeight(); row++) {
            if (row != r && isLamp(row, c)) {
                if (clearPath(r, c, row, c)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean clearPath(int r1, int c1, int r2, int c2) {
        Puzzle active = getActivePuzzle();

        if (r1 == r2) {
            int start = Math.min(c1, c2);
            int end = Math.max(c1, c2);
            for (int c = start + 1; c < end; c++) {
                if (active.getCellType(r1, c) != CellType.CORRIDOR) {
                    return false;
                }
            }
            return true;
        }
        if (c1 == c2) {
            int start = Math.min(r1, r2);
            int end = Math.max(r1, r2);
            for (int r = start + 1; r < end; r++) {
                if (active.getCellType(r, c1) != CellType.CORRIDOR) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean isLamp(int r, int c) {
        Puzzle active = getActivePuzzle();
        if (r < 0 || r >= active.getHeight() || c < 0 || c >= active.getWidth()) {
            throw new IllegalArgumentException("Out of bounds");
        }

        if (active.getCellType(r, c) != CellType.CORRIDOR) {
            throw new IllegalArgumentException("Must be a corridor");
        }
        return lamps.stream().anyMatch(lamp -> lamp[0] == r && lamp[1] == c);
    }

    @Override
    public boolean isLampIllegal(int r, int c) {
        Puzzle active = getActivePuzzle();
        if (r < 0 || r >= active.getHeight() || c < 0 || c >= active.getWidth()) {
            throw new IllegalArgumentException("Out of bounds");
        }
        if (!isLamp(r, c)) {
            throw new IllegalArgumentException("Cell must contain a lamp");
        }

        for (int col = 0; col < active.getWidth(); col++) {
            if (col != c && isLamp(r, col)) {
                return true;
            }
        }
        for (int row = 0; row < active.getHeight(); row++) {
            if (row != r && isLamp(row, c)) {
                return true;
            }
        }
        return false;
    }


    @Override
    public Puzzle getActivePuzzle() {
        return library.getPuzzle(currentPuzzleIndex);
    }

    @Override
    public int getActivePuzzleIndex() {
        return currentPuzzleIndex;
    }

    @Override
    public void setActivePuzzleIndex(int index) {
        if (index < 0 || index >= library.size()) {
            throw new IndexOutOfBoundsException("Invalid index");
        }
        currentPuzzleIndex = index;
        resetPuzzle();
        notifyObservers();
    }

    @Override
    public int getPuzzleLibrarySize() {
        return library.size();
    }

    @Override
    public void resetPuzzle() {
        lamps.clear();
        notifyObservers();
    }

    @Override
    public boolean isSolved() {
        Puzzle active = getActivePuzzle();

        for (int r = 0; r < active.getHeight(); r++) {
            for (int c = 0; c < active.getWidth(); c++) {
                if (active.getCellType(r, c) == CellType.CORRIDOR && !isLit(r, c)) {
                    return false;
                }
            }
        }
        for (int r = 0; r < active.getHeight(); r++) {
            for (int c = 0; c < active.getWidth(); c++) {
                if (active.getCellType(r, c) == CellType.CLUE && !isClueSatisfied(r, c)) {
                    return false;
                }
            }
        }
        for (int[] lamp : lamps) {
            if (isLampIllegal(lamp[0], lamp[1])) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isClueSatisfied(int r, int c) {
        Puzzle active = getActivePuzzle();
        if (r < 0 || r >= active.getHeight() || c < 0 || c >= active.getWidth()) {
            throw new IllegalArgumentException("Out of bounds");
        }
        if (active.getCellType(r, c) != CellType.CLUE) {
            throw new IllegalArgumentException("Must be a clue cell");
        }
        int expectedLamps = active.getClue(r, c);
        int adjacentLamps = 0;

        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

        for(int[] dir : directions) {
            int newR = r + dir[0];
            int newC = c + dir[1];

            if (newR > 0 && newR < active.getHeight() && newC >= 0 && newC < active.getWidth() && isLamp(newR, newC)) {
                adjacentLamps++;
            }
        }
        return adjacentLamps == expectedLamps;
    }

    @Override
    public void addObserver(ModelObserver observer) {
        if (observers != null && !observers.contains(observer)) {
            observers.add(observer);
        }
    }

    @Override
    public void removeObserver(ModelObserver observer) {
        observers.remove(observer);
    }

    private void notifyObservers(){
        for (ModelObserver observer : observers) {
            observer.update(this);
        }
    }
}