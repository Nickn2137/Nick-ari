package com.comp301.a09akari.view;

import com.comp301.a09akari.controller.ClassicMvcController;
import com.comp301.a09akari.model.Model;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

public class ControlsView implements FXComponent {
  private final ClassicMvcController controller;
  private final Model model;

  public ControlsView(Model model, ClassicMvcController controller) {

    this.controller = controller;
    this.model = model;
  }

  @Override
  public Parent render() {
    HBox controlBox = new HBox(10);

    Button prevButton = new Button("Previous");
    prevButton.setOnAction((ActionEvent event) -> controller.clickPrevPuzzle());

    Button nextButton = new Button("Next");
    nextButton.setOnAction((ActionEvent event) -> controller.clickNextPuzzle());

    Button randomButton = new Button("Random");
    randomButton.setOnAction((ActionEvent event) -> controller.clickRandPuzzle());

    Button resetButton = new Button("Reset");
    resetButton.setOnAction((ActionEvent event) -> controller.clickResetPuzzle());

    controlBox.setAlignment(Pos.BASELINE_CENTER);
    controlBox.setPadding(new Insets(0, 0, 50, 0));
    controlBox.getChildren().addAll(prevButton, nextButton, randomButton, resetButton);

    return controlBox;
  }
}
