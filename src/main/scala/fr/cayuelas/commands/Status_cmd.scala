package fr.cayuelas.commands

import fr.cayuelas.managers.StageManager

object Status_cmd {

  /**
   * Main function that dispatch the action
   */
  def status() : Unit ={
    println(s"On the ${Branch_cmd.getCurrentBranch} branch")
    println("Changes that will be validated : ")
    println()
    getChangesThatWillBeValidatedNew.map(elem => println(s"   new file : ${elem}"))
    println()
    println("Changes that will not be validated:")
    println("   (use \"git add <file> ...\" to update what will be validated)")
    println()

    println("Files untracked:")
    println("   (use \"git add <file> ...\" to update what will be validated)")
    println()

  }

  /**
   *Method that retrieve the files that will be added in the next commit
   * @return a list[String] containing all changes that will be validated
   */
  def getChangesThatWillBeValidatedNew: List[String] = {

    StageManager.retrieveLinesBeginningWithStars.map(x => x.split(" ")).map(x => x(2))


  }

}
