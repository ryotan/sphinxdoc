package kaba.plugins.gradle.sphinx

import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification
import spock.lang.Unroll
/**
 * {@link SphinxPlugin} のSpecクラス。
 *
 * @author Ryo TANAKA
 * @since 1.0
 */
class SphinxPluginSpec extends Specification {

    /**
     * プラグイン名
     */
    private static final String PLUGIN_NAME = 'sphinx'

    /**
     * extensionオブジェクト名
     */
    private static final String EXTENSION_NAME = 'sphinxdoc'

    /**
     * extensionオブジェクト名
     */
    private static final String TASK_NAME = 'sphinxdoc'

    /**
     * 規約プロパティのデフォルト値
     */
    private static final d = [
        executable: 'sphinx-build',
        builder: 'html',
        sourcedir: 'source/',
        outdir: 'build/docs/sphinxdoc/html/',
        settings: null
    ].asImmutable()

    /**
     * テストフィクスチャプロジェクト
     */
    Project project = ProjectBuilder.builder().build()

    def "sphinxというプラグインとしてプロジェクトで利用できること。"() {

        given: "プロジェクトにはsphinxプラグインが存在しない時に"
        assert !project.plugins.hasPlugin(PLUGIN_NAME)

        when: "sphinxプラグインをapplyすると"
        project.apply(plugin: PLUGIN_NAME)

        then: "sphinxプラグインが適用され、"
        project.plugins.findPlugin(PLUGIN_NAME) instanceof SphinxPlugin

        and: "規約オブジェクトがextensionsオブジェクトに追加されていること"
        project.extensions.findByName(EXTENSION_NAME) instanceof SphinxdocConvention
    }

    def "sphinxdocコードブロックが存在しない場合、規約プロパティはすべてデフォルト値となっていること"() {

        given: "プロジェクトにはsphinxプラグインが存在しない時に"
        assert !project.plugins.hasPlugin(PLUGIN_NAME)

        when: "sphinxプラグインをapplyする。"
        project.apply(plugin: PLUGIN_NAME)

        then: "規約プロパティはすべてデフォルト値となっていること。"
        d.each {key, value ->
            assert project.sphinxdoc."${key}" == value
        }
    }

    @Unroll
    def "sphinxdocコードブロックで、Sphinxビルドの規約プロパティを上書きできること。(#message)"() {

        given: "プロジェクトにsphinxプラグインをapplyして"
        project.apply(plugin: PLUGIN_NAME)

        when: "sphinxdocコードブロックで設定すると、"
        project.sphinxdoc {
            builder = conf.builder
        }

        then: "規約プロパティはすべて設定値となっていること。"
        SphinxdocConvention convention = project.plugins.getPlugin(SphinxPlugin).convention
        convention.is project.sphinxdoc
        convention.builder == conf.builder
        convention.outdir == "build/docs/sphinxdoc/${conf.builder}/"

        where:
        message | conf
        'builderが設定されていてoutdirが設定されていない場合、outdirがbuilderから導出されること。' | [builder: 'epub']
    }

    def "複数のプロジェクトにapplyしても、それぞれのプロジェクトで独立した規約オブエジェクトをプラグインで保持していること。"() {

        setup: "サブプロジェクト1にsphinxプラグインがapplyされていて、"
        Project sub1 = ProjectBuilder.builder().withParent(project).build()
        sub1.apply(plugin: PLUGIN_NAME)

        and: "かつ、サブプロジェクト2にsphinxプラグインをapplyされている場合、"
        Project sub2 = ProjectBuilder.builder().withParent(project).build()
        sub2.apply(plugin: PLUGIN_NAME)

        when: "サブプロジェクト1にsphinxdocコードブロックで設定し、"
        sub1.sphinxdoc {
            builder = 'sub1_builder'
        }

        and: "サブプロジェクト2にもsphinxdocコードブロックで設定しても、"
        sub2.sphinxdoc {
            builder = 'sub2_builder'
        }

        then: "プラグインで保持する規約プロパティの値はそれぞれのプロジェクトで設定された値となっていること。"
        SphinxdocConvention convention1 = sub1.plugins.findPlugin(SphinxPlugin).convention
        convention1.builder == 'sub1_builder'

        SphinxdocConvention convention2 = sub2.plugins.findPlugin(SphinxPlugin).convention
        convention2.builder == 'sub2_builder'
    }

    def "sphinxプラグインをapplyすると、そのプロジェクトにsphinxdocタスクが追加されること。"() {

        given: "プロジェクトにはsphinxプラグインが存在しない時に"
        assert !project.plugins.hasPlugin(PLUGIN_NAME)

        when: "sphinxプラグインをapplyすると、"
        project.apply(plugin: PLUGIN_NAME)

        then: "sphinxdocタスクが追加されていること。"
        Task task = project.tasks.findByName(TASK_NAME)
        task instanceof Sphinxdoc
        task.description == 'Builds Sphinx documentation.'
        task.group == 'Documentation'
    }

    @Unroll
    def "sphinxdocタスクは、規約プロパティに設定された値をビルド設定値として保持すること。(#message)"() {

        // サブプロジェクトの場合でも、ディレクトリ設定値が正しくサブプロジェクトのルートディレクトリからの
        // 相対パスとして解釈されることを検証するために、サブプロジェクトを利用して振舞いを検証する。
        Project sub = ProjectBuilder.builder().withParent(project).build()

        given: "プロジェクトにはsphinxプラグインが存在しない時に"
        assert !sub.plugins.hasPlugin(PLUGIN_NAME)

        when: "sphinxプラグインをapplyし、"
        sub.apply(plugin: PLUGIN_NAME)

        and: "規約プロパティを設定すると、"
        sub.sphinxdoc {
            if (conf.executable) {
                executable = conf.executable
            }
            if (conf.builder) {
                builder = conf.builder
            }
            if (conf.sourcedir) {
                sourcedir = conf.sourcedir
            }
            if (conf.outdir) {
                outdir = conf.outdir
            }
            if (conf.settings) {
                settings = conf.settings
            }
        }
        // 非公開APIだけど、これ呼ばないとafterEvaluateが呼ばれないから。。。
        sub.evaluate()

        then: "sphinxdocタスクの設定値は、設定された値となること。"
        Sphinxdoc task = sub.tasks.findByName(TASK_NAME) as Sphinxdoc
        task.executable == expected.executable
        task.builder == expected.builder
        task.settings == expected.settings

        and: "ただし、ディレクトリは絶対パスまたはプロジェクトルートからの相対パスとして解釈されること。"
        task.sourcedir == sub.file(expected.sourcedir)
        task.outdir == sub.file(expected.outdir)

        where:
        message |
            conf |
            expected
        'デフォルト値の場合' |
            [:] |
            d
        'すべて設定されている場合（絶対パス指定のディレクトリを含む）' |
            [executable: '/usr/bin/sphinx-build', builder: 'epub', sourcedir: '.', outdir: '/var/sphinxdoc/build', settings: [title: 'title']] |
            [executable: '/usr/bin/sphinx-build', builder: 'epub', sourcedir: '.', outdir: '/var/sphinxdoc/build', settings: [title: 'title']]
        'builderが設定されていてoutdirが設定されていない場合、outdirがbuilderから導出されること。' |
            [builder: 'epub'] |
            [executable: d.executable, builder: 'epub', sourcedir: d.sourcedir, outdir: 'build/docs/sphinxdoc/epub', settings: d.settings]
    }
}
