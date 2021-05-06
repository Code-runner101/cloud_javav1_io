package netty.pipeline;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;

@Slf4j
public class StringToByteHandler {

//    @Override
//    public void write(ChannelHandlerContext ctx,
//                      Object msg,
//                      ChannelPromise promise) throws Exception {
//
//        String s = (String) msg;
//        log.debug("received: {}", s);
//        ByteBuf buf = ctx.alloc().directBuffer();
//        buf.writeCharSequence(s, StandardCharsets.UTF_8);
//        buf.retain();
//
//        ctx.writeAndFlush(buf);
//    }
}
