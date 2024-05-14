package pj.domain

type Result[A] = Either[DomainError, A]

enum DomainError:
  case Error(error: String)
  case WrongFormat(error: String)
  case IOFileProblem(error: String)
  case XMLError(error: String)
  case DuplicateError(error: String)
  case ImpossibleSchedule
  case AGENDA_DUPLICATED_VIVAS
  case AGENDA_NO_VIVAS
  case AGENDA_MULTIPLE_VIVAS
  case VIVA_MULTIPLE_ROLES(error: String)
  case TIME_INVALID_DATE(error: String)
  case TEACHER_INVALID_ID(error: String)
  case PERIOD_INVALID_TIMMINGS(error: String)
  case AGENDA_NO_TEACHERS
  case VIVA_INVALID_TITLE
  case VIVA_INVALID_PRESIDENT(error: String)
  case VIVA_INVALID_ADVISOR(error: String)
  case VIVA_INVALID_SUPERVISOR(error: String)
  case VIVA_INVALID_COADVISOR(error: String)
  case AVAILABILITY_SPLIT_ERROR(error: String)
  case InvalidPreference(error: String)
