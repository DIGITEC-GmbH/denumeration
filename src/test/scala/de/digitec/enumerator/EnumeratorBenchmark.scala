package de.digitec.enumerator

import java.util.concurrent.TimeUnit

import org.openjdk.jmh.annotations._

@State(Scope.Thread)
@BenchmarkMode(Array(Mode.AverageTime))
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 10)
@Measurement(iterations = 10)
@Fork(1)
class EnumeratorBenchmark {

   import EnumeratorBenchmark._

   @Benchmark
   def withNameEnumeration: Seq[Eration.Value] = Names.map { name =>
      Eration.withName(name)
   }

   @Benchmark
   def withNameEnumerator: Seq[Erator.Value] = Names.map { name =>
      Erator.withName(name)
   }

   @Benchmark
   def valueSetUnionEnumerator: Eration.ValueSet = ErationSubsets.reduce(_ ++ _)

   @Benchmark
   def valueSetUnionEnumeration: Erator.ValueSet = EratorSubsets.reduce(_ ++ _)
}

object EnumeratorBenchmark {

   val Names = List("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P")


   object Erator extends Enumerator {
      val A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P = Value
   }

   object Eration extends Enumerator {
      val A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P = Value
   }

   val ErationSubsets: List[Eration.ValueSet] = Eration.values.subsets.toList
   val EratorSubsets: List[Erator.ValueSet] = Erator.values.subsets.toList
}
