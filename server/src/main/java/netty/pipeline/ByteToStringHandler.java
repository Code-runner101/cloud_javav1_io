package netty.pipeline;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ByteToStringHandler {

//    @Override
//    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//
//        ByteBuf buf = (ByteBuf) msg;
//        StringBuilder s = new StringBuilder();
//
//        while (buf.isReadable()) {
//            s.append((char) buf.readByte());
//        }
//
//        ctx.fireChannelRead(s.toString());
//    }

}
