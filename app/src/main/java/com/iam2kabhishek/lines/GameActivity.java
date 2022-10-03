package com.iam2kabhishek.lines;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

public class GameActivity extends AppCompatActivity {
    GameLogic gameLogic = new GameLogic();
    ArrayList<GameButton> allGameButtons = new ArrayList<>();
    TableLayout tableLayout;
    int idCell = 0;
    Button currentCellClicked, lastCellClicked, restart;
    ImageView leftBall, centralBall, rightBall;
    TextView currentScore;
    Animation animationBounce;
    Toast noAvailablePath;
    Dialog gameOverDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        leftBall = findViewById(R.id.imageviewleftball);
        centralBall = findViewById(R.id.imageviewcenterball);
        rightBall = findViewById(R.id.imageviewrightball);
        restart = findViewById(R.id.restart);
        currentScore = findViewById(R.id.tvCurentScore);
        noAvailablePath = Toast.makeText(getApplicationContext(), "Sorry! No way available", Toast.LENGTH_SHORT);
        gameOverDialog = new Dialog(this);
        randomNextBalls();
        restart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNewGame();
            }
        });
        allGameButtons = new ArrayList<>();
        tableLayout = findViewById(R.id.tablelayout);
        animationBounce = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.animation);
        for (int i = 0; i < 9; i++) {
            TableRow tableRow = (TableRow) tableLayout.getChildAt(i);
            for (int j = 0; j < 9; j++) {
                GameButton button = (GameButton) tableRow.getChildAt(j);
                button.setBtnId(idCell);
                GameManager.idAllCells.put(button, idCell);
                allGameButtons.add(button);

                idCell++;
                gameLogic.getAvailableCells().add(button);
                setButtonListener(button);
            }

        }
        gameLogic.createThreeballs(tableLayout);
        Integer[] startOfRow = {0, 9 * 1, 9 * 2, 9 * 3, 9 * 4, 9 * 5, 9 * 6, 9 * 7, 9 * 8};

        Integer[] endOfRow = {9 * 1 - 1, 9 * 2 - 1, 9 * 3 - 1, 9 * 4 - 1, 9 * 5 - 1, 9 * 6 - 1, 9 * 7 - 1, 9 * 8 - 1, 9 * 9 - 1};

        for (Integer i = 0; i < allGameButtons.size(); i++) {
            if (!Arrays.asList(endOfRow).contains(i)) {
                if (i < 80) {
                    allGameButtons.get(i).setRightNeighbor(allGameButtons.get(i + 1));
                }
            }

            if (!Arrays.asList(startOfRow).contains(i)) {
                if (i > 0) {
                    allGameButtons.get(i).setLeftNeighbor(allGameButtons.get(i - 1));
                }
            }

            if (i > 8) {
                allGameButtons.get(i).setTopNeighbor(allGameButtons.get(i - 9));
            }
            if (i < 71) {
                allGameButtons.get(i).setBottomNeighbor(allGameButtons.get(i + 9));
            }
        }
    }


    private void setButtonListener(final Button button) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkCurrentClick(button);
                animationAlgorithm(button);
                currentScore.setText("Score: " + gameLogic.getScore());
                if (gameLogic.isGameOver()) {
                    finishGame();
                }
            }
        });
    }

    private void finishGame() {
        Toast.makeText(this, "Game Over", Toast.LENGTH_SHORT).show();
        gameOverDialog.setContentView(R.layout.game_over_dialog);
        Button btNewGame = gameOverDialog.findViewById(R.id.dialoggameover_btnewgame);
        TextView finalScore = gameOverDialog.findViewById(R.id.finishScore);
        finalScore.setText("Your score: " + gameLogic.getScore());
        gameOverDialog.show();
        btNewGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNewGame();
            }
        });
    }


    private void checkCurrentClick(Button button) {

        if (lastCellClicked == null && currentCellClicked == null) {
            lastCellClicked = button;
            currentCellClicked = button;
        } else {
            lastCellClicked = currentCellClicked;
            currentCellClicked = button;
        }

        if (lastCellClicked.getAnimation() != null && gameLogic.getAvailableCells().contains(currentCellClicked) && isMovePossible(lastCellClicked, currentCellClicked)) {
            doMove(lastCellClicked, currentCellClicked);
            gameLogic.createThreeballs(tableLayout);
            randomNextBalls();

        }


    }

    private void animationAlgorithm(Button button) {
        if (button.getAnimation() == null) {
            if (!(gameLogic.getAvailableCells().contains(button))) {
                gameLogic.clearAllAnimation();
                button.startAnimation(animationBounce);
            }
        } else {
            gameLogic.clearAllAnimation();
        }
    }


    private void startNewGame() {
        finish();
        GameManager.idAllCells = new HashMap<>();
        Intent i = new Intent(getApplicationContext(), GameActivity.class);
        startActivity(i);
    }

    private void doMove(Button currentCell, Button wantedCell) {
        if (gameLogic.getAvailableCells().contains(wantedCell)) {
            if (isMovePossible(currentCell, wantedCell)) {
                moveSuccessful(currentCell, wantedCell);
            } else {
                noAvailablePath.show();
            }
        } else {
            noAvailablePath.show();

        }
    }

    private void moveSuccessful(Button currentCell, Button wantedCell) {

        wantedCell.setBackground(currentCell.getBackground());
        wantedCell.setTag(currentCell.getTag());
        gameLogic.removeButton(currentCell);
        gameLogic.getAvailableCells().remove(wantedCell);
        gameLogic.checkSuccessionAllDirections(wantedCell);

    }

    private boolean isMovePossible(Button currentLocation, Button wantedLocation) {

        return new PathAlgorithm(((GameButton) currentLocation).getBtnId(), ((GameButton) wantedLocation).getBtnId(), gameLogic).checkPath();

    }

    public void randomNextBalls() {
        Random random = new Random();
        int[] colors = {R.drawable.blueball, R.drawable.greenball, R.drawable.purpleball, R.drawable.redball, R.drawable.yellowball};
        int[] threeRandomColors = {colors[random.nextInt(colors.length)], colors[random.nextInt(colors.length)], colors[random.nextInt(colors.length)]};
        gameLogic.setColorToThreeBalls(threeRandomColors);
        ImageView[] threeBalls = {leftBall, centralBall, rightBall};
        for (int i = 0; i < 3; i++) {
            threeBalls[i].setImageResource(threeRandomColors[i]);
        }
    }

}