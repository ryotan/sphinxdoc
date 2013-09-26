package kaba.plugins.gradle.sphinx

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

/**
 * Sphinxドキュメントのビルドに必要な設定を提供する規約オブジェクト。
 * <p/>
 * Sphinxドキュメントのビルド時に設定可能な値として、以下を提供する。（括弧内はデフォルト値）
 * <table border="1">
 *     <tr align="left">
 *         <th>プロパティ</th>
 *         <th>設定する値</th>
 *         <th>デフォルト値</th>
 *         <th>対応するsphinx-build引数</th>
 *     </tr>
 *     <tr>
 *         <td><code>executable</code></td>
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
 *         <td><code>sourcedir</code></td>
 *         <td>ソースディレクトリ（projectルートからの相対パス）</td>
 *         <td><code>source/</code></td>
 *         <td><code>sourcedir</code>（オプションを除く第一引数）</td>
 *     </tr>
 *     <tr>
 *         <td><code>outdir</code></td>
 *         <td>出力先ディレクトリ（projectルートからの相対パス）</td>
 *         <td><code>build/docs/sphinx/${builder}</code></td>
 *         <td><code>outdir（オプションを除く第二引数）</code></td>
 *     </tr>
 *     <tr>
 *         <td><code>settings</code></td>
 *         <td>Sphinx設定ファイル(<code>conf.py</code>)の設定を上書きする値</td>
 *         <td><code>null</code></td>
 *         <td><code>-D ${key}=${value}</code></td>
 *     </tr>
 * </table>
 */
@EqualsAndHashCode
@ToString(includeNames = true, includeFields = true)
class SphinxdocConvention {

    /**
     * <code>sphinx-build</code>実行ファイルのパス
     */
    String executable = 'sphinx-build'

    /**
     * ビルド形式
     */
    String builder = 'html'

    /**
     * Sphinxドキュメントのルートディレクトリのパス（conf.pyのあるディレクトリ。）
     */
    String root = '.'

    /**
     * ソースディレクトリ
     */
    String sourcedir = 'source/'

    /**
     * 出力先ディレクトリ
     * <p/>
     * {@link #builder} のみ設定されている場合は、<code>build/docs/sphinxdoc/${builder}/</code>が設定される。
     */
    String outdir

    /**
     * <code>conf.py</code>の設定を上書きする値 (なし)
     */
    Map<String, String> settings = [:]

    String getOutdir() {
        outdir ?: "build/docs/sphinxdoc/${builder}/"
    }
}
