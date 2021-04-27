package server.service.impl;

import server.service.AuthDBService;
import server.handler.SQLHandler;

public class AuthDBServise implements AuthDBService {
    @Override
    public String getNicknameByLoginAndPassword(String login, String password) {
        return SQLHandler.getNicknameByLoginAndPassword(login, password);
    }

    @Override
    public boolean registration(String login, String password, String nickname) {
        return SQLHandler.registration(login, password, nickname);
    }

        @Override
    public boolean changeNick(String oldNickname, String newNickname) {
        return SQLHandler.changeNick(oldNickname, newNickname);
    }
}
