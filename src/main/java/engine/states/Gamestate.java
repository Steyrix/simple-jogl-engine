package engine.states;

public enum Gamestate {
    GAME_PAUSED(0),
    GAME_ACTIVE(1),
    GAME_MENU(2);

    private int id;

    Gamestate(int id){
        this.id = id;
    }

}
