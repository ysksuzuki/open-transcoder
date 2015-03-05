open-transcoder
===============

FFmpegとopenh264を利用したメディアトランスコーダー

Install
--------

open-transcodeを任意のディレクトリに配置する。

### Windows and Linux

1. open-transcode/ に移動して以下のコマンドを実行する。

~~~
$ java -jar open-transcoder.jar config
~~~

2. open-transcoder.confが生成されたことを確認し、続いて以下のコマンドを実行する。

~~~
$ java -jar open-transcoder.jar install
~~~

3. Linuxの場合のみ以下を.bash_profileに追加、sourceコマンドを実行して反映する。

~~~
$ export LD_LIBRARY_PATH=${OPEN_TRANSCODER_HOME}/bin:${LD_LIBRARY_PATH}
~~~


Execution
--------

~~~
$ java [-Dconfig.resource=application_win.json] -jar hls_converter.jar -i [file] -segment_list [file] [file] [options]
~~~
[system properties]
* -Dconfig.resource=application_win.json
    *    Windowsで実行する場合に必須

[options]
*  -i [file] 
    *    必須 変換元の動画ファイルを指定する。
    *    (例)(-i /home/user/input/input.mp4) 
*  -segment_list [file] [file]
    *    必須 変換後のplaylistのファイル名と、segmentファイルを指定する。
    *    (例)(-segment_list /home/user/hls/playlist.m3u8 /home/user/hls/segment%d.ts)
*  -prefix [value]
    *    任意 外部プログラムのパスを指定する。(ffmpeg, ffprobe, MP4Box, openh264enc)
    *    (例)(-prefix /usr/local/bin/)
*  -loglevel [value]
    *    任意 ログレベルを指定する。(quiet|error|warning|info)
    *    (例)(-loglevel info)

