package app.objects


case class Commit(var idCommit: String="new", var parent: String=_, var comment:String=_, commiter:String="Me") {


  def get_idCommit(): String = {
   this.idCommit
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
    this.idCommit = id
  }
  def set_parent(p: String): Unit = {

    parent = p
  }
  def set_comment(newComment: String): Unit = {
    comment = newComment
  }


}