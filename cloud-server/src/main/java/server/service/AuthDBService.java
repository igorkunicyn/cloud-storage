package server.service;

public interface AuthDBService {
    // выдает имя по логину и паролю
    String getNicknameByLoginAndPassword(String login, String password);
   // регистрирует по логину и паролю
    boolean registration(String login, String password, String nickname);
    // меняет имя
    boolean changeNick(String oldNickname, String newNickname);
}
