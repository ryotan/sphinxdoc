package kaba.io

import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

import java.nio.charset.Charset

/**
 * {@link FileDumpOutputStream} のSpecクラス
 *
 * @author Ryo TANAKA
 * @since 1.0
 */
class FileDumpOutputStreamSpec extends Specification {

    @Rule
    TemporaryFolder tempFolder = new TemporaryFolder()

    def "ファイルに追記され、移譲先のOutputStreamにも出力されること"() {
        given:
        OutputStream under = new ByteArrayOutputStream()
        File file = tempFolder.newFile()
        FileDumpOutputStream sut = new FileDumpOutputStream(under, file)

        file.write("initial text.")
        assert file.bytes == "initial text.".bytes

        String out = """出力する日本語テキスト
            改行してみる。
        """
        byte[] bytes = out.getBytes(Charset.forName("MS932"))

        when:
        sut.write(bytes)

        then:
        under.toByteArray() == bytes
        file.bytes == ("initial text." + out).getBytes(Charset.forName("MS932"))
    }
}
