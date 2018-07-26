package engine.collision;

public enum CornerCollision {
    LEFT_UPPER(0),
    LEFT_BOTTOM(1),
    RIGHT_UPPER(2),
    RIGHT_BOTTOM(3),
    NO_COLLISION(-1);

    private final int id;

    CornerCollision(int id){
        this.id = id;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
