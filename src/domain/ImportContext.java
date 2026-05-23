package domain;

import java.util.ArrayList;
import java.util.List;

class ImportContext {
    List<SafeZone> safeZones = new ArrayList<>();
    List<Wall> walls = new ArrayList<>();
    List<Coin> coins = new ArrayList<>();
    List<Enemy> enemies = new ArrayList<>();
    List<Bomb> bombs = new ArrayList<>();
    List<Life> lives = new ArrayList<>();
    Player player = null;
    Player player2 = null;
    int deaths;
    int deaths2;
    int score;
    int score2;
    int winner;
    boolean gameOver;
    int levelTime;
    double checkpointX, checkpointY;
    boolean hasCheckpoint;
    int playerCount;
    int deathsCount;
    int scoreCount;
}
