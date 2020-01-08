package de.digitec.`enum`

import scala.collection.{AbstractSet, SortedSetLike, generic, immutable, mutable}
import java.lang.reflect.{Field => JField, Method => JMethod}

import scala.collection.generic.CanBuildFrom
import scala.collection.immutable.SortedSet
import scala.reflect.NameTransformer._
import scala.util.matching.Regex

// TODO CDO: Licence and Notice

abstract class Enumeration (initial: Int) extends Serializable { thisenum =>

   def this() = this(0)

   /* Note that `readResolve` cannot be private, since otherwise
      the JVM does not invoke it when deserializing subclasses. */
   protected def readResolve(): AnyRef = thisenum.getClass.getField(MODULE_INSTANCE_NAME).get(null)

   /** The name of this enumeration.
    */
   override def toString: String =
      ((getClass.getName stripSuffix MODULE_SUFFIX_STRING split '.').last split
         Regex.quote(NAME_JOIN_STRING)).last

   /** The mapping from the integer used to identify values to the actual
    * values. */
   private val vmap: mutable.Map[Int, Value] = new mutable.HashMap

   /** The cache listing all values of this enumeration. */
   @transient private var vset: ValueSet = null
   @transient @volatile private var vsetDefined = false

   /** The mapping from the integer used to identify values to their
    * names. */
   private val nmap: mutable.Map[Int, String] = new mutable.HashMap

   /** The values of this enumeration as a set.
    */
   def values: ValueSet = {
      if (!vsetDefined) {
         vset = (ValueSet.newBuilder ++= vmap.values).result()
         vsetDefined = true
      }
      vset
   }

   /** The integer to use to identify the next created value. */
   protected var nextId: Int = initial

   /** The string to use to name the next created value. */
   protected var nextName: Iterator[String] = _

   private def nextNameOrNull =
      if (nextName != null && nextName.hasNext) nextName.next() else null

   /** The highest integer amongst those used to identify values in this
    * enumeration. */
   private var topId = initial

   /** The lowest integer amongst those used to identify values in this
    * enumeration, but no higher than 0. */
   private var bottomId = if(initial < 0) initial else 0

   /** The one higher than the highest integer amongst those used to identify
    *  values in this enumeration. */
   final def maxId: Int = topId

   /** The value of this enumeration with given id `x`
    */
   final def apply(x: Int): Value = vmap(x)

   /** Return a `Value` from this `Enumeration` whose name matches
    *  the argument `s`.  The names are determined automatically via reflection.
    *
    * @param  s an `Enumeration` name
    * @return   the `Value` of this `Enumeration` if its name matches `s`
    * @throws   NoSuchElementException if no `Value` with a matching
    *           name is in this `Enumeration`
    */
   final def withName(s: String): Value = values.find(_.toString == s).getOrElse(
      throw new NoSuchElementException(s"No value found for '$s'"))

   /** Creates a fresh value, part of this enumeration. */
   protected final def Value: Value = Value(nextId)

   /** Creates a fresh value, part of this enumeration, identified by the
    *  integer `i`.
    *
    *  @param i An integer that identifies this value at run-time. It must be
    *           unique amongst all values of the enumeration.
    *  @return  Fresh value identified by `i`.
    */
   protected final def Value(i: Int): Value = Value(i, nextNameOrNull)

   /** Creates a fresh value, part of this enumeration, called `name`.
    *
    *  @param name A human-readable name for that value.
    *  @return  Fresh value called `name`.
    */
   protected final def Value(name: String): Value = Value(nextId, name)

   /** Creates a fresh value, part of this enumeration, called `name`
    *  and identified by the integer `i`.
    *
    * @param i    An integer that identifies this value at run-time. It must be
    *             unique amongst all values of the enumeration.
    * @param name A human-readable name for that value.
    * @return     Fresh value with the provided identifier `i` and name `name`.
    */
   protected final def Value(i: Int, name: String): Value = new Val(i, name)

   private def populateNameMap() {
      val fields: Array[JField] = getClass.getDeclaredFields
      def isValDef(m: JMethod): Boolean = fields exists (fd => fd.getName == m.getName && fd.getType == m.getReturnType)

      // The list of possible Value methods: 0-args which return a conforming type
      val methods: Array[JMethod] = getClass.getMethods filter (m => m.getParameterTypes.isEmpty &&
         classOf[Value].isAssignableFrom(m.getReturnType) &&
         m.getDeclaringClass != classOf[Enumeration] &&
         isValDef(m))
      methods foreach { m =>
         val name = m.getName
         // invoke method to obtain actual `Value` instance
         val value = m.invoke(this).asInstanceOf[Value]
         // verify that outer points to the correct Enumeration: ticket #3616.
         if (value.outerEnum eq thisenum) {
            val id = Int.unbox(classOf[Val] getMethod "id" invoke value)
            nmap += ((id, name))
         }
      }
   }

   /* Obtains the name for the value with id `i`. If no name is cached
    * in `nmap`, it populates `nmap` using reflection.
    */
   private def nameOf(i: Int): String = synchronized { nmap.getOrElse(i, { populateNameMap() ; nmap(i) }) }

