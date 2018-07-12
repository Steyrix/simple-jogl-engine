package states;

public interface GameState {
    void init();
    void dispose();
    void draw();
    void reshape();
}
