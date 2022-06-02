package Netty;


import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ServerHandler extends SimpleChannelInboundHandler<String> {

    /**
     * Способ получения и обработки сообщений
     */
    @Override
    protected void messageReceived(ChannelHandlerContext ctx, String msg) throws Exception {

        System.out.println ("Сервер получил сообщение от" + ctx.channel (). remoteAddress () + ":" + msg);
        ctx.channel (). writeAndFlush ("привет служба получила ваш IP =" + ctx.channel (). remoteAddress () + "сообщение отправлено");
        ctx.writeAndFlush ("привет, служба получила сообщение, которое вы отправили");
    }


    /**
     * Когда новый клиент подключается к серверу
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println ("channelActive ....... Новый клиент подключается к серверу");
    }

    /**
     * Когда клиент отключается от сервера
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println ("channelInactive ........ клиент отключается от сервера");
    }

    /**
     * Аномалии :)
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
    }


}