   /** The type of the enumerated values. */
   @SerialVersionUID(7091335633555234129L)
   abstract class Value extends Ordered[Value] with Serializable {
      /** the id and bit location of this enumeration value */
      def id: Int
      /** a marker so we can tell whose values belong to whom come reflective-naming time */
      private[DEnumeration] val outerEnum = thisenum

      override def compare(that: Value): Int =
         if (this.id < that.id) -1
         else if (this.id == that.id) 0
         else 1
      override def equals(other: Any): Boolean = other match {
         case that: Enumeration#Value  => (outerEnum eq that.outerEnum) && (id == that.id)
         case _                        => false
      }
      override def hashCode: Int = id.##

      /** Create a ValueSet which contains this value and another one */
      def + (v: Value): ValueSet = ValueSet(this, v)
   }

   /** A class implementing the [[scala.Enumeration.Value]] type. This class
    *  can be overridden to change the enumeration's naming and integer
    *  identification behaviour.
    */
   @SerialVersionUID(0 - 3501153230598116017L)
   protected class Val(i: Int, name: String) extends Value with Serializable {
      def this(i: Int)       = this(i, nextNameOrNull)
      def this(name: String) = this(nextId, name)
      def this()             = this(nextId)

      assert(!vmap.isDefinedAt(i), "Duplicate id: " + i)
      vmap(i) = this
      vsetDefined = false
      nextId = i + 1
      if (nextId > topId) topId = nextId
      if (i < bottomId) bottomId = i

      def id: Int = i

      override def toString(): String = {
         if (name != null) name
         else try thisenum.nameOf(i)
         catch { case _: NoSuchElementException => "<Invalid enum: no field for #" + i + ">" }
      }

      protected def readResolve(): AnyRef = {
         val enum = thisenum.readResolve().asInstanceOf[Enumeration]
         if (enum.vmap == null) this
         else enum.vmap(i)
      }
   }

   /** An ordering by id for values of this set */
   object ValueOrdering extends Ordering[Value] {
      def compare(x: Value, y: Value): Int = x compare y
   }

   /** A class for sets of values.
    *  Iterating through this set will yield values in increasing order of their ids.
    *
    *  @param nnIds The set of ids of values (adjusted so that the lowest value does
    *    not fall below zero), organized as a `BitSet`.
    *  @define Coll `collection.immutable.SortedSet`
    */
   class ValueSet private[ValueSet] (private var nnIds: immutable.BitSet)
      extends AbstractSet[Value]
         with immutable.SortedSet[Value]
         with SortedSetLike[Value, ValueSet]
         with Serializable {

      implicit def ordering: Ordering[Value] = ValueOrdering
      def rangeImpl(from: Option[Value], until: Option[Value]): ValueSet =
         new ValueSet(nnIds.rangeImpl(from.map(_.id - bottomId), until.map(_.id - bottomId)))

      override def empty: ValueSet = ValueSet.empty

      def contains(v: Value): Boolean = nnIds contains (v.id - bottomId)
      def + (value: Value) = new ValueSet(nnIds + (value.id - bottomId))
      def - (value: Value) = new ValueSet(nnIds - (value.id - bottomId))

      // TODO CDO: ???
      //def ++(that: ValueSet): ValueSet = new ValueSet(this.nnIds | that.nnIds)
      //def --(that: ValueSet): ValueSet = new ValueSet(this.nnIds &~ that.nnIds)
      //def intersect(that: ValueSet): ValueSet = new ValueSet(this.nnIds & that.nnIds)
      //def intersects(that: ValueSet): Boolean = this.intersect(that).nonEmpty

      def iterator: Iterator[Value] = nnIds.iterator map (id => thisenum.apply(bottomId + id))

      override def keysIteratorFrom(start: Value): Iterator[Value] =
         nnIds keysIteratorFrom start.id  map (id => thisenum.apply(bottomId + id))

      override def stringPrefix: String = thisenum + ".ValueSet"
      /** Creates a bit mask for the zero-adjusted ids in this set as a
       *  new array of longs */
      def toBitMask: Array[Long] = nnIds.toBitMask
   }

   /** A factory object for value sets */
   object ValueSet {
      import generic.CanBuildFrom

      /** The empty value set */
      val empty = new ValueSet(immutable.BitSet.empty)
      /** A value set consisting of given elements */
      def apply(elems: Value*): ValueSet = (newBuilder ++= elems).result()
      /** A value set containing all the values for the zero-adjusted ids
       *  corresponding to the bits in an array */
      def fromBitMask(elems: Array[Long]): ValueSet = new ValueSet(immutable.BitSet.fromBitMask(elems))
      /** A builder object for value sets */
      def newBuilder: mutable.Builder[Value, ValueSet] = new mutable.Builder[Value, ValueSet] {
         private[this] val b = new mutable.BitSet
         def += (x: Value): this.type = { b += (x.id - bottomId); this }
         def clear(): Unit = b.clear()
         def result(): ValueSet = new ValueSet(b.toImmutable)
      }
      /** The implicit builder for value sets */
      implicit def canBuildFrom: CanBuildFrom[ValueSet, Value, ValueSet] =
         new CanBuildFrom[ValueSet, Value, ValueSet] {
            def apply(from: ValueSet): mutable.Builder[Value, ValueSet] = newBuilder
            def apply(): mutable.Builder[Value, ValueSet] = newBuilder
         }
   }
}
