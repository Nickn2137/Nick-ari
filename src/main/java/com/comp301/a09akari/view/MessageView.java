package com.comp301.a09akari.view;

import com.comp301.a09akari.controller.ClassicMvcController;
import com.comp301.a09akari.model.Model;
import com.comp301.a09akari.model.ModelObserver;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class MessageView implements FXComponent {
  private final Model model;
  private final ClassicMvcController controller;

  public MessageView(Model model, ClassicMvcController controller) {
    this.model = model;
    this.controller = controller;
  }

  @Override
  public Parent render() {
    StackPane stackPane = new StackPane();
    stackPane.setPadding(new Insets(50, 0, 0, 0));
    stackPane.setAlignment(Pos.CENTER);

    VBox vbox = new VBox();
    vbox.setAlignment(Pos.CENTER);
    vbox.setSpacing(10);

    Label label;
    Label part =
        new Label(
            "Puzzle " + (model.getActivePuzzleIndex() + 1) + "/" + model.getPuzzleLibrarySize());
    if (model.isSolved()) {
      label = new Label("HOORAY!");
      label.setStyle("-fx-text-fill: #BEE3BA;");
      label.setFont(Font.font("Arial", FontWeight.BOLD, 40));
    } else {
      label = new Label(" ");
    }
    vbox.getChildren().addAll(label, part);
    part.setStyle("-fx-text-fill: white;");
    part.setFont(Font.font("Arial", FontWeight.BOLD, 25));

    stackPane.getChildren().add(vbox);
    return stackPane;
  }
}
