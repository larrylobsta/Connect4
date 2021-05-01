package connect4;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class Connect4App extends Application {
    final int COLUMNS = 7;
    final int ROWS = 6;
    final int cellSize = 100;
    
    Color yellow = Color.YELLOW;
    Color red = Color.INDIANRED;
    Color white = Color.WHITE;

    boolean yellowTurn = true;
    boolean playingCPU = false;
    boolean gameOver = false;

    Slot[][] grid = new Slot[COLUMNS][ROWS];

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        BorderPane root = new BorderPane();
        Pane gamePane = new Pane();
        gamePane.getChildren().add(makeBoard());
        gamePane.getChildren().addAll(makeSlots());
        gamePane.getChildren().addAll(makeColumns());

        root.setCenter(gamePane);
        root.setRight(makeSidePane());

        primaryStage.setTitle("Connect4");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public Pane makeSidePane() {
        VBox vbox = new VBox();
        vbox.setAlignment(Pos.TOP_CENTER);
        Button newGame = new Button("New Game");
        newGame.setOnAction(e -> resetGame());
        Button playCPU = new Button("Play CPU");
        playCPU.setOnAction(e->playAgainstCPU());
        vbox.getChildren().add(newGame);
        vbox.getChildren().add(playCPU);

        return vbox;
    }

    public Shape makeBoard() {
        Shape board = new Rectangle(COLUMNS * cellSize, ROWS * cellSize);
        board.setFill(new Color(0.2,0.4,0.8,1));
        return board;
    }

    public List<Slot> makeSlots() {
        List<Slot> slots = new ArrayList<>();
        for (int x = 0; x < COLUMNS; x++) {
            for (int y = 0; y < ROWS; y++) {
                Slot slot = new Slot();
                slot.setCenterX(cellSize / 2);
                slot.setCenterY(cellSize / 2);
                slot.setTranslateX(x * cellSize);
                slot.setTranslateY(y * cellSize);

                slots.add(slot);
                grid[x][y] = slot; // places disks on the disk grid
            }
        }
        return slots;
    }

    public List<Rectangle> makeColumns() {
        List<Rectangle> columns = new ArrayList<>();
        for (int x = 0; x < COLUMNS; x++) {
            Rectangle column = new Rectangle(cellSize, ROWS * cellSize);
            column.setTranslateX(x * cellSize);
            column.setFill(Color.TRANSPARENT);

            final int columnNum = x;
            column.setOnMouseClicked(e -> placeDisk(columnNum));
            columns.add(column);
        }
        return columns;
    }

    public void placeDisk(int columnNum) {
        if (!grid[columnNum][0].isEmpty()) {
            return;
        } else if (!gameOver) {
            for (int i = ROWS - 1; i >= 0; i--) {
                if (grid[columnNum][i].isEmpty()) {
                    grid[columnNum][i].changeState(yellowTurn ? 1 : 2);
                    if (checkForWin(yellowTurn ? 1 : 2)) {
                        gameOver = true;
                    } else if (checkFullBoard()) {
                        gameOver = true;
                    }
                    yellowTurn = !yellowTurn;
                    if (playingCPU) {
                        CPUturn();
                    }
                    break;
                }
            }
        }
    }

    public void CPUturn() {
        Random rand = new Random();
        int columnNum = rand.nextInt(COLUMNS - 1);
        while (!grid[columnNum][0].isEmpty()) {
            columnNum = columnNum++ % (COLUMNS - 1);
        }

        if (!gameOver) {
            for (int i = ROWS - 1; i >= 0; i--) {
                if (grid[columnNum][i].isEmpty()) {
                    grid[columnNum][i].changeState(yellowTurn ? 1 : 2);
                    if (checkForWin(yellowTurn ? 1 : 2)) {
                        gameOver = true;
                    }
                    yellowTurn = !yellowTurn;
                    break;
                }
            }
        }
    }

    public boolean checkFullBoard() {
        for (int i = 0; i < COLUMNS; i++) {
            if (grid[i][0].isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public boolean checkForWin(int i) {
        int count = 0;
        //check horizontally
        for (int x = 0; x < COLUMNS - 3; x++) {
            // horizontal
            for (int y = 0; y < ROWS; y++) {
                if (grid[x][y].getState() == i && grid[x + 1][y].getState() == i && grid[x + 2][y].getState() == i && grid[x + 3][y].getState() == i) {
                    highlightWin(grid[x][y],grid[x + 1][y],grid[x + 2][y],grid[x + 3][y] );
                    return true;
                }
            }
            // descending diagonal
            for (int y = 3; y < ROWS; y++) {
                if (grid[x][y].getState() == i && grid[x + 1][y - 1].getState() == i && grid[x + 2][y - 2].getState() == i && grid[x + 3][y - 3].getState() == i) {
                    highlightWin(grid[x][y],grid[x + 1][y - 1],grid[x + 2][y - 2],grid[x + 3][y - 3]);
                    return true;
                }
            }
            // ascending diagonal
            for (int y = 0; y < ROWS - 3; y++) {
                if (grid[x][y].getState() == i && grid[x + 1][y + 1].getState() == i && grid[x + 2][y + 2].getState() == i && grid[x + 3][y + 3].getState() == i) {
                    highlightWin(grid[x][y],grid[x + 1][y + 1],grid[x + 2][y + 2],grid[x + 3][y + 3]);
                    return true;
                }
            }
        }
        //check vertically
        for (int x = 0; x < COLUMNS; x++) {
            for (int y = 0; y < ROWS - 3; y++) {
                if (grid[x][y].getState() == i && grid[x][y + 1].getState() == i && grid[x][y + 2].getState() == i && grid[x][y + 3].getState() == i) {
                    highlightWin(grid[x][y],grid[x][y + 1],grid[x][y + 2],grid[x][y + 3]);
                    return true;
                }
            }
        }
        return false;
    }

    public void highlightWin(Slot s1, Slot s2, Slot s3, Slot s4) {
        s1.setStroke(Color.LIGHTGREEN);
        s1.setStrokeWidth(5);
        s2.setStroke(Color.LIGHTGREEN);
        s2.setStrokeWidth(5);
        s3.setStroke(Color.LIGHTGREEN);
        s3.setStrokeWidth(5);
        s4.setStroke(Color.LIGHTGREEN);
        s4.setStrokeWidth(5);
    }

    public void resetGame() {
        for (int x = 0; x < COLUMNS; x++) {
            for (int y = 0; y < ROWS; y++) {
                grid[x][y].changeState(0);
                grid[x][y].setStrokeWidth(0);
            }
        }
        gameOver = false;
    }

    public void playAgainstCPU() {
        if (gameOver) {
            for (int x = 0; x < COLUMNS; x++) {
                for (int y = 0; y < ROWS; y++) {
                    grid[x][y].changeState(0);
                    grid[x][y].setStrokeWidth(0);
                }
            }
            gameOver = false;
        }
        playingCPU = true;
    }

    private class Slot extends Circle {
        int state; // 0 represents empty slot, 1 means occupied by yellow, 2 means occupied by red

        public Slot() {
            super(cellSize / 2.2);
            state = 0;
            setFill(white);
        }

        public void changeState(int i) {
            state = i;
            if (i == 0) {
                setFill(white);
            } else if (i == 1) {
                setFill(yellow);
            } else if (i == 2) {
                setFill(red);
            }
        }

        public boolean isEmpty() {
            return state == 0;
        }

        public int getState() {
            return state;
        }
    }
}
