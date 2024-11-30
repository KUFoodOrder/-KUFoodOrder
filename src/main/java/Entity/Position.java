package Entity;

public class Position {
    private static final int MIN_VALUE = 0;
    private static final int MAX_VALUE = 9999;

    private static final String INVALID_POINT = String.format("위치 값은 %s ~ %s 사이의 값이여야 합니다", MIN_VALUE, MAX_VALUE);

    private int x;
    private int y;

    public Position(int x, int y) {
        if(invalidRange(x) || invalidRange(y)) {
            throw new IllegalArgumentException(INVALID_POINT);
        }
        this.x = x;
        this.y = y;
    }

    private boolean invalidRange(int point) {
        return point < MIN_VALUE || MAX_VALUE < point;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public String toString() {
        return "Position{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}