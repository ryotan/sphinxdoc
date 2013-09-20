package kaba.plugins.gradle.sphinx

import spock.lang.Specification
import spock.lang.Unroll

/**
 * {@link SphinxdocConvention} のSpecクラス
 *
 * @author Ryo TANAKA
 * @since 1.0
 */
class SphinxdocConventionSpec extends Specification {

    private static final d = [
        executable: 'sphinx-build',
        builder: 'html',
        sourcedir: 'source/',
        outdirRoot: 'build/docs/sphinxdoc/',
        settings: null
    ]

    @Unroll
    def "#confと設定されているSphinxdocConventionの各プロパティの値は、設定値通りになっていること。(#message)"() {
        when:
        SphinxdocConvention sut = new SphinxdocConvention(conf);

        then:
        sut.executable == (executable ?: d.executable)
        sut.builder == builder
        sut.sourcedir == sourcedir
        sut.outdir == outdir
        sut.settings == settings

        where:
        message |
          conf |
            executable    | builder   | sourcedir   | outdir                         | settings

        '設定されていない場合は、すべての値がデフォルト値であること。' |
          [:] |
            d.executable  | d.builder | d.sourcedir | "${d.outdirRoot}${d.builder}/" | null

        '全ての値が設定されている場合、すべての設定値が設定された値になること。' |
          [executable: '/bin/sphinx', builder: 'epub', sourcedir: '.', outdir: '_build/', settings: [title: "title"]] |
            '/bin/sphinx' | 'epub'    | '.'         | '_build/'                      | [title: "title"]

        'builderが設定されていてoutdirが設定されていない場合、outdirがbuilderから導出されること。' |
          [builder: 'epub'] |
            d.executable  | 'epub'    | d.sourcedir | "${d.outdirRoot}epub/"         | null
    }
}
