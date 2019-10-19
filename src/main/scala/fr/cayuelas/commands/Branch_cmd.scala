package fr.cayuelas.commands


import fr.cayuelas.helpers.{HelperBranch, HelperTag}
import fr.cayuelas.managers.IoManager

object Branch_cmd {
  /*
  BRANCH---------------------
   */
  /**
   * Function that dispatch actions following the args param.
   * @param args: contains the arg to do a specific action
   *            -av for diplaying branches and tags
   *            <branchName> to create a new branch
   */

  def branch(args: Array[String]): Unit = {
    if ((args.length == 2) && args(1).equals("-av")) {
      HelperBranch.displayAllBranches()
      IoManager.printSeparator()
      HelperTag.displayAllTags()
    }
    else if (args.length == 2) HelperBranch.createBranch(args(1))
    else IoManager.numberOfArgumentNotSupported(args(0))
  }


}
