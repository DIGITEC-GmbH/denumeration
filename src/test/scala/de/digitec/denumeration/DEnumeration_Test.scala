package de.digitec.denumeration

import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.junit.Assert._

@RunWith(classOf[JUnit4])
class DEnumerationTest {
   import DEnumerationTest._


   @Test
   def testWithName: Unit = {
      assertEquals(TestEnumeration.A, TestEnumeration.withName("A"))
      assertEquals(TestEnumeration.B, TestEnumeration.withName("B"))
      assertEquals(TestEnumeration.C, TestEnumeration.withName("X"))
      assertEquals(TestEnumeration.D, TestEnumeration.withName("Y"))
   }

   @Test
   def testApply: Unit = {
      assertEquals(TestEnumeration.A, TestEnumeration(0))
      assertEquals(TestEnumeration.B, TestEnumeration(3))
      assertEquals(TestEnumeration.C, TestEnumeration(4))
      assertEquals(TestEnumeration.D, TestEnumeration(5))
   }

}

object DEnumerationTest {

   object TestEnumeration extends DEnumeration {
      val A = Value
      val B = Value(3)
      val C = Value(4, "X")
      val D = Value(name = "Y")
   }
}
