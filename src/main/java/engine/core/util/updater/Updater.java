package engine.core.util.updater;

import org.jetbrains.annotations.NotNull;

public interface Updater {
    void update();
    void setUpdateFunc(@NotNull UpdateFunc func);
}
