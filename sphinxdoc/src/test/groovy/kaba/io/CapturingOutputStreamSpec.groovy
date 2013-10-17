package kaba.io

import spock.lang.Specification

import java.nio.ByteBuffer

/**
 * {@link CapturingOutputStream} のSpecクラス
 *
 * @author Ryo TANAKA
 * @since 1.0
 */
class CapturingOutputStreamSpec extends Specification {

    def "指定したByteBufferにキャプチャし、移譲先のOutputStreamにも出力されること"() {
        given:
        OutputStream under = new ByteArrayOutputStream()
        byte[] actual = new byte[10]
        ByteBuffer buf = ByteBuffer.wrap(actual)
        CapturingOutputStream sut = new CapturingOutputStream(under, buf)

        byte[] expected = [0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09]

        when:
        sut.write(expected)

        then:
        under.toByteArray() == expected
        actual == expected
    }
}
