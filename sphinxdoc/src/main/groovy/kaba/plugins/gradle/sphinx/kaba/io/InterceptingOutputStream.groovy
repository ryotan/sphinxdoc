package kaba.plugins.gradle.sphinx.kaba.io

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
     * When some bytes are sent to this stream, {@ #callback} is called with that bytes.
     * <p/>
     * Then, if {@ #callback} returns {@code true}, that bytes will be sent to {@code out} too.
     *
     * @param out the underlying output stream to send bytes to.
     * @param callback accepts a byte to be sent to {@code out} and returns a Boolean.
     *                 If the return value is true, output will be sent to out, otherwise it will not.
     */
    InterceptingOutputStream(final OutputStream out, final Closure<Boolean> callback) {
        super(out)
        this.callback = callback
    }

    @Override
    void write(int b) throws IOException {
        super.write(b)
    }

    @Override
    void write(byte[] b) throws IOException {
        super.write(b)
    }

    @Override
    void write(byte[] b, int off, int len) throws IOException {
        super.write(b, off, len)
    }
}
