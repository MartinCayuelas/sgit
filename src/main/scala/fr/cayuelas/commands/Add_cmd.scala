package fr.cayuelas.commands

import java.nio.file.Paths

import fr.cayuelas.helpers.HelperAdd
import fr.cayuelas.managers.IOManager

object Add_cmd {
  /**
   * This function is called in the Dispacher in the Laucher object
   *General function that transform each param in File and call a function which process the creating blobs processus
   * @param args : array of String that represents the name of files or folders
   */
  def add(args: Array[String]) : Unit = {
    if (args.length == 1) IOManager.specifyFileOrFolder()
    else {
      args.filter(_ != "add").map(arg =>{
        val file =  Paths.get(arg).toFile
        HelperAdd.addRoutine(file)
      })
    }
  }
}
