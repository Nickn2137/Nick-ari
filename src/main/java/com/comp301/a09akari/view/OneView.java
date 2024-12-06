package com.comp301.a09akari.view;

import com.comp301.a09akari.controller.ClassicMvcController;
import com.comp301.a09akari.model.Model;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;

import javax.swing.*;

public class OneView implements FXComponent {
  private final ClassicMvcController controller;
  private final Model model;

  public OneView(Model model, ClassicMvcController controller) {
    this.model = model;
    this.controller = controller;
  }

  @Override
  public Parent render() {
    BorderPane grid = new BorderPane();

    PuzzleView puzzle = new PuzzleView(model, controller);
    grid.setCenter(puzzle.render());
    ControlsView control = new ControlsView(model, controller);
    grid.setBottom(control.render());
    MessageView message = new MessageView(model, controller);
    grid.setTop(message.render());

    return grid;
  }
}
