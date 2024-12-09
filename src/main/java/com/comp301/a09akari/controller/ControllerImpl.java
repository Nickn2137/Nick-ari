package com.comp301.a09akari.controller;

import com.comp301.a09akari.model.Model;

import java.util.Random;

public class ControllerImpl implements ClassicMvcController {
  private final Model model;

  public ControllerImpl(Model model) {
    if (model == null) {
      throw new IllegalArgumentException("Model cannot be null");
    }
    this.model = model;
  }

  @Override
  public void clickNextPuzzle() {
    int currentIndex = model.getActivePuzzleIndex();
    int librarySize = model.getPuzzleLibrarySize();

    if (currentIndex != librarySize - 1) {
      model.setActivePuzzleIndex(currentIndex + 1);
    }
  }

  @Override
  public void clickPrevPuzzle() {
    int currentIndex = model.getActivePuzzleIndex();

    if (currentIndex != 0) {
      model.setActivePuzzleIndex(currentIndex - 1);
      }
  }

  @Override
  public void clickRandPuzzle() {
    int librarySize = model.getPuzzleLibrarySize();
    int currentIndex = model.getActivePuzzleIndex();

    Random random = new Random();
    int randomIndex = random.nextInt(librarySize);

    while (randomIndex == currentIndex) {
      randomIndex = random.nextInt(librarySize);
    }

    System.out.println("Intended Index at Rand: " + randomIndex);

    model.setActivePuzzleIndex(randomIndex);
  }

  @Override
  public void clickResetPuzzle() {
    model.resetPuzzle();
  }

  @Override
  public void clickCell(int r, int c) {
    try {
      if (model.isLamp(r, c)) {
        model.removeLamp(r, c);
      } else {
        model.addLamp(r, c);
      }
    } catch (IllegalArgumentException | IndexOutOfBoundsException e) {
      System.out.println("Invalid cell click: " + e.getMessage());
    }
  }
}
