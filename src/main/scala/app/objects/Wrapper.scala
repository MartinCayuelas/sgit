package app.objects

case class Wrapper(path: String, hash: String, typeE: String) {

  def get_path(): String = {
    this.path
  }
  def get_hash(): String = {
    this.hash
  }
  def get_TypeE(): String = {
    this.typeE
  }
}

object Wrapper{
  def apply(path: String, hash: String, blob: String): Wrapper = new Wrapper(path, hash, blob)
}