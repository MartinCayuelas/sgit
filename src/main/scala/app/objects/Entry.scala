package app.objects

case class Entry(path: String, hash: String, typeE: String) {
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

object Entry{
  def apply(path: String, hash: String, blob: String): Entry = new Entry(path, hash, blob)
}