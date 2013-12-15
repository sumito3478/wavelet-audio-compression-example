package info.sumito3478

package object dwt97 {
  import scalaxy.loops._
  import scala.language.postfixOps
  import scala.collection._

  def fwt97(xs: mutable.IndexedSeq[Double]) = {
    val a1 = -1.586134342
    val a2 = -0.05298011854
    val a3 = 0.8829110762
    val a4 = 0.4435068522
    val k1 = 0.81289306611596146
    val k2 = 0.61508705245700002
    val n = xs.length
    // Predict 1
    for (i <- 1 until n - 2 by 2 optimized)
      xs(i) += a1 * (xs(i - 1) + xs(i + 1))
    xs(n - 1) += 2 * a1 * xs(n - 2)
    // Update 1
    for (i <- 2 until n by 2 optimized)
      xs(i) += a2 * (xs(i - 1) + xs(i + 1))
    xs(0) += 2 * a2 * xs(1)
    // Predict 2
    for (i <- 1 until n - 2 by 2 optimized)
      xs(i) += a3 * (xs(i - 1) + xs(i + 1))
    xs(n - 1) += 2 * a3 * xs(n - 2)
    // Update 2
    for (i <- 2 until n by 2 optimized)
      xs(i) += a4 * (xs(i - 1) + xs(i + 1))
    xs(0) += 2 * a4 * xs(1)
    // Scale and pack
    val tmp = new Array[Double](n)
    for (i <- 0 until n / 2 optimized) {
      tmp(i) = k1 * xs(i * 2)
      tmp(n / 2 + i) = k2 * xs(i * 2 + 1)
    }
    for (i <- 0 until n optimized) {
      xs(i) = tmp(i)
    }
  }

  def iwt97(xs: mutable.IndexedSeq[Double]) = {
    val a1 = 1.586134342
    val a2 = 0.05298011854
    val a3 = -0.8829110762
    val a4 = -0.4435068522
    val k1 = 1.230174104914
    val k2 = 1.6257861322319229
    val n = xs.length
    // Unpack and Undo Scale
    val tmp = new Array[Double](n)
    for (i <- 0 until n / 2 optimized) {
      tmp(i * 2) = k1 * xs(i)
      tmp(i * 2 + 1) = k2 * xs(n / 2 + i)
    }
    for (i <- 0 until n optimized) {
      xs(i) = tmp(i)
    }
    // Undo update 2
    for (i <- 2 until n by 2 optimized) {
      xs(i) += a4 * (xs(i - 1) + xs(i + 1))
    }
    xs(0) += 2 * a4 * xs(1)
    // Undo predict 2
    for (i <- 1 until n - 2 by 2 optimized) {
      xs(i) += a3 * (xs(i - 1) + xs(i + 1))
    }
    xs(n - 1) += 2 * a3 * xs(n - 2)
    // Undo update 1
    for (i <- 2 until n by 2 optimized) {
      xs(i) += a2 * (xs(i - 1) + xs(i + 1))
    }
    xs(0) += 2 * a2 * xs(1)
    // Undo predict 1
    for (i <- 1 until n - 2 by 2 optimized) {
      xs(i) += a1 * (xs(i - 1) + xs(i + 1))
    }
    xs(n - 1) += 2 * a1 * xs(n - 2)
  }
}