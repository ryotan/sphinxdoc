package kaba.io

import java.nio.ByteBuffer

/**
 * 指定されたキャプチャ先にバイト列をキャプチャした後、
 * {@link OutputStream} に処理を移譲する {@link FilterOutputStream}
 *
 * @author Ryo TANAKA
 * @since 1.0
 */
class CapturingOutputStream extends InterceptingOutputStream {

    /**
     * {@link ByteBuffer} にバイト列をキャプチャする、
     * {@link CapturingOutputStream} を生成する。
     *
     * @param out 処理を移譲する {@link OutputStream}
     * @param buf バイト列をキャプチャする {@link ByteBuffer}
     */
    CapturingOutputStream(OutputStream out, ByteBuffer buf) {
        super(out, { byte[] bytes ->
            buf.put(bytes)
            true
        })
    }
}
