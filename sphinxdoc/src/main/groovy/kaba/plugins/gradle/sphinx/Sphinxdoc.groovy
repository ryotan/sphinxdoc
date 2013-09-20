package kaba.plugins.gradle.sphinx

import org.gradle.api.tasks.Exec
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

/**
 * Sphinxビルドを実行するタスク。
 *
 * @author Ryo TANAKA
 * @since 1.0
 */
class Sphinxdoc extends Exec {

    /**
     * <code>sphinx-build</code>実行ファイルのパス
     */
    @Input
    String executable

    /**
     * Sphinxドキュメントのルートディレクトリのパス（conf.pyのあるディレクトリ。）
     */
    @Input
    File root

    /**
     * ビルド形式
     */
    @Input
    String builder

    /**
     * ソースディレクトリ
     */
    @InputFiles
    File sourcedir

    /**
     * 出力先ディレクトリ
     */
    @OutputDirectory
    File outdir

    /**
     * <code>conf.py</code>の設定を上書きする値
     */
    @Input
    Map<String, String> settings

    @TaskAction
    def build() {
        args = buildArgs()
        workingDir = root
        execute()
    }

    List<String> buildArgs() {
        def args = []
        args << "-b"
        args << "${builder}"
        settings.each {
            args << "-D"
            args << "\"${key}\"=\"${value}\""
        }
        args << sourcedir.path
        args << outdir.path
        args
    }
}
