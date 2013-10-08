package kaba.plugins.gradle.sphinx

import org.gradle.testfixtures.ProjectBuilder
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
        sut.configure() == [
            d.exec,
            '-c', sut.project.file(d.source).path,
            '-b', d.builder,
            sut.project.file(d.source).path,
            sut.project.file(d.out).path
        ]
    }

    def "全ての値を設定してconfigureを実行すると、設定した値でコマンドラインが構築されること。"() {
        given:
        sut.with {
            exec = '/usr/local/bin/sphinx-build'
            builder = 'epub'
            source = '.'
            out = '_build'
            root = '..'
            settings = [version: '1.0', revision: '1.0.0']
        }

        when:
        def pj = sut.project
        def cmd = sut.configure()

        then:
        cmd == [
            '/usr/local/bin/sphinx-build',
            '-c', pj.file('..').path,
            '-b', 'epub',
            '-D', 'version=1.0', '-D', 'revision=1.0.0',
            pj.file('.').path,
            pj.file('_build').path]
    }
}
