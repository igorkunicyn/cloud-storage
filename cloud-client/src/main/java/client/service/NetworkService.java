package client.service;

public interface NetworkService {

        void sendCommand(Object o);
        Object readCommandResult();
        void closeConnection();
}
