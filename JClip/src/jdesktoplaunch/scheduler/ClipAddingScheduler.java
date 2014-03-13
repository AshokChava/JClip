package jdesktoplaunch.scheduler;
import java.util.Timer;
import java.util.TimerTask;
import jdesktoplaunch.ui.MainFrame;

public class ClipAddingScheduler {
    Timer timer;
    MainFrame main=null;
    public ClipAddingScheduler(MainFrame main,int seconds) {
        this.main=main;
        timer = new Timer();  //At this line a new Thread will be created
        timer.scheduleAtFixedRate(new ClipAddTask(), seconds*1000,seconds*1000); //delay in milliseconds
    }

    class ClipAddTask extends TimerTask {

        @Override
        public void run() {
            main.addFromClipBoard();
            //timer..cancel();
            
        }
    }

    
}


