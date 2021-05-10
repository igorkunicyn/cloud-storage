package client.service.impl;

import client.service.NetworkService;
import io.netty.handler.codec.serialization.*;
import java.io.*;
import java.net.Socket;

public class IONetworkService implements NetworkService {

    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 8189;
    private static IONetworkService instance;
    private static Socket socket;
    private static ObjectDecoderInputStream in;
    private static ObjectEncoderOutputStream out;

    private IONetworkService() {
    }

    public static IONetworkService getInstance() {
        if (instance == null) {
            instance = new IONetworkService();
            initializeSocket();
            initializeIOStreams();
        }
        return instance;
    }

    private static void initializeSocket() {
        try {
            socket = new Socket(SERVER_HOST, SERVER_PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static void initializeIOStreams() {
        try {
            out = new ObjectEncoderOutputStream(socket.getOutputStream());
            in = new ObjectDecoderInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendCommand(Object o) {
        try {
            System.out.println(o.toString()+" sendCommand");
            out.writeObject(o);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public Object readCommandResult() {
        try {
            return  in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Read command result exception: " + e.getMessage());
        }
    }
    @Override
    public void closeConnection() {
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

