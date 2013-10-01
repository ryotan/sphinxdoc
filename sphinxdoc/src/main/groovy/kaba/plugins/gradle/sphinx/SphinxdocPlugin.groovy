package kaba.plugins.gradle.sphinx

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Sphinxドキュメントのビルドをサポートするプラグイン。
 * <p/>
 * <dl>
 *     <dt>タスク(<code>sphinxdoc</code>)</dt>
 *     <dd>
 *         追加されたタスクの詳細については、{@link Sphinxdoc}を参照。
 *     </dd>
 * </dl>
 * <dl>
 *     <dt>規約プロパティ</dt>
 *     <dd>規約プロパティの詳細については、{@link Sphinxdoc}を参照。</dd>
 * </dl>
 *
 * 規約プロパティは以下のように、<code>sphinxdoc</code>コードブロックを利用して設定することができる。
 * <pre>
 * sphinxdoc {
 *     exec = "${sphinx-exec}"
 *     source     = '.'
 *     settings   = [version: '1.3.0b']
 * }
 * </pre>
 * なお、ディレクトリ関連の設定については、絶対パス表記の場合はそのままの絶対パスとして解釈され、
 * 相対パス表記の場合は、プロジェクトルートからの相対パスとして解釈される。 ({@link Project#file(Object)} を参照。)
 *
 * @author Ryo TANAKA
 * @since 1.0
 */
class SphinxdocPlugin implements Plugin<Project> {

    /**
     * プロジェクトのextensionsオブジェクトに追加するタスクの名称
     */
    private static final String TASK_NAME = 'sphinxdoc'

    /**
     * プロジェクトに追加されたタスク
     */
    Sphinxdoc task

    @Override
    void apply(Project project) {
        task = createSphinxdocTask(project)
    }

    /**
     * 適用されるプロジェクトに、<code>sphinxdoc</code>タスクを追加する。
     * <p/>
     * sphinxdocタスクで利用するSphinxビルド設定（sphinx-buildコマンドの引数）は、
     * sphinxプラグインの提供する規約プロパティから取得する。
     * <p/>
     * ただし、プラグインの適用後に<code>sphinxdoc</code>コードブロックで
     * 規約プロパティを設定可能とするために、プロジェクトの設定フェーズの終了時に
     * タスクで実行するのコマンドラインの設定を行う。
     *
     * @param project プラグインを適用するプロジェクト
     */
    private static Sphinxdoc createSphinxdocTask(Project project) {
        Sphinxdoc task = project.task(
            type: Sphinxdoc,
            group: 'Documentation',
            description: 'Builds Sphinx documentation.',
            TASK_NAME
        ) as Sphinxdoc
        project.afterEvaluate {
            task.configure()
        }
        task
    }
}
