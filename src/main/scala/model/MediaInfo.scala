package model

case class MediaInfo (
  duration: String,
  bit_rate: String,
  videoInfo: VideoInfo,
  audioInfo: AudioInfo
)

case class VideoInfo (
  codec_name: String = "",
  width: String = "",
  height: String = "'",
  r_frame_rate: String = ""
)

case class AudioInfo (
  codec_name: String = ""
)
