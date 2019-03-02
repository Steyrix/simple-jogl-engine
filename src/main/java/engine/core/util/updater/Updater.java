package engine.core.util.updater;

public interface Updater {
    void update();
    void setUpdateFunc(UpdateFunc func);
}
