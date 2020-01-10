# DEnumeration

An optimized version of scala's [Enumeration](https://github.com/scala/scala/blob/2.12.x/src/library/scala/Enumeration.scala).

 **Optimizations:** 
 + withName takes almost constant time
 + Operations on ValueSet take constant time
 
**Changes:** 
 + add exception free withName (withNameOpt(s: String): Option[Value])
 + add exception free apply (get(i: Int): Option[Value])


## License 

[Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0)