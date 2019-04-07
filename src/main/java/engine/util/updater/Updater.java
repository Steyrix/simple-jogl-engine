package engine.util.updater;

import org.jetbrains.annotations.NotNull;

public interface Updater {
    void update();
    void setUpdateFunc(@NotNull UpdateFunc func);
}
