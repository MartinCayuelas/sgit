package fr.cayuelas.helpers

import org.scalatest.{FlatSpec, Matchers}

class HelperSha1Spec extends FlatSpec with Matchers {

  "Hashes strings" should " be equals with the same content" in {
      //Given
      val helperTested = HelperSha1
      val stringToConvert = "sha1 is very good"
      //When
      val hash1 = helperTested.convertToSha1(stringToConvert)
      val hash2 = helperTested.convertToSha1(stringToConvert)
      //Then
      assert(hash1==hash2)
    }

    it should "be different if the content isn't the same" in{
      //Given
      val helperTested = HelperSha1
      val stringToConvert = "sha1 is very good"
      val stringToConvert2 = "sha256 is better"
      //When
      val hash1 = helperTested.convertToSha1(stringToConvert)
      val hash2 = helperTested.convertToSha1(stringToConvert2)
      //Then
      assert(hash1!=hash2)
    }

}