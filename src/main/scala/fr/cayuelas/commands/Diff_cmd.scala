package fr.cayuelas.commands

import fr.cayuelas.helpers.{HelperBlob, HelperDiff, HelperPaths}
import fr.cayuelas.managers.{IoManager, StageManager}

object Diff_cmd {

  /**
   * Main function of the diff_cmd
   * Retrieves the stage and use it to perform the deiff over all the files staged
   */
  def diff(): Unit = {
    val stageToCommitSplited = StageManager.readStageToCommit().map(x => x.split(" "))
    stageToCommitSplited.map(file => {
      val contentBlob = HelperBlob.readContentInBlob(file(1))
      val contentOfFile = IoManager.readInFileAsLine(HelperPaths.sgitPath+file(2))
   HelperDiff.displayDifferenceBetweenTwoFiles(contentBlob, contentOfFile, file(2), file(1))
    })
  }

}
