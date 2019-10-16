package fr.cayuelas.commands


import fr.cayuelas.helpers.HelperCommit
import fr.cayuelas.managers.{IOManager, StageManager}
import fr.cayuelas.objects.{Commit, Tree, Wrapper}


object Commit_cmd {
  /**
   * Main function that process the commit.
   * Checks if it is possible to commit.
   * If yes, then retrieves all files in root et in subdirectories and call a method to creates all the trees. Then th Commit class do the commit
   * If no, the user is informed that there is nothing to commit
   */
  def commit(args: Array[String]): Unit = {

    if (!StageManager.canCommit) IOManager.nothingToCommit()
    else {
      HelperCommit.mergeStageToCommitInStage()

      val stage: List[Wrapper] = StageManager.retrieveStageStatus()
      val blobsInRoot: List[Wrapper] = StageManager.retrieveStageRootBlobs()

      val hashFinalGhostTree: String = stage.nonEmpty match {
        case true => Tree.createTree(Some(HelperCommit.createAllTrees(stage, None)), Some(blobsInRoot))
        case false => Tree.createTree(Some(List()), Some(blobsInRoot))
      }
      //Creation and process about a Commit object (class)
      args match {
        case Array(_,"-m",_*) => Commit.commit(hashFinalGhostTree, args.filter(_ !="commit").filter(_ !="-m").mkString)
        case _ => Commit.commit(hashFinalGhostTree,"NoMessageForThisCommit")
      }
    }
  }
}
