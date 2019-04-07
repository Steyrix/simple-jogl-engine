package engine.util.updater;


public class ElapsedTimeUpdater implements Updater {

    private UpdateFunc func;

    private final long updatePeriod;
    private long timeAccumulator;

    public ElapsedTimeUpdater(final long updatePeriod) {
        this.updatePeriod = updatePeriod;
        this.timeAccumulator = 0;
    }

    @Override
    public void setUpdateFunc(UpdateFunc func) {
        this.func = func;
    }

    @Override
    public void update() {
        ++this.timeAccumulator;
        execUpdateFuncOnCondition();
    }

    private boolean condition() {
        return timeAccumulator >= updatePeriod && func != null;
    }

    private void execUpdateFuncOnCondition() {
        if (condition()) func.exec();
    }
}
