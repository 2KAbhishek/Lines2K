package com.iam2kabhishek.lines;

import android.util.Log;
import android.widget.Button;
import android.widget.TableLayout;

import java.util.HashSet;
import java.util.Map;
import java.util.Random;

public class GameLogic {
    private final HashSet<Button> availableCells = new HashSet<>();
    int score;
    TableLayout tableLayout;
    boolean gameOver = false;
    int colorIndex;
    int numberOfBallsSuccession = 1;
    HashSet<Button> buttonsToExplosion = new HashSet();
    boolean alreadyCheckReverse = false;
    int[] threeRandomColors;

    public static <T, E> T getKeyByValue(Map<T, E> map, E value) {
        for (Map.Entry<T, E> entry : map.entrySet()) {
            if (entry.getValue().equals(value)) {
                return entry.getKey();
            }
        }
        return null;
    }

    public boolean checkSuccessionAllDirections(Button button) {
        boolean doExplosion = false;
        for (int direction = 0; direction < 8; direction++) {
            if (checkSuccessionByDirection(button, direction)) ;
            {
                doExplosion = true;
            }
        }
        return doExplosion;
    }

    public boolean checkSuccessionByDirection(Button button, int direction) {
        boolean doExplosion = false;
        int currentLocation = GameManager.idAllCells.get(button);
        Button nearButton = getKeyByValue(GameManager.idAllCells, currentLocation - 1);

        Button[] buttonsDirection = {getKeyByValue(GameManager.idAllCells, currentLocation - 1), getKeyByValue(GameManager.idAllCells, currentLocation - 10), getKeyByValue(GameManager.idAllCells, currentLocation - 9), getKeyByValue(GameManager.idAllCells, currentLocation - 8), getKeyByValue(GameManager.idAllCells, currentLocation + 1), getKeyByValue(GameManager.idAllCells, currentLocation + 10), getKeyByValue(GameManager.idAllCells, currentLocation + 9), getKeyByValue(GameManager.idAllCells, currentLocation + 8)};
        for (int i = 0; i < buttonsDirection.length; i++) {
            if (direction == i) {
                nearButton = buttonsDirection[i];
            }
        }
        buttonsToExplosion.add(button);

        checkIfNearButtonIsSameColor(button, nearButton, direction);

        if (numberOfBallsSuccession > 1 && alreadyCheckReverse == false) {
            if (!alreadyCheckReverse) {
                numberOfBallsSuccession = 1;
            }

            if (direction == 0) {
                alreadyCheckReverse = true;
                checkIfNearButtonIsSameColor(button, getKeyByValue(GameManager.idAllCells, currentLocation + 1), 4);

            } else if (direction == 1) {
                alreadyCheckReverse = true;
                checkIfNearButtonIsSameColor(button, getKeyByValue(GameManager.idAllCells, currentLocation + 10), 5);
            } else if (direction == 2) {
                alreadyCheckReverse = true;
                checkIfNearButtonIsSameColor(button, getKeyByValue(GameManager.idAllCells, currentLocation + 9), 6);
            } else if (direction == 3) {
                alreadyCheckReverse = true;
                checkIfNearButtonIsSameColor(button, getKeyByValue(GameManager.idAllCells, currentLocation + 8), 7);
            } else if (direction == 4) {
                alreadyCheckReverse = true;
                checkIfNearButtonIsSameColor(button, getKeyByValue(GameManager.idAllCells, currentLocation - 1), 0);
            } else if (direction == 5) {
                alreadyCheckReverse = true;
                checkIfNearButtonIsSameColor(button, getKeyByValue(GameManager.idAllCells, currentLocation - 10), 1);
            } else if (direction == 6) {
                alreadyCheckReverse = true;
                checkIfNearButtonIsSameColor(button, getKeyByValue(GameManager.idAllCells, currentLocation - 9), 2);
            } else if (direction == 7) {
                alreadyCheckReverse = true;
                checkIfNearButtonIsSameColor(button, getKeyByValue(GameManager.idAllCells, currentLocation - 8), 3);
            }
        }
        if (numberOfBallsSuccession > 4) {
            doExplosion = true;
            numberOfBallsSuccession = 1;
            doExplosion(buttonsToExplosion);
            buttonsToExplosion.clear();
            alreadyCheckReverse = false;
            return doExplosion;
        } else {
            numberOfBallsSuccession = 1;
            doExplosion = false;
            buttonsToExplosion.clear();
            alreadyCheckReverse = false;
            return doExplosion;
        }
    }

    private void checkIfNearButtonIsSameColor(Button button, Button nearButton, int direction) {

        if (nearButton != null && button != null) {
            if (!getAvailableCells().contains(nearButton)) {
                try {
                    if (button.getTag().equals(nearButton.getTag())) {
                        numberOfBallsSuccession++;
                        checkSuccessionByDirection(nearButton, direction);
                        buttonsToExplosion.add(nearButton);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void doExplosion(HashSet<Button> buttonToExplosion) {
        int numberOfBalls = 0;
        for (Button button : buttonToExplosion) {
            numberOfBalls++;
            removeButton(button);
        }
        score = score + numberOfBalls;
        clearAllAnimation();
    }

    public int getScore() {
        return score;
    }

    public void clearAllAnimation() {
        for (int i = 0; i < 81; i++) {
            getKeyByValue(GameManager.idAllCells, i).clearAnimation();
        }
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public HashSet<Button> getAvailableCells() {
        return availableCells;
    }

    public void createThreeballs(TableLayout tablelayout) {
        this.tableLayout = tablelayout;
        //creates 3 balls
        for (int i = 0; i < 3; i++) {
            colorIndex = i;
            createBall();
        }
    }

    public void createBall() {
        if (!(availableCells.size() == 0)) {

            Random random = new Random();
            int randomLocation = random.nextInt(GameManager.idAllCells.size());

            if (getAvailableCells().contains(getKeyByValue(GameManager.idAllCells, randomLocation))) {
                Log.i("getAvailableCells", "inside getAvailableCells");
                Button button = getKeyByValue(GameManager.idAllCells, randomLocation);
                for (int i = 0; i < 3; i++) {
                    if (i == colorIndex) {
                        button.setBackgroundResource(this.threeRandomColors[i]);
                        button.setTag(this.threeRandomColors[i]);
                    }
                }
                getAvailableCells().remove(button);
                checkSuccessionAllDirections(button);

                if (availableCells.size() == 0) {
                    gameOver = true;
                }
            } else {
                createBall();
            }
        } else {
            gameOver = true;
        }
    }

    public void setColorToThreeBalls(int[] threeRandomColors) {
        this.threeRandomColors = threeRandomColors;
    }

    public void removeButton(Button Button) {
        Button.setTag(null);
        Button.setBackgroundResource(R.drawable.cell);
        getAvailableCells().add(Button);
    }

}