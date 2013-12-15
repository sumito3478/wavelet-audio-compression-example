package info.sumito3478

package object codec {
  import java.nio._
  import java.io._
  import org.apache.commons.io._
  import org.tukaani.xz._
  import scalaxy.debug._
  import scalaxy.loops._
  import javax.sound.sampled._
  import dwt97._

  val chunkMaxSize = 1024 * 1024 * 128 // 128 MB

  private[this] implicit class InputStreamW(val self: InputStream) extends AnyVal {
    def readShortArray(byteOrder: ByteOrder): Array[Short] = {
      val out = new ByteArrayOutputStream
      IOUtils.copy(self, out)
      val buffer = out.toByteArray
      val ret = new Array[Short](buffer.size / 2)
      val bytebuf = ByteBuffer.wrap(buffer)
      bytebuf.order(byteOrder)
      bytebuf.asShortBuffer.get(ret)
      ret
    }
  }

  private[this] def shortArrayToByteArray(xs: Array[Short]) = {
    val ret = new Array[Byte](xs.size * 2)
    val bytebuf = ByteBuffer.wrap(ret)
    bytebuf.order(ByteOrder.LITTLE_ENDIAN)
    bytebuf.asShortBuffer.put(xs)
    ret
  }

  implicit class IntW(val self: Int) extends AnyVal {
    def toEven = self - self % 2
  }

  def encode(in: AudioInputStream, out: OutputStream, correction: OutputStream) = {
    require(in.getFormat.getSampleSizeInBits == 16) // only 16-bit is supported for now
    require(in.getFormat.getChannels == 1) // only mono is supported for now
    val data = in.readShortArray(if (in.getFormat.isBigEndian) ByteOrder.BIG_ENDIAN else ByteOrder.LITTLE_ENDIAN)
    val buffer = data.map(s => s / 0x8000.toDouble)
    val orig = buffer.toBuffer
    for (i <- 0 until 5) {
      fwt97(buffer.view(0, (data.size / math.pow(2, i).toInt).toEven))
    }
    val convertedData = shortArrayToByteArray(buffer.map(f => (f * 0x8000).toShort))
    for (i <- 4 to 0 by -1) {
      iwt97(buffer.view(0, (data.size / math.pow(2, i).toInt).toEven))
    }
    val restored = buffer.map(f => (f * 0x8000).toShort)
    val correctionData = new Array[Byte](data.size)
    for (i <- 0 until data.size optimized) correctionData(i) = (data(i) - restored(i)).toByte
    println(correctionData.map(e => math.abs(e)).max)
    val xzout = new XZOutputStream(out, new LZMA2Options())
    new DataOutputStream(xzout).writeFloat(in.getFormat.getSampleRate)
    xzout.write(convertedData)
    xzout.finish
    val xzcor = new XZOutputStream(correction, new LZMA2Options())
    xzcor.write(correctionData)
    xzcor.finish
  }
  def decode(in: InputStream, correction: InputStream, out: OutputStream) = {
    val xzin = new XZInputStream(in)
    val sampleRate = new DataInputStream(xzin).readFloat
    val data = xzin.readShortArray(ByteOrder.LITTLE_ENDIAN)
    val buffer = data.map(s => s / 0x8000.toDouble)
    for (i <- 4 to 0 by -1) {
      iwt97(buffer.view(0, (data.size / math.pow(2, i).toInt).toEven))
    }
    val restored = buffer.map(f => (f * 0x8000).toShort)
    val xzcor = new XZInputStream(correction)
    val corout = new ByteArrayOutputStream
    IOUtils.copy(xzcor, corout)
    val correctionData = corout.toByteArray
    for (i <- 0 until restored.size) restored(i) = (restored(i) + correctionData(i)).toShort
    val ret = shortArrayToByteArray(restored)
    val ain = new AudioInputStream(new ByteArrayInputStream(ret), new AudioFormat(sampleRate, 16, 1, true, false), ret.length / 2)
    AudioSystem.write(ain, AudioFileFormat.Type.WAVE, out)
  }
}