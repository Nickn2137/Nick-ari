package com.comp301.a09akari.view;

import com.comp301.a09akari.controller.ClassicMvcController;
import com.comp301.a09akari.model.CellType;
import com.comp301.a09akari.model.Model;
import javafx.scene.control.Button;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.layout.GridPane;
import javafx.event.ActionEvent;
import javafx.scene.paint.Color;

public class PuzzleView implements FXComponent {
  private final Model model;
  private final ClassicMvcController controller;

  public PuzzleView(Model model, ClassicMvcController controller) {
    this.model = model;
    this.controller = controller;
  }

  @Override
  public Parent render() {
    GridPane gridPane = new GridPane();
    gridPane.setAlignment(Pos.CENTER);
    gridPane.setPadding(new Insets(10));
    gridPane.setHgap(5);
    gridPane.setVgap(5);

    int height = model.getActivePuzzle().getHeight();
    int width = model.getActivePuzzle().getWidth();

    for (int r = 0; r < height; r++) {
      for (int c = 0; c < width; c++) {
        Button cell = createCell(r, c);
        gridPane.add(cell, c, r);
      }
    }
    return gridPane;
  }

  private Button createCell(int r, int c) {
    CellType cellType = model.getActivePuzzle().getCellType(r, c);
    Button cell = new Button();
    cell.setMinSize(30, 30);
    cell.setMaxSize(30, 30);
    cell.setPrefSize(30, 30);

    switch (cellType) {
      case CORRIDOR:
        setCorridor(cell, r, c);
        break;
      case WALL:
        setWall(cell);
        break;
      case CLUE:
        setClue(cell, r, c);
        break;
      default:
        break;
    }
    return cell;
  }

  private void setCorridor(Button cell, int r, int c) {
    cell.setOnAction((ActionEvent event) -> controller.clickCell(r, c));

    if (model.isLit(r, c)) {
      cell.setStyle("-fx-background-color: #FFF2A6;");
    }
    if (model.isLamp(r, c)) {
      cell.setText("\uD83D\uDCA1");
      cell.setStyle("-fx-background-color: #FEC89A;");
    }
    if (model.isLamp(r, c) && model.isLampIllegal(r, c)) {
      cell.setStyle("-fx-background-color: #F08080;");
    }
  }

  private void setWall(Button cell) {
    cell.setStyle("-fx-background-color: #575656;");
  }

  private void setClue(Button cell, int r, int c) {
    int clue = model.getActivePuzzle().getClue(r, c);
    cell.setText(String.valueOf(clue));
    cell.setStyle("-fx-background-color: #4B4580;");
    cell.setTextFill(Color.WHITE);

    if (model.isClueSatisfied(r, c)) {
      cell.setStyle("-fx-background-color: #556B2F;");
    }
  }
}
