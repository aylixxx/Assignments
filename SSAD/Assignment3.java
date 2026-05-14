import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        int N = Integer.parseInt(scanner.nextLine());
        Position greenPos = readPosition(scanner.nextLine());
        Position redPos = readPosition(scanner.nextLine());
        int M = Integer.parseInt(scanner.nextLine());

        List<Coin> coins = new ArrayList<>();
        for (int i = 0; i < M; i++) {
            String[] parts = scanner.nextLine().split(" ");
            int x = Integer.parseInt(parts[0]);
            int y = Integer.parseInt(parts[1]);
            int v = Integer.parseInt(parts[2]);
            coins.add(new Coin(x, y, v));
        }

        int P = Integer.parseInt(scanner.nextLine());
        List<String[]> actions = new ArrayList<>();
        for (int i = 0; i < P; i++) {
            String line = scanner.nextLine();
            String[] parts = line.split(" ");
            actions.add(new String[]{parts[0], parts[1]});
        }

        Game game = new Game(N, greenPos, redPos, coins);

        for (int i = 0; i < actions.size(); i++) {
            String[] action = actions.get(i);
            String output = game.processAction(i + 1, action[0], action[1]);
            System.out.println(output);
        }

        System.out.println(game.getResult());
    }

    private static Position readPosition(String line) {
        String[] parts = line.split(" ");
        return new Position(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
    }
}

enum TeamColor { GREEN, RED }

enum Direction { UP, DOWN, LEFT, RIGHT }

// Class "Position" shows the coordinates on the game board

class Position {
    int x, y;

    Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Position position = (Position) o;
        return x == position.x && y == position.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}

// Class "Coin" shows a coin on the game board with coordinates and value

class Coin {
    int x, y, value;

    Coin(int x, int y, int value) {
        this.x = x;
        this.y = y;
        this.value = value;
    }
}

// State pattern for movement

interface MovementState {
    Position getMovePosition(Position current, Direction direction);
}

// Normal movement style moves by 1 cell in the chosen direction

class NormalState implements MovementState {
    @Override
    public Position getMovePosition(Position current, Direction direction) {
        int x = current.x;
        int y = current.y;
        switch (direction) {
            case UP: x--; break;
            case DOWN: x++; break;
            case LEFT: y--; break;
            case RIGHT: y++; break;
        }
        return new Position(x, y);
    }
}

// Attacking movement style moves by 2 cells in the chosen direction

class AttackingState implements MovementState {
    @Override
    public Position getMovePosition(Position current, Direction direction) {
        int x = current.x;
        int y = current.y;
        switch (direction) {
            case UP: x -= 2; break;
            case DOWN: x += 2; break;
            case LEFT: y -= 2; break;
            case RIGHT: y += 2; break;
        }
        return new Position(x, y);
    }
}

// Prototype Pattern for cloning

class Figure {
    private final TeamColor color;
    private Position position;
    private final boolean isClone;
    private boolean canBeCloned;
    private MovementState movementState;
    private boolean alive;

    Figure(TeamColor color, int x, int y, boolean isClone, MovementState initialState) {
        this.color = color;
        this.position = new Position(x, y);
        this.isClone = isClone;
        this.canBeCloned = !isClone;
        this.movementState = initialState;
        this.alive = true;
    }

    public TeamColor getColor() {
        return color;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(int x, int y) {
        this.position.x = x;
        this.position.y = y;
    }

    public boolean isClone() {
        return isClone;
    }

    public boolean canBeCloned() {
        return canBeCloned;
    }

    public void markAsCloned() {
        canBeCloned = false;
    }

    public MovementState getMovementState() {
        return movementState;
    }

    public void toggleStyle() {
        if (movementState instanceof NormalState) {
            movementState = new AttackingState();
        } else {
            movementState = new NormalState();
        }
    }

    public boolean isAlive() {
        return alive;
    }

    public void kill() {
        alive = false;
    }

    public Figure createClone(int newX, int newY) {
        return new Figure(color, newX, newY, true, new NormalState());
    }
}

// Composite Pattern for managing the team 

class Team {
    private final List<Figure> figures = new ArrayList<>();
    private final TeamColor color;
    private int score;

    Team(TeamColor color) {
        this.color = color;
    }

    public void addFigure(Figure figure) {
        figures.add(figure);
    }

    public Figure getFigure(String name) {
        for (Figure figure : figures) {
            if (!figure.isAlive()) continue;
            if (name.equals("GREEN")) {
                if (figure.getColor() == TeamColor.GREEN && !figure.isClone())
                    return figure;
            } else if (name.equals("GREENCLONE")) {
                if (figure.getColor() == TeamColor.GREEN && figure.isClone())
                    return figure;
            } else if (name.equals("RED")) {
                if (figure.getColor() == TeamColor.RED && !figure.isClone())
                    return figure;
            } else if (name.equals("REDCLONE")) {
                if (figure.getColor() == TeamColor.RED && figure.isClone())
                    return figure;
            }
        }
        return null;
    }

    public boolean isPositionOccupied(int x, int y) {
        for (Figure figure : figures) {
            if (figure.isAlive() && figure.getPosition().x == x && figure.getPosition().y == y)
                return true;
        }
        return false;
    }

