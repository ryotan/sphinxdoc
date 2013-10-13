package kaba.io

/**
 * Intercepts output to underlying output stream and callbacks {@link Closure}.
 *
 * @author Ryo TANAKA
 * @since 1.0
 */
class InterceptingOutputStream extends FilterOutputStream {

    private final Closure<Boolean> callback

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
    InterceptingOutputStream(final OutputStream out, final Closure<Boolean> callback) {
        super(out)
        this.callback = callback
    }

    /**
     * Calls {@link #callback} with {@code b} as an array, and when {@link #callback} returns {@code true},
     * send {@code b} to underlying output stream.
     *
     * @param     b  the <code>byte</code>.
     * @exception IOException  if an I/O error occurs.
     * @see       FilterOutputStream#write(int)
     */
    @Override
    void write(int b) throws IOException {
        if (callback.call([b] as byte[])) {
            out.write(b)
        }
    }

    /**
     * Calls {@link #callback} with {@code b}, and when {@link #callback} returns {@code true},
     * send {@code b} to underlying output stream.
     *
     * @param     b  the data to be written.
     * @exception IOException  if an I/O error occurs.
     * @see       FilterOutputStream#write(byte [])
     */
    @Override
    void write(byte[] b) throws IOException {
        if (callback.call(b)) {
            out.write(b)
        }
    }

    /**
     * Calls {@link #callback} with the subarray of {@code b},
     * and when {@link #callback} returns {@code true},
     * send {@code b} to underlying output stream.
     * <p/>
     * The subarray of {@code b} is <code>len</code> bytes from the specified
     * <code>byte</code> array starting at offset <code>off</code> to
     * this output stream.
     *
     * @param      b     the data.
     * @param      off   the start offset in the data.
     * @param      len   the number of bytes to write.
     * @exception  IOException  if an I/O error occurs.
     * @see        FilterOutputStream#write(byte[], int, int)
     */
    @Override
    void write(byte[] b, int off, int len) throws IOException {
        if ((off | len | b.length - (off + len)) < 0) {
            throw new IndexOutOfBoundsException("Specified range is out of bounds. length:${b.length} off:${off} len:${len}")
        }
        if (callback.call(b[off..(off + len - 1)] as byte[])) {
            out.write(b, off, len)
        }
    }
}
