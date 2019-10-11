package fr.cayuelas.objects

case class Wrapper(path: String, hash: String, typeElement: String, name: String) {

}

object Wrapper{
  def apply(path: String, hash: String, typeElement: String, name: String): Wrapper = new Wrapper(path, hash, typeElement,name)
}