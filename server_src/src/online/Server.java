package online;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.io.*;

// 发送消息的格式被规定为 Id:mode:[xx]

//系统id为0
//0：1：    xx 系统消息
//0：2：0   系统分配黑棋    
//0：2：1   系统分配白棋
//0：-1     对手退出
//0：%      通知客户刷新棋盘

//用户id为1
//1：1：xx 聊天
//1：2：x：y （x，y）处落子
//1: 0:0 复仇
//1: 0:1 同意复仇
//1: 0:2 不同意复仇

//特殊格式 
//= 换人
//& 不复仇
//@ 请求关机
//$ 请求联机

public class Server {

    private int port;

    public static Log log = new Log();

    public Server(int port) {
        this.port = port;
    }

    public void run() throws Exception {
        
        EventLoopGroup bossGroup = new NioEventLoopGroup(); // (1)
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap(); // (2)
            b.group(bossGroup, workerGroup)
             .channel(NioServerSocketChannel.class) // (3)
             .childHandler(new ServerInitializer())  //(4)
             .option(ChannelOption.SO_BACKLOG, 128)          // (5)
             .childOption(ChannelOption.SO_KEEPALIVE, true); // (6)
            
    		log.tolog("\n\nServer 启动了");
    		
            ChannelFuture f = b.bind(port).sync(); // (7)

            f.channel().closeFuture().sync();

        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
            
    		log.tolog("Server 关闭了");
        }
    }

    public static void main(String[] args) throws Exception {
        try{
            int port;
            port = getPort();
            new Server(port).run();
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    static int getPort() throws IOException {
        FileInputStream fis = new FileInputStream("server.conf");
        InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
        BufferedReader br = new BufferedReader(isr);
        String line = "";
        String arrs[] = null;
        int port = 0;

        if ((line = br.readLine()) != null) {
            arrs = line.split("=");
            port = Integer.parseInt(arrs[1]);
        } else {
            log.tolog("Read server.conf failed!");
        }

        br.close();
        isr.close();
        fis.close();
        return port;
    }
}