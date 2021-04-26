package server.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import server.factory.Factory;
import service.FileUpload;


public class ServerNettyHandler extends ChannelInboundHandlerAdapter {

   @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {

       if (msg.getClass()== FileUpload.class){
// попадаем сюда при копировании или перемещении файлов с компьютера в хранилище
            System.out.println("Ok");
                FileUpload fileUpload = (FileUpload) msg;
                SQLHandler.addFiles(fileUpload.getPath().split("\\\\")[0],
                        fileUpload.getNameFile(),fileUpload.getPath(),fileUpload.getBytes());
        }else if (msg.getClass()== String.class){
            String s = (String) msg;
            if (s.startsWith("/")){
                ctx.writeAndFlush(Factory.getCommandDirectoryService().processCommand(s));
            }else {
                ctx.writeAndFlush(Factory.getCommandFileProcessable().commandProcessFile(s));
            }
        }
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.channel().close();
    }

}


