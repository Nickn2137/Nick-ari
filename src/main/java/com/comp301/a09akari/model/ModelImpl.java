package com.comp301.a09akari.model;

import javafx.scene.control.Cell;

import java.util.ArrayList;
import java.util.List;

public class ModelImpl implements Model {
  private final PuzzleLibrary library;
  private int currentPuzzleIndex;
  private int[][] lamps;
  private final List<ModelObserver> observers;

  public ModelImpl(PuzzleLibrary library) {
    if (library == null || library.size() == 0) {
      throw new IllegalArgumentException("Library cannot be null or empty");
    }
    this.library = library;
    this.currentPuzzleIndex = 0;
    this.lamps =
        new int[library.getPuzzle(currentPuzzleIndex).getHeight()]
            [library.getPuzzle(currentPuzzleIndex).getWidth()];
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

    lamps[r][c] = 1;

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

    lamps[r][c] = 0;
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

    if (isLamp(r, c)) {
      return true;
    }

    for (int i = r - 1; i >= 0; i--) {
      if (active.getCellType(i, c) == CellType.WALL || active.getCellType(i, c) == CellType.CLUE) {
        break;
      }
      if (isLamp(i, c)) {
        return true;
      }
    }
    for (int i = r + 1; i < active.getHeight(); i++) {
      if (active.getCellType(i, c) == CellType.WALL || active.getCellType(i, c) == CellType.CLUE) {
        break;
      }
      if (isLamp(i, c)) {
        return true;
      }
    }
    for (int i = c - 1; i >= 0; i--) {
      if (active.getCellType(r, i) == CellType.WALL || active.getCellType(r, i) == CellType.CLUE) {
        break;
      }
      if (isLamp(r, i)) {
        return true;
      }
    }
    for (int i = c + 1; i < active.getWidth(); i++) {
      if (active.getCellType(r, i) == CellType.WALL || active.getCellType(r, i) == CellType.CLUE) {
        break;
      }
      if (isLamp(r, i)) {
        return true;
      }
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
      throw new IllegalArgumentException("Must be a corridor");
    }
    return lamps[r][c] == 1;
  }

  @Override
  public boolean isLampIllegal(int r, int c) {
    Puzzle active = getActivePuzzle();
    if (r < 0 || r >= active.getHeight() || c < 0 || c >= active.getWidth()) {
      throw new IndexOutOfBoundsException("Out of bounds");
    }
    if (active.getCellType(r, c) != CellType.CORRIDOR) {
      throw new IllegalArgumentException("Must be a corridor");
    }

    for (int i = r - 1; i >= 0; i--) {
      if (active.getCellType(i, c) == CellType.WALL || active.getCellType(i, c) == CellType.CLUE) {
        break;
      }
      if (isLamp(i, c)) {
        return true;
      }
    }
    for (int i = r + 1; i < active.getHeight(); i++) {
      if (active.getCellType(i, c) == CellType.WALL || active.getCellType(i, c) == CellType.CLUE) {
        break;
      }
      if (isLamp(i, c)) {
        return true;
      }
    }
    for (int i = c - 1; i >= 0; i--) {
      if (active.getCellType(r, i) == CellType.WALL || active.getCellType(r, i) == CellType.CLUE) {
        break;
      }
      if (isLamp(r, i)) {
        return true;
      }
    }
    for (int i = c + 1; i < active.getWidth(); i++) {
      if (active.getCellType(r, i) == CellType.WALL || active.getCellType(r, i) == CellType.CLUE) {
        break;
      }
      if (isLamp(r, i)) {
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
  }

  @Override
  public int getPuzzleLibrarySize() {
    return library.size();
  }

  @Override
  public void resetPuzzle() {
    Puzzle current = library.getPuzzle(currentPuzzleIndex);
    lamps = new int[current.getHeight()][current.getWidth()];
    notifyObservers();
  }

  @Override
  public boolean isSolved() {
    Puzzle active = getActivePuzzle();

    for (int r = 0; r < active.getHeight(); r++) {
      for (int c = 0; c < active.getWidth(); c++) {
        if (active.getCellType(r, c) == CellType.CORRIDOR) {
          if (!isLit(r, c)) {
          return false;
          }
          if (isLamp(r, c) && isLampIllegal(r, c)) {
          return false;
          }
        }
        if (active.getCellType(r, c) == CellType.CLUE) {
          if (!isClueSatisfied(r, c)) {
            return false;
          }
        }
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

    if (r > 0 && active.getCellType(r -1, c) == CellType.CORRIDOR && isLamp(r - 1, c)) {
      adjacentLamps++;
    }
    if (r < active.getHeight() - 1 && active.getCellType(r + 1, c) == CellType.CORRIDOR && isLamp(r + 1, c)) {
      adjacentLamps++;
    }
    if (c > 0 && active.getCellType(r, c - 1) == CellType.CORRIDOR && isLamp(r, c - 1)) {
      adjacentLamps++;
    }
    if (c < active.getWidth() - 1 && active.getCellType(r, c + 1) == CellType.CORRIDOR && isLamp(r, c + 1)) {
      adjacentLamps++;
    }
    return adjacentLamps == expectedLamps;
  }

  @Override
  public void addObserver(ModelObserver observer) {
    observers.add(observer);
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
