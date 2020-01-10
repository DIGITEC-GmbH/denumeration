package de.digitec.denumeration

import java.util.concurrent.TimeUnit

import org.openjdk.jmh.annotations._

@State(Scope.Thread)
@BenchmarkMode(Array(Mode.AverageTime))
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 10)
@Measurement(iterations = 10)
@Fork(1)
class DEnumerationBenchmark {

   import DEnumerationBenchmark._

   @Benchmark
   def withNameEnumeration: Seq[Enum.Value] = Names.map { name =>
      Enum.withName(name)
   }

   @Benchmark
   def withNameDEnumeration: Seq[DEnum.Value] = Names.map { name =>
      DEnum.withName(name)
   }

   @Benchmark
   def valueSetUnionEnumeration: Enum.ValueSet = ErationSubsets.reduce(_ ++ _)

   @Benchmark
   def valueSetUnionDEnumeration: DEnum.ValueSet = EratorSubsets.reduce(_ ++ _)
}

object DEnumerationBenchmark {

   val Names = List("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P")


   object DEnum extends DEnumeration {
      val A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P = Value
   }

   object Enum extends Enumeration {
      val A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P = Value
   }

   val ErationSubsets: List[Enum.ValueSet] = Enum.values.subsets.toList
   val EratorSubsets: List[DEnum.ValueSet] = DEnum.values.subsets.toList
}
