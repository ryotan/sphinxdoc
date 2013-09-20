package kaba.plugins.gradle.sphinx

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Sphinxドキュメントのビルドをサポートするプラグイン。
 * <p/>
 * 以下のタスクと規約オブジェクトを追加する。
 *
 * <dl>
 *     <dt>タスク(<code>sphinxdoc</code>)</dt>
 *     <dd>
 *         下記の規約プロパティの<code>executable</code>に指定されたプログラムを、
 *         規約プロパティに指定された引数で呼び出して、Sphinxドキュメントをビルドする。
 *     </dd>
 * </dl>
 * <dl>
 *     <dt>規約プロパティ</dt>
 *     <dd>
 *         <table border="1">
 *             <tr align="left">
 *                 <th>プロパティ</th>
 *                 <th>設定する値</th>
 *                 <th>デフォルト値</th>
 *                 <th>対応するsphinx-build引数</th>
 *             </tr>
 *             <tr>
 *                 <td><code>executable</code></td>
 *                 <td>Sphinxのビルド実行ファイル</td>
 *                 <td><code>sphinx-build</code></td>
 *                 <td>-</td>
 *             </tr>
 *             <tr>
 *                 <td><code>builder</code></td>
 *                 <td>ビルド形式</td>
 *                 <td><code>html</code></td>
 *                 <td><code>-b ${builder}</code></td>
 *             </tr>
 *             <tr>
 *                 <td><code>sourcedir</code></td>
 *                 <td>ソースディレクトリ（projectルートからの相対パス）</td>
 *                 <td><code>source/</code></td>
 *                 <td><code>sourcedir</code>（オプションを除く第一引数）</td>
 *             </tr>
 *             <tr>
 *                 <td><code>outdir</code></td>
 *                 <td>出力先ディレクトリ（projectルートからの相対パス）</td>
 *                 <td><code>build/docs/sphinx/${builder}</code></td>
 *                 <td><code>outdir（オプションを除く第二引数）</code></td>
 *             </tr>
 *             <tr>
 *                 <td><code>settings</code></td>
 *                 <td>Sphinx設定ファイル(<code>conf.py</code>)の設定を上書きする値</td>
 *                 <td><code>null</code></td>
 *                 <td><code>-D ${key}=${value}</code></td>
 *             </tr>
 *         </table>
 *     </dd>
 * </dl>
 *
 * 規約プロパティは以下のように、<code>sphinxdoc</code>コードブロックを利用して設定することができる。
 * <pre>
 * sphinxdoc {
 *     sourcedir = '.'
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
     * プロジェクトのextensionsオブジェクトに追加する規約オブジェクトの名称
     */
    private static final String EXTENSION_NAME = 'sphinxdoc'

    /**
     * プロジェクトに追加された規約オブジェクト
     */
    SphinxdocConvention convention

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
        project.plugins.add(this)
        convention = createSphinxdocExtension(project)
        task = createSphinxdocTask(project)
    }

    /**
     * 適用されるプロジェクトで、<code>sphinxdoc</code>コードブロックを利用してSphinxビルドの設定をできるように、
     * プロジェクトのextensionオブジェクトにSphinxビルドの規約オブジェクトを追加する。
     *
     * @param project プラグインを適用するプロジェクト
     */
    private static SphinxdocConvention createSphinxdocExtension(Project project) {
        project.extensions.create(EXTENSION_NAME, SphinxdocConvention)
    }

    /**
     * 適用されるプロジェクトに、<code>sphinxdoc</code>タスクを追加する。
     * <p/>
     * sphinxdocタスクで利用するSphinxビルド設定（sphinx-buildコマンドの引数）は、
     * sphinxプラグインの提供する規約プロパティから取得する。
     * <p/>
     * ただし、プラグインの適用後に<code>sphinxdoc</code>コードブロックで
     * 規約プロパティを設定可能とするために、プロジェクトの設定フェーズの終了時に
     * タスクのビルド設定に規約プロパティを設定する。
     *
     * @param project プラグインを適用するプロジェクト
     */
    private Sphinxdoc createSphinxdocTask(Project project) {
        Sphinxdoc task = project.task(
            type: Sphinxdoc,
            group: 'Documentation',
            description: 'Builds Sphinx documentation.',
            TASK_NAME
        ) as Sphinxdoc
        project.afterEvaluate {Project pj ->
            task.configure {
                // このブロック内で対象オブジェクトを指定しないで'convention'にアクセスすると、
                // デフォルトではTask#conventionを参照してしまう。
                // そのため、conventionをthis参照する必要がある。（変数名は変えたくないなぁ。）
                executable = this.convention.executable
                root = pj.file(this.convention.root)
                builder = this.convention.builder
                sourcedir = pj.file(this.convention.sourcedir)
                outdir = pj.file(this.convention.outdir)
                settings = this.convention.settings
            }
        }
        task
    }
}
