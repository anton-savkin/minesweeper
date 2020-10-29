package com.javarush.games.minesweeper;

import com.javarush.engine.cell.Color;
import com.javarush.engine.cell.Game;

import java.util.ArrayList;
import java.util.List;

public class MinesweeperGame extends Game {
    private static final int SIDE = 9;
    private GameObject[][] gameField = new GameObject[SIDE][SIDE];
    private int countMinesOnField = 0;
    private int countClosedTiles = SIDE*SIDE;
    private int countFlags;
    private static final String MINE = "\uD83D\uDCA3";
    private static final String FLAG = "\uD83D\uDEA9";
    private boolean isGameStopped;
    private int score = 0;


    @Override
    public void initialize() {
        setScreenSize(SIDE, SIDE);
        createGame();
    }

    private void createGame() {
        //isGameStopped = false;
        for (int y = 0; y < SIDE; y++) {
            for (int x = 0; x < SIDE; x++) {
                boolean isMine = getRandomNumber(10) < 1;
                if (isMine) {
                    countMinesOnField++;
                }
                gameField[y][x] = new GameObject(x, y, isMine);
                setCellValueEx(x, y, Color.ORANGE,  "");
            }
        }
        countMineNeighbors();
        countFlags = countMinesOnField;
    }

    private List<GameObject> getNeighbors(GameObject gameObject) {
        List<GameObject> result = new ArrayList<>();
        for (int y = gameObject.y - 1; y <= gameObject.y + 1; y++) {
            for (int x = gameObject.x - 1; x <= gameObject.x + 1; x++) {
                if (y < 0 || y >= SIDE) {
                    continue;
                }
                if (x < 0 || x >= SIDE) {
                    continue;
                }
                if (gameField[y][x] == gameObject) {
                    continue;
                }
                result.add(gameField[y][x]);
            }
        }
        return result;
    }

    private void countMineNeighbors(){
        for (GameObject[] column: gameField){
            for (GameObject cell: column){
                if (!cell.isMine){
                    for (GameObject neighbor: getNeighbors(cell)){
                        if (neighbor.isMine) cell.countMineNeighbors++;
                    }
                }
            }
        }
    }

    private void openTile(int x, int y){
        if (!gameField[y][x].isOpen && !gameField[y][x].isFlag && isGameStopped==false) {
            countClosedTiles--;
            gameField[y][x].isOpen = true;
            if (gameField[y][x].isMine) {
                setCellValueEx(x, y, Color.RED, MINE);
                gameOver();
            } else {
                score += 5;
                setScore(score);
                if (countClosedTiles==countMinesOnField) win();
                else{
                    if (gameField[y][x].countMineNeighbors != 0) setCellNumber(x, y, gameField[y][x].countMineNeighbors);
                    else {
                        setCellValue(x, y, "");
                        for (GameObject cell : getNeighbors(gameField[y][x])) {
                            if (!cell.isOpen) openTile(cell.x, cell.y);
                        }
                    }
                }
            }
            setCellColor(x, y, Color.GREEN);
        }

    }

    @Override
    public void onMouseLeftClick(int x, int y) {
        if (isGameStopped) restart();
        else openTile(x, y);
    }

    private void markTile(int x, int y){
        if (isGameStopped!=true){
            if (!gameField[y][x].isOpen){
                if (gameField[y][x].isFlag){
                    gameField[y][x].isFlag = false;
                    countFlags++;
                    setCellValue(x, y, "");
                    setCellColor(x,y, Color.ORANGE);
                } else {
                    if (countFlags!=0){
                        gameField[y][x].isFlag = true;
                        countFlags--;
                        setCellValue(x, y, FLAG);
                        setCellColor(x,y, Color.YELLOW);
                    }
                }
            }
        }

    }

    @Override
    public void onMouseRightClick(int x, int y) {
        markTile(x, y);
    }

    private void gameOver(){
        isGameStopped = true;
        showMessageDialog(Color.WHITE, "Game Over", Color.BLACK, 80);
    }

    private void restart(){
        isGameStopped = false;
        countMinesOnField = 0;
        countClosedTiles = SIDE*SIDE;
        score = 0;
        setScore(score);
        createGame();
    }

    private void win(){
        isGameStopped = true;
        showMessageDialog(Color.WHITE, "Congratulations!", Color.BLACK, 80);
    }
}