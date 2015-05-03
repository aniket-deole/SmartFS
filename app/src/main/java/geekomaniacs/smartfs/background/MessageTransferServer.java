package geekomaniacs.smartfs.background;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by aniket on 5/2/15.
 */
public class MessageTransferServer extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return 0;
    }

    @Override
    public void onCreate () {

    }

    @Override
    public void onDestroy () {

    }

}

