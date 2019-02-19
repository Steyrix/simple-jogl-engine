package engine.core.modules.updater;

public interface Updater {
    void update();
    void setUpdateFunc(UpdateFunc func);
}
