package pj.domain

final case class President private (
                           id: String
                          )

object President {
  def validateUniquePresidents(presidentIds: Seq[String]): Either[String, Boolean] =
    // Encontra IDs duplicados usando sizeIs para uma verificação mais eficiente
    val duplicates = presidentIds.groupBy(identity).collect { case (id, occurrences) if occurrences.sizeIs > 1 => id }
    duplicates.toList match
      case Nil => Right(true) // Nenhuma duplicata encontrada, todos os presidentes são únicos
      case ids => Left(s"Encontrados IDs de presidentes duplicados: ${ids.mkString(", ")}")
}