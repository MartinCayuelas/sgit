package fr.cayuelas.commands


import fr.cayuelas.helpers.HelperTag
import fr.cayuelas.managers.IOManager

object Tag_cmd {

  /**
   * Main function for tags that dispatch actions given a paramater
   * @param args : should contains the name of the new tag
   */
  def tag(args: Array[String]): Unit = {
    if (args.length == 2) HelperTag.createTag(args(1))
    else  IOManager.numberOfArgumentNotSupported(args(0))
  }


}
