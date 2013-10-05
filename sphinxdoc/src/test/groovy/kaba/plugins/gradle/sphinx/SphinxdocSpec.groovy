package kaba.plugins.gradle.sphinx

import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Shared
import spock.lang.Specification

/**
 * {@link Sphinxdoc}のSpecクラス
 *
 * @author Ryo TANAKA
 * @since 1.0
 */
class SphinxdocSpec extends Specification {

    /**
     * テスト対象
     */
    @Shared
    Sphinxdoc sut = ProjectBuilder.builder().build().task(type: Sphinxdoc, "sut") as Sphinxdoc

    /**
     * 規約プロパティのデフォルト値
     */
    private static final d = [
            exec: 'sphinx-build',
            builder: 'html',
            source: 'source/',
            out: 'build/docs/sphinxdoc/html/',
            root: '.',
            settings: [:]
    ].asImmutable()

    def "何も設定せずにconfigureを実行すると、デフォルト値でコマンドラインが構築されること。"() {
        expect:
        sut.configure() == [ d.exec, '-b', d.builder, sut.project.file(d.source).path, sut.project.file(d.out).path ]
    }
}
