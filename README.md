open-transcoder
===============

Media transcofing tool with FFmpeg and OpenH264

Install
--------

- Deploy open-transcode to an arbitrary directory
  - The explanation below is supposed that $OPEN_TRANSCODER_HOME is the directory

### Windows and Linux

- Move to open-transcode/, and run the command below

~~~
$ java -jar open-transcoder.jar config
~~~

- Confirm open-transcoder.conf in the current directory, and run the command below

~~~
$ java -jar open-transcoder.jar install
~~~

- In case of Linux, add the line below to .bash_profile, and run source command to enable system to use it.

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
-  -i  | --input  <file>    (required)
-  -o  | --output <file>... (required)
-  -l  | --logLevel <quiet|error|info|warning>
-  -h  | --help
-  -li | --license

[Example]
-  mp4
    -  java -jar open-transcoder.jar -i input.mp4 -o output.mp4 -l quiet
-  hls
    -  java -jar open-transcoder.jar -i input.mp4 -o playlist.m3u8 -l quiet
-  webm
    -  java -jar open-transcoder.jar -i input.mp4 -o output.webm -l quiet

[Usage - config]
-  java -jar open-transcoder.jar config

[Usage - install]
-  java -jar open-transcoder.jar install

