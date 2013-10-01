package kaba.plugins.gradle.sphinx
import org.gradle.api.Project
import org.gradle.api.tasks.Exec
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory

/**
 * Sphinxビルドを実行するタスク。
 *
 * Sphinxドキュメントのビルド時に設定可能な規約プロパティとして、以下を提供する。（括弧内はデフォルト値）
 * <table border="1">
 *     <tr align="left">
 *         <th>プロパティ</th>
 *         <th>設定する値</th>
 *         <th>デフォルト値</th>
 *         <th>対応するsphinx-build引数</th>
 *     </tr>
 *     <tr>
 *         <td><code>exec</code></td>
 *         <td>Sphinxのビルド実行ファイル</td>
 *         <td><code>sphinx-build</code></td>
 *         <td>-</td>
 *     </tr>
 *     <tr>
 *         <td><code>root</code></td>
 *         <td>Sphinxドキュメントのルートディレクトリ（conf.pyのあるディレクトリ。projectルートからの相対パス）</td>
 *         <td><code>.</code></td>
 *         <td>-c ${root}</td>
 *     </tr>
 *     <tr>
 *         <td><code>builder</code></td>
 *         <td>ビルド形式</td>
 *         <td><code>html</code></td>
 *         <td><code>-b ${builder}</code></td>
 *     </tr>
 *     <tr>
 *         <td><code>source</code></td>
 *         <td>ソースディレクトリ（projectルートからの相対パス）</td>
 *         <td><code>source/</code></td>
 *         <td><code>sourcedir</code>（オプションを除く第一引数）</td>
 *     </tr>
 *     <tr>
 *         <td><code>out</code></td>
 *         <td>出力先ディレクトリ（projectルートからの相対パス）</td>
 *         <td><code>build/docs/sphinx/${builder}</code></td>
 *         <td><code>outdir（オプションを除く第二引数）</code></td>
 *     </tr>
 *     <tr>
 *         <td><code>settings</code></td>
 *         <td>Sphinx設定ファイル(<code>conf.py</code>)の設定を上書きする値</td>
 *         <td><code>[:]</code></td>
 *         <td><code>-D ${key}=${value}</code></td>
 *     </tr>
 * </table>
 *
 * @author Ryo TANAKA
 * @since 1.0
 */
class Sphinxdoc extends Exec {

    /**
     * <code>sphinx-build</code>実行ファイルのパス
     */
    @Input
    String exec = 'sphinx-build'

    /**
     * Sphinxドキュメントのビルド形式
     */
    @Input
    String builder = 'html'

    /**
     * Sphinxドキュメントのルートディレクトリのパス（conf.pyのあるディレクトリ。）
     */
    @Input
    String root = '.'

    /**
     * ソースディレクトリ
     */
    @InputFiles
    File sourceFiles
    String source = 'source/'

    /**
     * 出力先ディレクトリ
     * <p/>
     * 指定されていない場合に、{@link #builder}から導出される値を取得したい場合は、
     * {@link #getOut()}を利用すること。
     */
    @OutputDirectory
    File outDir
    String out

    /**
     * Sphinxビルドの出力先ディレクトリを返却する。
     * 指定されていない場合には、{@link #builder}から導出される値を返却する。
     * <p/>
     * 出力先の分類を容易にするため、{@link #builder}から導出されるディレクトリは、
     * sphinxdocタスクのデフォルトの出力先ディレクトリの
     * {@link #builder}名のディレクトリとなる。
     * <p/>
     * ${#out}を上書きすることで、この出力先ディレクトリは容易に上書きできる。
     *
     * @return Sphinxビルドの出力先ディレクトリ
     */
    String getOut() {
        out ?: "build/docs/sphinxdoc/${builder}/"
    }

    /**
     * <code>conf.py</code>の設定を上書きする値 (なし)
     */
    @Input
    Map<String, String> settings = [:]

    /**
     * {@link Exec}タスク用のコマンドラインをセットアップする。
     * <p/>
     * 規約プロパティとコマンドライン引数の対応は、{@link Sphinxdoc}を参照。
     *
     * @param project タスクを設定する対象の{@link Project}
     * @return 実行されるコマンドライン
     */
    String configure() {
        sourceFiles = project.file(source)
        outDir = project.file(getOut())

        // 実行するコマンドラインのセットアップ（Execの設定）。
        executable = exec
        workingDir = project.file(root)
        args("-b")
        args(builder)
        settings.each { key, value ->
            args("-D")
            args("${key}=${value}")
        }
        args(sourceFiles.path)
        args(outDir.path)
        doLast {

        }
        commandLine
    }
}
