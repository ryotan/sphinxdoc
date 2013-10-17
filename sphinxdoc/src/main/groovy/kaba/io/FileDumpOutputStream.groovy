package kaba.io

/**
 * ファイル出力を行った後、 {@link OutputStream} に処理を移譲する {@link FilterOutputStream}
 *
 * @author Ryo TANAKA
 * @since 1.0
 */
class FileDumpOutputStream extends InterceptingOutputStream {

    /**
     * 移譲先の {@link OutputStream} と出力先ファイルを指定して、
     * {@link FileDumpOutputStream} を生成する。
     *
     * @param out 処理を移譲する {@link OutputStream}
     * @param file 出力先ファイル
     */
    FileDumpOutputStream(OutputStream out, File file) {
        super(out, { byte[] bytes ->
            file.append(bytes)
            true
        })
    }
}
