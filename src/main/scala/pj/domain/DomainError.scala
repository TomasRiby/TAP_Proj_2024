package pj.domain

type Result[A] = Either[DomainError,A]

enum DomainError:

  //Standart Error
  case Error(error: String)
  case WrongFormat(error: String)
  case InvalidPreference(error: String)

  //XML Errors
  case IOFileProblem(error: String)
  case XMLError(error: String)
  case DuplicateError(error: String)

  def message: String = this match
    case Error(error) => error
    case WrongFormat(error) => error
    case IOFileProblem(error) => error
    case XMLError(error) => error
    case DuplicateError(error) => error
    case InvalidPreference(error) => error