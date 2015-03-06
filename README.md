open-transcoder
===============

FFmpegとopenh264を利用したメディアトランスコーダー

Install
--------

open-transcodeを任意のディレクトリに配置する。

### Windows and Linux

- open-transcode/ に移動して以下のコマンドを実行する。

~~~
$ java -jar open-transcoder.jar config
~~~

- open-transcoder.confが生成されたことを確認し、続いて以下のコマンドを実行する。

~~~
$ java -jar open-transcoder.jar install
~~~

- Linuxの場合のみ以下を.bash_profileに追加、sourceコマンドを実行して反映する。

~~~
$ export LD_LIBRARY_PATH=${OPEN_TRANSCODER_HOME}/bin:${LD_LIBRARY_PATH}
~~~

Execution
--------

[Sub Command]
transcode (default)
config
install

[Usage - transcode]
java -jar open-transcoder.jar [options]

[options]
*  -i  | --input  <file>    (required)
*  -o  | --output <file>... (required)
*  -l  | --logLevel <quiet|error|info|warning>
*  -h  | --help
*  -li | --license

[Example]
*  mp4
    *  java -jar open-transcoder.jar -i input.mp4 -o output.mp4 -l quiet
*  hls
    *  java -jar open-transcoder.jar -i input.mp4 -o playlist.m3u8 -l quiet
*  webm
    *  java -jar open-transcoder.jar -i input.mp4 -o output.webm -l quiet

[Usage - config]
*  java -jar open-transcoder.jar config

[Usage - install]
*  java -jar open-transcoder.jar install

