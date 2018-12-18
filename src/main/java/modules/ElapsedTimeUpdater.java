package modules;


public class ElapsedTimeUpdater {
    private final long updatePeriod;
    private long timeAccumulator;
    private boolean isReady;
    private updateFunc func;

    public ElapsedTimeUpdater(long updatePeriod) {
        this.updatePeriod = updatePeriod;
        this.timeAccumulator = 0;
        this.isReady = false;
    }

    public ElapsedTimeUpdater(long updatePeriod, updateFunc func) {
        this.updatePeriod = updatePeriod;
        this.timeAccumulator = 0;
        this.func = func;
        this.isReady = true;
    }

    private boolean condition() {
        return timeAccumulator >= updatePeriod && isReady;
    }

    private void execUpdateFuncOnCondition() {
        if(condition())
            func.exec();
    }

    public void resetFunc(updateFunc func){
        this.func = func;
        this.isReady = true;
    }

    public void update() {
        ++this.timeAccumulator;
        execUpdateFuncOnCondition();
    }
}
