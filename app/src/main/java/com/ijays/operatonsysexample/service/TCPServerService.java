package com.ijays.operatonsysexample.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by ijaysdev on 16/5/18.
 */
public class TCPServerService extends Service {
    private boolean mIsServiceDestroyed = false;
    private String mContent;

    @Override
    public void onCreate() {
        new Thread(new TcpServer()).start();
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder();
    }

    @Override
    public void onDestroy() {
        mIsServiceDestroyed = true;
        super.onDestroy();
    }

    private class TcpServer implements Runnable {

        @Override
        public void run() {
            ServerSocket serverSocket = null;
            try {
                serverSocket = new ServerSocket(9000);
            } catch (IOException e) {
                System.out.println("establish tcp server failed");
                e.printStackTrace();
                return;
            }
            while (!mIsServiceDestroyed) {
                try {
                    final Socket client = serverSocket.accept();
                    new Thread() {
                        @Override
                        public void run() {
                            Log.e("SONGJIE", "connection");
                            responseClient(client);
                        }
                    }.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void responseClient(Socket client) {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            PrintWriter out = new PrintWriter(new BufferedWriter(
                    new OutputStreamWriter(client.getOutputStream())), true);
            out.println(mContent);
            while (!mIsServiceDestroyed) {
                String str = in.readLine();
                if (str == null) {
                    break;
                }
                out.println("fdfdfdfsf");
            }
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
            if (client != null) {
                client.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public class MyBinder extends Binder {
        public TCPServerService getService() {
            return TCPServerService.this;
        }
    }

    public void setContent(String mContent) {
        this.mContent = mContent;
    }
}
