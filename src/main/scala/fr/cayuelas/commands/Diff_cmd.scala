package fr.cayuelas.commands

import fr.cayuelas.helpers.{HelperBlob, HelperCommit, HelperDiff, HelperPaths}
import fr.cayuelas.managers.{IOManager, StageManager}
import fr.cayuelas.objects.Wrapper

object Diff_cmd {

  /**
   * Main function of the diff_cmd
   * Retrieves the stage and use it to perform the deiff over all the files staged
   */
  def diff(): Unit = {
    val stageToCommitSplited = StageManager.readStageToCommit().map(x => x.split(" "))
    stageToCommitSplited.map(file => {
      val contentBlob = HelperBlob.readContentInBlob(file(1))
      val contentOfFile = IOManager.readInFileAsLine(HelperPaths.sgitPath+file(2))
   HelperDiff.displayDifferenceBetweenTwoFiles(contentBlob, contentOfFile, file(2), file(1))
    })
  }

  /**
   *
   * @param lastCommit
   * @return
   */

  def diffWhenCommitting(lastCommit: String): (Int, Int) = {
    val stageSplited= StageManager.readStageAsLines().map(x => x.split(" "))
    val hashes = stageSplited.map(x => x(1))
    val paths = stageSplited.map(x => x(2))

    val listZippedStageFiltered = hashes.zip(paths).map(x => x._2)
    val listBlobLastCommit: List[Wrapper] = HelperCommit.getAllBlobsFromCommit(lastCommit).map(blob => Wrapper(blob._2,blob._1,"Blob",""))
    val listBlobLastCommitFiltered = listBlobLastCommit.map(x => x.path)

    val listFilteredNewsFiles = listZippedStageFiltered.diff(listBlobLastCommitFiltered) //Files neverCommited

    val (inserted, deleted) = HelperDiff.accumulateCalculation(listBlobLastCommit, (0, 0))
    (inserted + HelperDiff.calculateNewsLinesWhenFileHasNeverBeenCommitted(listFilteredNewsFiles,0), deleted)
  }


}
