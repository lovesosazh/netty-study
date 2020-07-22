package org.lovesosa.netty.simple;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @author lovesosa
 */
public class NettyServer {
    public static void main(String[] args) throws Exception {

        // 1.创建两个线程组BossGroup 和 WorkerGroup
        // 2.BossGroup用来处理连接请求，WorkerGroup完成真正的业务处理
        // 3.两个都是无限循环
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup wrokerGroup = new NioEventLoopGroup();


        try {
            // 创建服务端启动的对象，配置参数
            ServerBootstrap bootstrap = new ServerBootstrap();
            // 使用链式编程进行配置
            bootstrap.group(bossGroup, wrokerGroup) //设置两个线程组
                    .channel(NioServerSocketChannel.class) // 使用NioSocketChannel作为服务器通道的实现
                    .option(ChannelOption.SO_BACKLOG, 128) // 设置线程队列等待连接个数
                    .childOption(ChannelOption.SO_KEEPALIVE, true) // 设置保持活动的连接状态
                    .childHandler(new ChannelInitializer<SocketChannel>() { // 创建一个通道初始化对象
                        // 给pipeLine设置处理器
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new NettyServerHandler());
                        }
                    }); // 给wrokerGroup的EventLoop的对应的管道设置处理器

            System.out.println("服务器已经准备好...");
            // 启动服务器，绑定一个端口并且同步，返回一个ChannelFuture对象
            ChannelFuture channelFuture = bootstrap.bind(6666).sync();
            // 对关闭通道进行监听
            channelFuture.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            wrokerGroup.shutdownGracefully();
        }


    }
}
