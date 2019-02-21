public enum Direction {
    // Each direction is an offset in the x,y plane
    E(0),
    W(1),
    N(2),
    S(3),
    NE(4),
    SE(5),
    NW(6),
    SW(7),
    C(8);


    private int position;

    Direction(int position) {
        this.position = position;
    }

    public int getPosition() {
        return this.position;
    }
}
