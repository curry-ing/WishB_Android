package com.vivavu.dream.repository.task;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.vivavu.dream.common.Constants;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import static com.vivavu.dream.common.enums.HandlerCode.SERVER_IS_REACHABLE;

/**
 * Created by yuja on 2014-04-08.
 */
public class CheckServerStatusTask implements Runnable {
    public static String TAG = "com.vivavu.dream.repository.task.CheckServerStatusTask";
    private final Handler handler;

    public CheckServerStatusTask(Handler handler) {
        this.handler = handler;
    }

    @Override
    public void run() {
        SocketAddress addr = new InetSocketAddress(Constants.url, Integer.parseInt(Constants.port));
        Socket socket = new Socket();
        int timeoutMs = 3000;
        boolean reachable = false;
        try {
            socket.connect(addr, timeoutMs);
            reachable = true;
        } catch (IOException e) {
            Log.v(TAG, e.getMessage());
        } finally {
            Message message = handler.obtainMessage( SERVER_IS_REACHABLE.ordinal(), reachable);
            handler.sendMessage(message);
        }
    }
}
