package online;

import java.util.*;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

public class ServerHandler extends SimpleChannelInboundHandler<String> { // (1)

	public static ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

	public static Map<Channel, Channel> map = new HashMap<Channel, Channel>();

	public static Log log = new Log();

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {  // (2)
		Channel incoming = ctx.channel();
		channels.add(incoming);
		map.put(incoming, null);
		log.tolog(incoming.remoteAddress() + " 加入");
		findPlayer(incoming);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {  // (3)
		Channel incoming = ctx.channel();
		
		Channel other = map.get(incoming);
		if(other != null) {
			other.writeAndFlush("0:-1\n");
			map.remove(incoming);
			map.put(other, null);
		}
	}
	

    @Override
	protected void channelRead0(ChannelHandlerContext ctx, String s) throws Exception { // (4)
		Channel incoming = ctx.channel();
		//System.out.println(s);

		Channel other = map.get(incoming);
		if(other != null){
			if("=".equals(s)) { //换人
				other.writeAndFlush("0:-1\n");
				map.put(incoming, null);
				map.put(other, null);
			} else if("$".equals(s)) { //请求联机
				findPlayer(incoming);
			} else if("&".equals(s)){ // 不复仇
				incoming.writeAndFlush("0:%\n");
				other.writeAndFlush("0:%\n");
				incoming.writeAndFlush("0:1:正在匹配新对手\n");
				other.writeAndFlush("0:1:正在匹配更强的对手\n");
				findPlayer(incoming);
				findPlayer(other);
			} else if("@".equals(s)) {
				map.put(other, null);
				other.writeAndFlush("0:-1\n");
				map.remove(incoming);
			} else {
				other.writeAndFlush(s + "\n");
			}
		}
	}
  
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception { // (5)
        Channel incoming = ctx.channel();
		log.tolog("Client:"+incoming.remoteAddress()+"在线");
	}
	
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception { // (6)
        Channel incoming = ctx.channel();
		log.tolog("Client:"+incoming.remoteAddress()+"掉线");
	}
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) { // (7)
    	Channel incoming = ctx.channel();
		log.tolog("Client:"+incoming.remoteAddress()+"异常");
        // 当出现异常就关闭连接
        cause.printStackTrace();
        ctx.close();
	}

	public void findPlayer(Channel incoming) {
		if(map.get(incoming) != null) return;

		incoming.writeAndFlush("0:1:正在寻找对手\n");
		for(Channel channel : channels) {
			if(channel != incoming && map.get(channel) == null) {
				channel.writeAndFlush("0:1:找到一个对手，开始游戏\n");
				incoming.writeAndFlush("0:1:找到一个对手，开始游戏\n");
				channel.writeAndFlush("0:2:0\n");
				incoming.writeAndFlush("0:2:1\n");
				map.put(channel, incoming);
				map.put(incoming, channel);
				break;
			}
		}
	}
}