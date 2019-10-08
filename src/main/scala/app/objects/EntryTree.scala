package app.objects

case class EntryTree(typeElem: String, id: String, filename: String) {
def get_typeElem(): String = {
  this.typeElem
}
  def get_id(): String = {
    this.id
  }
  def get_fileName(): String = {
    this.filename
  }
}

object EntryTree{
  def apply(typeElem: String, id: String, filename: String): EntryTree = new EntryTree(typeElem, id, filename)
}