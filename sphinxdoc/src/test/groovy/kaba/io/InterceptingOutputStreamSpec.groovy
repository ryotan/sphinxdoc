package kaba.io

import spock.lang.Specification
import spock.lang.Unroll

/**
 * Spec class for {@link InterceptingOutputStream}.
 *
 * @author Ryo TANAKA
 * @since 1.0
 */
class InterceptingOutputStreamSpec extends Specification {

    /**
     * underlying output stream
     */
    def under = new ByteArrayOutputStream()

    /**
     * captured bytes
     */
    def capture

    /**
     * callback closure to send bytes to underlying output stream
     */
    def sendToUnder = { byte[] b ->
        capture = b
        true
    }

    /**
     * callback closure not to send bytes to underlying output stream
     */
    def notSendToUnder = { byte[] b ->
        capture = b
        false
    }

    def "When a byte is sent to this stream, callback closure is called."() {
        given:
        def sut = new InterceptingOutputStream(under, sendToUnder)
        byte b = 0x00

        when:
        sut.write(b)

        then:
        capture == [b]
    }

    def "When a byte array is sent to this stream, callback closure is called."() {
        given:
        def sut = new InterceptingOutputStream(under, sendToUnder)
        byte[] bytes = [0x00, 0x01] as byte[]

        when:
        sut.write(bytes)

        then:
        capture == bytes
    }

    @Unroll
    def "When a byte array is sent to this stream and range is specified, callback closure is called with the subarray. case: #message"() {
        given:
        def sut = new InterceptingOutputStream(under, sendToUnder)

        when:
        sut.write(bytes, off, len)

        then:
        capture == expected

        where:
        message           | bytes                              | off | len | expected
        "head one byte"   | [0x00, 0x01] as byte[]             | 0   | 1   | [0x00]
        "tail one byte"   | [0x00, 0x01] as byte[]             | 1   | 1   | [0x01]
        "head multi byte" | [0x00, 0x01, 0x02] as byte[]       | 0   | 2   | [0x00, 0x01]
        "tail multi byte" | [0x00, 0x01, 0x02] as byte[]       | 1   | 2   | [0x01, 0x02]
        "mid one byte"    | [0x00, 0x01, 0x02] as byte[]       | 1   | 1   | [0x01]
        "mid multi byte"  | [0x00, 0x01, 0x02, 0x03] as byte[] | 1   | 2   | [0x01, 0x02]
        "full array"      | [0x00, 0x01, 0x02, 0x03] as byte[] | 0   | 4   | [0x00, 0x01, 0x02, 0x03]
    }

    def "If specified range is out of bounds, IndexOutOfBounds exception occurs. case: #message"() {
        given:
        def sut = new InterceptingOutputStream(under, sendToUnder)

        when:
        sut.write(bytes, off, len)

        then:
        def e = thrown(IndexOutOfBoundsException)
        e.message == "Specified range is out of bounds. length:${bytes.length} off:${off} len:${len}"
        capture == null

        where:
        message           | bytes                        | off | len
        "off is negative" | [0x00, 0x01] as byte[]       | -1  | 1
        "len is negative" | [0x00, 0x01] as byte[]       | 0   | -1
        "too long range"  | [0x00, 0x01, 0x02] as byte[] | 1   | 3
    }

    def "If callback closure returns true, sent byte should be sent to underlying output stream too."() {
        given:
        def sut = new InterceptingOutputStream(under, sendToUnder)

        when:
        sut.write(b)

        then:
        capture == expected
        under.toByteArray() == expected

        where:
        message | b    | expected
        "byte"  | 0x00 | [0x00]
    }

    def "If callback closure returns true, sent bytes should be sent to underlying output stream too."() {
        given:
        def sut = new InterceptingOutputStream(under, sendToUnder)

        when:
        sut.write(bytes)

        then:
        capture == expected
        under.toByteArray() == expected

        where:
        message      | bytes                        | expected
        "byte array" | [0x00, 0x01, 0x02] as byte[] | [0x00, 0x01, 0x02]
    }

    def "If callback closure returns true, sent bytes and off, len should be sent to underlying output stream too."() {
        given:
        def sut = new InterceptingOutputStream(under, sendToUnder)

        when:
        sut.write(bytes, off, len)

        then:
        capture == expected
        under.toByteArray() == expected

        where:
        message                | bytes                        | off | len | expected
        "byte array and range" | [0x00, 0x01, 0x02] as byte[] | 1   | 1   | [0x01]
    }

    def "If callback closure returns false, nothing is sent to underlying stream (a byte)."() {
        given:
        def sut = new InterceptingOutputStream(under, notSendToUnder)

        when:
        sut.write(b)

        then:
        capture == expected
        under.toByteArray() == []

        where:
        message | b    | expected
        "byte"  | 0x00 | [0x00]
    }

    def "If callback closure returns false, nothing is sent to underlying stream (byte array)."() {
        given:
        def sut = new InterceptingOutputStream(under, notSendToUnder)

        when:
        sut.write(bytes)

        then:
        capture == expected
        under.toByteArray() == []

        where:
        message      | bytes                        | expected
        "byte array" | [0x00, 0x01, 0x02] as byte[] | [0x00, 0x01, 0x02]
    }

    def "If callback closure returns false, nothing is sent to underlying stream (byte array and range)."() {
        given:
        def sut = new InterceptingOutputStream(under, notSendToUnder)

        when:
        sut.write(bytes, off, len)

        then:
        capture == expected
        under.toByteArray() == []

        where:
        message                | bytes                        | off | len | expected
        "byte array and range" | [0x00, 0x01, 0x02] as byte[] | 1   | 1   | [0x01]
    }
}
