package fr.cayuelas.objects

case class Wrapper(path: String, hash: String, typeElement: String) {

}

object Wrapper{
  def apply(path: String, hash: String, typeElement: String): Wrapper = new Wrapper(path, hash, typeElement)
}