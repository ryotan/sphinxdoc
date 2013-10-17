package kaba.io

/**
 * ファイル出力を行った後、 {@link OutputStream} に処理を移譲する {@link FilterOutputStream}
 *
 * @author Ryo TANAKA
 * @since 1.0
 */
class FileDumpOutputStream extends InterceptingOutputStream {

    /**
     * Creates an output stream filter built on top of the specified
     * underlying output stream.
     * <p/>
     * When some bytes are sent to this stream, {@link #callback} is called with that bytes.
     * <p/>
     * Then, if {@link #callback} returns {@code true}, that bytes will be sent to {@code out} too.
     *
     * @param out the underlying output stream to send bytes to.
     * @param callback accepts a byte to be sent to {@code out} and returns a Boolean.
     *                 If the return value is true, output will be sent to out, otherwise it will not.
     */
    FileDumpOutputStream(OutputStream out, File file) {
        super(out, { byte[] bytes ->
            file.append(bytes)
            true
        })
    }
}
