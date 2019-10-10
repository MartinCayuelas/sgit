package app.objects

case class Wrapper(path: String, hash: String, typeE: String) {

}

object Wrapper{
  def apply(path: String, hash: String, blob: String): Wrapper = new Wrapper(path, hash, blob)
}