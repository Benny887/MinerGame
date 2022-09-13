package model.timer;

public class SimpleTimer {

    private TimerTickListener timerTick;
    private boolean stop = false;

    private Thread thread;

    public void go(TimerTickListener timerTick) {
        this.timerTick = timerTick;
        init();
        thread.start();
    }

    private void init() {
        stop = false;
        thread = new Thread(() -> {
            long startTime = System.currentTimeMillis();
            long elapsedTime = System.currentTimeMillis() - startTime;
            int sec = 0;
            long diff;
            while (!stop) {
                diff = elapsedTime / 1000;
                if (diff != sec) {
                    timerTick.tick(++sec);
                }
                elapsedTime = System.currentTimeMillis() - startTime;
            }
        });
    }

    public void end() {
        stop = true;
    }

    public interface TimerTickListener {
        void tick(int sec);
    }

}
