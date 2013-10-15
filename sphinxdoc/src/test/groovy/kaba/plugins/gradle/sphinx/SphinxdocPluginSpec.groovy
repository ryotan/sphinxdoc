package kaba.plugins.gradle.sphinx

import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification
import spock.lang.Unroll
/**
 * {@link SphinxdocPlugin} のSpecクラス。
 *
 * @author Ryo TANAKA
 * @since 1.0
 */
class SphinxdocPluginSpec extends Specification {

    /**
     * プラグイン名
     */
    private static final String PLUGIN_NAME = 'sphinxdoc'

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
        source: 'source/',
        out: 'build/docs/sphinxdoc/html/',
        root: 'source/',
        settings: [:]
    ].asImmutable()

    /**
     * テストフィクスチャプロジェクト
     */
    Project project = ProjectBuilder.builder().build()

    def "sphinxdocというプラグインとしてプロジェクトで利用できること。"() {

        given: "プロジェクトにはsphinxdocプラグインが存在しない時に"
        assert !project.plugins.hasPlugin(PLUGIN_NAME)

        when: "sphinxdocプラグインをapplyすると"
        project.apply(plugin: PLUGIN_NAME)

        then: "sphinxdocプラグインが適用されること"
        project.plugins.findPlugin(PLUGIN_NAME) instanceof SphinxdocPlugin
    }

    def "sphinxdocコードブロックが存在しない場合、規約プロパティはすべてデフォルト値となっていること。"() {

        given: "プロジェクトにはsphinxdocプラグインが存在しない時に"
        assert !project.plugins.hasPlugin(PLUGIN_NAME)

        when: "sphinxdocプラグインをapplyする。"
        project.apply(plugin: PLUGIN_NAME)

        then: "規約プロパティはすべてデフォルト値となっていること。"
        d.each {key, value ->
            assert project.sphinxdoc."${key}" == value
        }
    }

    @Unroll
    def "sphinxdocコードブロックで、Sphinxビルドの規約プロパティを上書きできること。(#message)"() {

        given: "プロジェクトにsphinxdocプラグインをapplyして"
        project.apply(plugin: PLUGIN_NAME)

        when: "sphinxdocコードブロックで設定すると、"
        project.sphinxdoc {
            builder = conf.builder
        }

        then: "規約プロパティが上書きされること。"
        Sphinxdoc task = project.plugins.getPlugin(SphinxdocPlugin).task
        task.is project.sphinxdoc
        task.builder == conf.builder
        task.out == "build/docs/sphinxdoc/${conf.builder}/"

        where:
        message | conf
        'builderが設定されていてoutdirが設定されていない場合、outdirがbuilderから導出されること。' | [builder: 'epub']
    }

    def "複数のプロジェクトにapplyしても、それぞれのプロジェクトで独立した規約オブジェクトをプラグインで保持していること。"() {

        setup: "サブプロジェクト2にsphinxdocプラグインがapplyされていて、"
        Project sub1 = ProjectBuilder.builder().withParent(project).build()
        sub1.apply(plugin: PLUGIN_NAME)

        and: "かつ、サブプロジェクト2にsphinxdocプラグインをapplyされている場合、"
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
        Sphinxdoc task1 = sub1.plugins.findPlugin(SphinxdocPlugin).task
        task1.builder == 'sub1_builder'
        task1.out == 'build/docs/sphinxdoc/sub1_builder/'

        Sphinxdoc task2 = sub2.plugins.findPlugin(SphinxdocPlugin).task
        task2.builder == 'sub2_builder'
        task2.out == 'build/docs/sphinxdoc/sub2_builder/'
    }

    def "sphinxdocプラグインをapplyすると、そのプロジェクトにsphinxdocタスクが追加されること。"() {

        given: "プロジェクトにはsphinxdocプラグインが存在しない時に"
        assert !project.plugins.hasPlugin(PLUGIN_NAME)

        when: "sphinxdocプラグインをapplyすると、"
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

        given: "プロジェクトにはsphinxdocプラグインが存在しない時に"
        assert !sub.plugins.hasPlugin(PLUGIN_NAME)

        when: "sphinxdocプラグインをapplyし、"
        sub.apply(plugin: PLUGIN_NAME)

        and: "規約プロパティを設定すると、"
        sub.sphinxdoc {
            if (conf.executable) {
                executable = conf.executable
            }
            if (conf.builder) {
                builder = conf.builder
            }
            if (conf.source) {
                source= conf.source
            }
            if (conf.out) {
                out= conf.out
            }
            if (conf.root) {
                root = conf.root
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
        task.source == expected.source
        task.out == expected.out
        task.root == expected.root

        where: "タスクの設定が正しく行われ、設定値とコマンドライン引数の間の変換処理も行われることを確認する。"
        message |
            conf |
            expected
        'デフォルト値の場合' |
            [:] |
            d
        'すべて設定されている場合' |
            [executable: '/usr/bin/sphinx-build', builder: 'epub', source: '.', out: '/var/sphinxdoc/build', settings: [title: 'title'], root: '~/sphinxdoc'] |
            [executable: '/usr/bin/sphinx-build', builder: 'epub', source: '.', out: '/var/sphinxdoc/build', settings: [title: 'title'], root: '~/sphinxdoc']
    }
}
