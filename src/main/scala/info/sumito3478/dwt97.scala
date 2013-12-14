package info.sumito3478

package object dwt97 {
  import scalaxy.loops._
  import scala.language.postfixOps

  def fwt97(xs: Array[Float]) = {
    val a1 = -1.586134342f
    val a2 = -0.05298011854f
    val a3 = 0.8829110762f
    val a4 = 0.4435068522f
    val k1 = 0.81289306611596146f
    val k2 = 0.61508705245700002f
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
    val tmp = new Array[Float](n)
    for (i <- 0 until n / 2 optimized) {
      tmp(i) = k1 * xs(i * 2)
      tmp(n / 2 + i) = k2 * xs(i * 2 + 1)
    }
    Array.copy(tmp, 0, xs, 0, n)
  }

  def iwt97(xs: Array[Float]) = {
    val a1 = 1.586134342f
    val a2 = 0.05298011854f
    val a3 = -0.8829110762f
    val a4 = -0.4435068522f
    val k1 = 1.230174104914f
    val k2 = 1.6257861322319229f
    val n = xs.length
    // Unpack and Undo Scale
    val tmp = new Array[Float](n)
    for (i <- 0 until n / 2 optimized) {
      tmp(i * 2) = k1 * xs(i)
      tmp(i * 2 + 1) = k2 * xs(n / 2 + i)
    }
    Array.copy(tmp, 0, xs, 0, n)
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