package app

case class Tree() {
  private var idTree = "new"
  private var contentTree = List("")

  def set_idTree(id: String): Unit = {
    idTree = id
  }
  def set_contentTree(list: List[String]):Unit = {
    contentTree = list
  }

  def get_idTree(): String = {
    return idTree
  }

  def get_contentTree(): List[String] = {
    return contentTree
  }

  def addContentTree(newContent: String): List[String] = {
    newContent::get_contentTree()
  }

  def createId(): String = {

  }

}