    public Figure findEnemyAt(int x, int y) {
        for (Figure figure : figures) {
            if (figure.isAlive() && figure.getPosition().x == x && figure.getPosition().y == y)
                return figure;
        }
        return null;
    }

    public void addToScore(int value) {
        score += value;
    }

    public int getScore() {
        return score;
    }

    public TeamColor getColor() {
        return color;
    }
}

// Core class for logic

class Game {
    private final int boardSize;
    private final Team greenTeam;
    private final Team redTeam;
    private final Map<Position, Coin> coins;

    // Initializes the game board with figures and coins

    Game(int boardSize, Position greenPos, Position redPos, List<Coin> initialCoins) {
        this.boardSize = boardSize;
        greenTeam = new Team(TeamColor.GREEN);
        redTeam = new Team(TeamColor.RED);

        Figure greenFigure = new Figure(TeamColor.GREEN, greenPos.x, greenPos.y, false, new NormalState());
        greenTeam.addFigure(greenFigure);

        Figure redFigure = new Figure(TeamColor.RED, redPos.x, redPos.y, false, new NormalState());
        redTeam.addFigure(redFigure);

        coins = new HashMap<>();
        for (Coin coin : initialCoins) {
            coins.put(new Position(coin.x, coin.y), coin);
        }
    }

    /**
     * Processes a game action and returns the result
     * @param actionNumber Action sequence number
     * @param figureName Figure to act
     * @param actionType Action type
     * @return Result message
     */

    public String processAction(int actionNumber, String figureName, String actionType) {
        boolean isGreenTurn = (actionNumber % 2) == 1;
        Team team = isGreenTurn ? greenTeam : redTeam;
        Team enemyTeam = isGreenTurn ? redTeam : greenTeam;

        Figure figure = team.getFigure(figureName);
        if (figure == null || !figure.isAlive())
            return "INVALID ACTION";

        switch (actionType) {
            case "COPY":
                return handleCopyAction(figure, team);
            case "STYLE":
                return handleStyleAction(figure);
            default:
                return handleMoveAction(figure, Direction.valueOf(actionType), team, enemyTeam);
        }
    }

    private String handleCopyAction(Figure figure, Team team) {
        if (!figure.canBeCloned() || figure.isClone())
            return "INVALID ACTION";
        Position pos = figure.getPosition();
        if (pos.x == pos.y)
            return "INVALID ACTION";
        int targetX = pos.y;
        int targetY = pos.x;
        if (targetX < 1 || targetX > boardSize || targetY < 1 || targetY > boardSize)
            return "INVALID ACTION";
        if (isPositionOccupied(targetX, targetY) || coins.containsKey(new Position(targetX, targetY)))
            return "INVALID ACTION";

        Figure clone = figure.createClone(targetX, targetY);
        team.addFigure(clone);
        figure.markAsCloned();
        String figureType = figure.isClone() ? "CLONE" : "";
        return figure.getColor() + (figureType.isEmpty() ? "" : "CLONE") + " CLONED TO " + targetX + " " + targetY;
    }

    private String handleStyleAction(Figure figure) {
        figure.toggleStyle();
        String newStyle = (figure.getMovementState() instanceof NormalState) ? "NORMAL" : "ATTACKING";
        String figureType = figure.isClone() ? "CLONE" : "";
        return figure.getColor() + figureType + " CHANGED STYLE TO " + newStyle;
    }

    private String handleMoveAction(Figure figure, Direction direction, Team team, Team enemyTeam) {
        Position current = figure.getPosition();
        Position newPos = figure.getMovementState().getMovePosition(current, direction);

        if (newPos.x < 1 || newPos.x > boardSize || newPos.y < 1 || newPos.y > boardSize)
            return "INVALID ACTION";

        if (team.isPositionOccupied(newPos.x, newPos.y))
            return "INVALID ACTION";

        Figure enemy = enemyTeam.findEnemyAt(newPos.x, newPos.y);
        boolean killedEnemy = false;
        if (enemy != null) {
            enemy.kill();
            killedEnemy = true;
        }

        Coin coin = coins.get(newPos);
        int coinValue = 0;
        if (coin != null) {
            coinValue = coin.value;
            team.addToScore(coinValue);
            coins.remove(newPos);
        }

        figure.setPosition(newPos.x, newPos.y);

        String figureType = figure.isClone() ? "CLONE" : "";
        String result = figure.getColor() + figureType + " MOVED TO " + newPos.x + " " + newPos.y;
        if (killedEnemy) {
            String enemyType = enemy.isClone() ? "CLONE" : "";
            result += " AND KILLED " + enemy.getColor() + enemyType;
        } else if (coinValue > 0) {
            result += " AND COLLECTED " + coinValue;
        }
        return result;
    }

    private boolean isPositionOccupied(int x, int y) {
        return greenTeam.isPositionOccupied(x, y) || redTeam.isPositionOccupied(x, y);
    }

    public String getResult() {
        int greenScore = greenTeam.getScore();
        int redScore = redTeam.getScore();

        if (greenScore == redScore) {
            return String.format("TIE. SCORE %d %d", greenScore, redScore);
        } else if (greenScore > redScore) {
            return String.format("GREEN TEAM WINS. SCORE %d %d", greenScore, redScore);
        } else {
            return String.format("RED TEAM WINS. SCORE %d %d", greenScore, redScore);
        }
    }
}