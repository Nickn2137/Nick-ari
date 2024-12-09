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
      throw new IndexOutOfBoundsException("Out of bounds");
    }

    if (active.getCellType(r, c) != CellType.CORRIDOR) {
      throw new IllegalArgumentException("Must be a corridor");
    }

    if (isLamp(r, c)) {
      return;
    }

    lamps.add(new int[] {r, c});
    notifyObservers();
  }

  @Override
  public void removeLamp(int r, int c) {
    Puzzle active = getActivePuzzle();
    if (r < 0 || r >= active.getHeight() || c < 0 || c >= active.getWidth()) {
      throw new IndexOutOfBoundsException("Out of bounds");
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
    if (r < 0 || r >= active.getHeight() || c < 0 || c >= active.getWidth()) {
      throw new IndexOutOfBoundsException("Out of bounds");
    }

    if (active.getCellType(r, c) != CellType.CORRIDOR) {
      throw new IllegalArgumentException("Must be a corridor");
    }

    if (active.getCellType(r, c) == CellType.WALL || active.getCellType(r, c) == CellType.CLUE) {
      return false;
    }

    if (isLamp(r, c)) {
      return true;
    }

    return litPath(r, c, 0, 1)
        || litPath(r, c, 0, -1)
        || litPath(r, c, 1, 0)
        || litPath(r, c, -1, 0);
  }

  private boolean litPath(int r1, int c1, int r2, int c2) {
    Puzzle active = getActivePuzzle();
    int newR = r1 + r2;
    int newC = c1 + c2;

    while (newR >= 0 && newR < active.getHeight() && newC >= 0 && newC < active.getWidth()) {
      CellType cellType = active.getCellType(newR, newC);
      if (cellType == CellType.WALL || cellType == CellType.CLUE) {
        break;
      }
      if (isLamp(newR, newC)) {
        return true;
      }
      newR += r2;
      newC += c2;
    }
    return false;
  }

  @Override
  public boolean isLamp(int r, int c) {
    Puzzle active = getActivePuzzle();
    if (r < 0 || r >= active.getHeight() || c < 0 || c >= active.getWidth()) {
      throw new IndexOutOfBoundsException("Out of bounds");
    }

    if (active.getCellType(r, c) != CellType.CORRIDOR) {
      System.out.println("Invalid cell for isLamp: (" + r + ", " + c + ") - Type: " + active.getCellType(r, c));
      throw new IllegalArgumentException("Must be a corridor");
    }
    return lamps.stream().anyMatch(lamp -> lamp[0] == r && lamp[1] == c);
  }

  @Override
  public boolean isLampIllegal(int r, int c) {
    Puzzle active = getActivePuzzle();
    if (r < 0 || r >= active.getHeight() || c < 0 || c >= active.getWidth()) {
      throw new IndexOutOfBoundsException("Out of bounds");
    }
    if (!isLamp(r, c)) {
      throw new IllegalArgumentException("Cell must contain a lamp");
    }

    return litPath(r, c, 0, 1)
        || litPath(r, c, 0, -1)
        || litPath(r, c, 1, 0)
        || litPath(r, c, -1, 0);
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
    validatePuzzle(getActivePuzzle());
    resetPuzzle();
    notifyObservers();
  }

  private void validatePuzzle(Puzzle puzzle) {
    for (int r = 0; r < puzzle.getHeight(); r++) {
      for (int c = 0; c < puzzle.getWidth(); c++) {
        CellType type = puzzle.getCellType(r, c);
        if (type != CellType.CORRIDOR && type != CellType.WALL && type != CellType.CLUE) {
          throw new IllegalStateException("Invalid cell type in puzzle: " + type);
        }
      }
    }
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
      throw new IndexOutOfBoundsException("Out of bounds");
    }
    if (active.getCellType(r, c) != CellType.CLUE) {
      throw new IllegalArgumentException("Must be a clue cell");
    }
    int expectedLamps = active.getClue(r, c);
    int adjacentLamps = 0;

    int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

    for (int[] dir : directions) {
      int newR = r + dir[0];
      int newC = c + dir[1];

      if (newR >= 0
          && newR < active.getHeight()
          && newC >= 0
          && newC < active.getWidth()
          && isLamp(newR, newC)) {
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

  private void notifyObservers() {
    for (ModelObserver observer : observers) {
      observer.update(this);
    }
  }
}
