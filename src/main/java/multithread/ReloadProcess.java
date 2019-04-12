package multithread;

/**
 * Thread class chạy định kỳ.
 */
public class ReloadProcess extends Thread{
    private Reloadable reloadable;
    private long circleTime;

    public ReloadProcess(Reloadable reloadable, long circleTime){
        this.reloadable = reloadable;
        this.circleTime = circleTime;
    }

    @Override
    public void run() {
        while (true){
            reloadable.reload();
            try {
                Thread.sleep(circleTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }
        }
    }
}
