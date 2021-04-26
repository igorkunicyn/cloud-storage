package server.service.impl.Command;


import server.service.CommandService;
import server.service.impl.NettyServerService;
import service.Command;

public class AuthClientCommand implements CommandService {

    private String nickname;

    public AuthClientCommand() {
    }

    @Override
    public String processCommand(String str) {
        String[] token = str.split("\\s");
        String newNick = NettyServerService.getInstance().getAuthService()
          .getNicknameByLoginAndPassword(token[1], token[2]);
        if (newNick != null) {
            nickname = newNick;
            System.out.println("client " + nickname + " connected ");
            return Command.AUTH_OK.getInstruction()+" " +nickname ;
        } else {
            return "Неверный логин / пароль";
        }
    }

    @Override
    public String getCommand() {
        return Command.AUTH.getInstruction();
    }

}
