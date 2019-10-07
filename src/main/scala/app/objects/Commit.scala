package app.objects

case class Commit() {
  private var idCommit: String = "new"
  private var  parent: String =  _
  private  var comment: String = ""
  private var commiter: String = "Me"

  def get_idCommit(): String = {
    return idCommit
  }
  def get_parent(): String = {
    return parent
  }
  def get_comment(): String = {
    return comment
  }
  def get_commiter(): String = {
    return commiter
  }

  def set_idCommit(id: String): Unit = {
    idCommit = id
  }
  def set_parent(p: String): Unit = {

    parent = p
  }
  def set_comment(newComment: String): Unit = {
    comment = newComment
  }
  def set_commiter(newCommiter: String): Unit = {
    return commiter
  }
}
