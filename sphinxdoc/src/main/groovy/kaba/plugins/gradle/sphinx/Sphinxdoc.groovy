package kaba.plugins.gradle.sphinx

import org.gradle.api.tasks.Exec
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory

/**
 * Sphinxビルドを実行するタスク。
 *
 * @author Ryo TANAKA
 * @since 1.0
 */
class Sphinxdoc extends Exec {

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

    void buildArgs() {
        args("-b")
        args(builder)
        settings.each {
            args("-D")
            args("\"${key}\"=\"${value}\"")
        }
        args(sourcedir.path)
        args(outdir.path)
        workingDir = root
    }
}
