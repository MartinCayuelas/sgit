package fr.cayuelas.helpers

import org.scalatest.{FunSpec, Matchers}

class HelperSha1Spec extends FunSpec with Matchers {

  describe("Hashes strings"){

    it ("should be equals with the same content") {
      //Given
      val classTested = HelperSha1
      val stringToConvert = "sha1 is very good"

      //When
      val hash1 = classTested.convertToSha1(stringToConvert)
      val hash2 = classTested.convertToSha1(stringToConvert)

      //Then
      hash1 shouldBe hash2
    }

    it ("should be different if the content isn't the same"){
      //Given
      val classTested = HelperSha1
      val stringToConvert = "sha1 is very good"
      val stringToConvert2 = "sha256 is better"

      //When
      val hash1 = classTested.convertToSha1(stringToConvert)
      val hash2 = classTested.convertToSha1(stringToConvert2)

      //Then
      hash1 should not be hash2
    }
  }
}