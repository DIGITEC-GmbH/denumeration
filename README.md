# DEnumeration

An optimized version of scala's 2.12 version of [Enumeration](https://github.com/scala/scala/blob/2.12.x/src/library/scala/Enumeration.scala).

 **Optimizations:** 

 + The operations *++*, *--*, *intersect*, *intersects* and *isEmpty* of *ValueSet* now use the internal *BitSet* to take constant time
  + *withName* now accesses a map to resolve Values and takes approximated constant time
 
**Interface Changes:** 
 + add exception free *withName* (*withNameOpt(s: String): Option[Value]*)
 + add exception free *apply* (*get(i: Int): Option[Value]*)


## License 

[Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0)