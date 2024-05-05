package pj.domain

type Result[A] = Either[DomainError, A]

enum DomainError:
  case Error(error: String)
  case WrongFormat(error: String)
  case IOFileProblem(error: String)
  case XMLError(error: String)
  case DuplicateError(error: String)


  // Tested already
  case DuplicateVivasInAgenda // When multiple vivas have the same title
  case NoVivasInAgenda // When there are no vivas in the agenda
  case OverlappingAvailabilities // When there are duplicate availabilities in a resources availabilities list
  case MoreThanOneRole(error: String) // When a resource has more than one role on a viva
  case InvalidDateTime(error: String) // When a date doesn't follow the correct format
  case InvalidTeacherId(error: String) // When a teacher id doesn't follow the correct pattern
  case InvalidExternalPersonId(error: String) // When External person id doesn't follow the correct pattern
  case StudentWithMultipleVivas // When a student is associated with more than one viva
  case InvalidIdRef(error: String) // When an id on a viva can't be matched to a resource
  case InvalidInterval(error: String)
  case NoTeachersInAgenda
  case InvalidDuration(error: String)
  case InvalidHour(error: String)
  case InvalidMinute(error: String)
  case InvalidSecond(error: String)
  case InvalidPreference(error: String) // When an invalid preference is present on a viva in xml (< 1 | > 5)

  // Covered by findIn and InvalidIdRef
  case InvalidPresidentId(error: String)
  case InvalidAdvisorId(error: String)
  case InvalidSupervisorId(error: String)
  case InvalidCoadvisorId(error: String)

  case ImpossibleSchedule

  //  Covered by XML.fromNode / XML.fromAttribute

  case InvalidName // When a teacher's name on a viva is invalid (empty | !string)
  case InvalidVivaTitle // When a viva title is invalid (empty | !string)

  //  Doesn't need tests
  case Huh(error: String) // Error for when we don't know how the fuck something happened // Used for tests in the algorithm