package org.lovesosa.netty.simple;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.util.CharsetUtil;

import java.util.concurrent.TimeUnit;

/**
 * 自定义一个Handler，需要继承Netty规定好的某个HandlerAdapter
 * 这时我们自定义的Handler才能称为一个Handler
 * @author lovesosa
 */
public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    /**
     * 读取数据事件（这里我们可以读取客户端发送的消息）
     * @param ctx 上下文对象，含有管道pipeline，通道Channel，地址
     * @param msg   客户端发送的数据
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        // 处理一个非常耗时的任务 -> 异步执行 -> 提交到该Channel对应的 NioEventLoop
        // 的 taskQueue中

        // 当操作一个耗时任务时，还是会出现阻塞的情况
        // 解决方案1: 用户程序自定义的普通任务
        ctx.channel().eventLoop().execute(() -> {
            try {
                Thread.sleep(1000 * 10);
                ctx.writeAndFlush(Unpooled.copiedBuffer("Hello，客户端(*^_^*)",CharsetUtil.UTF_8));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        // 解决方案2: 用户自定义定时任务,该任务是提交到scheduleTaskQueue中
        ctx.channel().eventLoop().schedule(() -> {
            try {
                Thread.sleep(1000 * 10);
                ctx.writeAndFlush(Unpooled.copiedBuffer("Hello，客户端(*^_^*)",CharsetUtil.UTF_8));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, 5, TimeUnit.SECONDS);




        System.out.println("go on...");

//        System.out.println("服务端worker线程信息: " + Thread.currentThread().getName());
//        System.out.println("server ctx: " + ctx);
//        System.out.println("看channel和pipeline的关系:");
//        Channel channel = ctx.channel();
//        ChannelPipeline pipeline = ctx.pipeline(); // 本质是一个双向链表,出栈入栈问题
//        // 将msg转为ByteBuf (ByteBuf是Netty提供的)
//        ByteBuf byteBuf = (ByteBuf) msg;
//        System.out.println("客户端发送的消息是: " + byteBuf.toString(CharsetUtil.UTF_8));
//        System.out.println("客户端地址是: " + ctx.channel().remoteAddress());


    }


    // 数据读取完毕,回复客户端
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        // 将数据写入到缓存，并刷新
        // 一般需要对进行发送的数据进行编码
        ctx.writeAndFlush(Unpooled.copiedBuffer("Hello，客户端(*^_^*)",CharsetUtil.UTF_8));
    }

    // 处理异常，关闭通道
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
