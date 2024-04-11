package pj.typeUtils.opaqueTypes

object opaqueTypes:
  opaque type ID = String
  opaque type Name = String
  opaque type Start = String
  opaque type End = String
  opaque type Preference = Int

  object ID:
    def apply(value: String): ID = value

  object Name:
    def apply(value: String): Name = value

  object Start:
    def apply(value: String): Start = value
