package pj.domain

type Result[A] = Either[DomainError,A]

enum DomainError:

  //Standart Error
  case Error(error: String)
  case WrongFormat(error: String)

  //XML Errors
  case IOFileProblem(error: String)
  case XMLError(error: String)

  case NonNegativeIntError(error: String)
  case PositiveIntError(error: String)