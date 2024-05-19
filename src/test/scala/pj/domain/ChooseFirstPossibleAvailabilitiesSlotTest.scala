import Generator.{genAvailability, genODuration}
import org.scalacheck.*
import org.scalacheck.Prop.forAll
import pj.domain.{Algorithm, Availability}
import pj.opaqueTypes.ODuration.ODuration

object ChooseFirstPossibleAvailabilitiesSlotTest extends Properties("ChooseFirstPossibleAvailabilitiesSlot") {

  // Gerador para uma lista de disponibilidades
  def genAvailabilityList: Gen[List[Availability]] = Gen.listOf(genAvailability)

  // Gerador para uma lista de disponibilidades usadas
  def genUsedAvailabilityList: Gen[List[Availability]] = Gen.listOf(genAvailability)

  property("should always return a valid availability or none") = forAll(genAvailabilityList, genODuration, genUsedAvailabilityList):
    (availabilities: List[Availability], duration: ODuration, usedSlots: List[Availability]) =>

      val (slotOpt, updatedUsedSlots) = Availability.chooseFirstPossibleAvailabilitiesSlot(availabilities, duration, usedSlots)

      slotOpt match
        case Some((start, end, preference)) =>
          val durationAsDuration = duration.toDuration
          val slotDuration = java.time.Duration.between(start, end)
          // Verifica se a duração do slot corresponde à duração necessária
          (slotDuration == durationAsDuration) &&
            // Verifica se o slot escolhido não está na lista de usedSlots
            !usedSlots.exists(us => us.start.toLocalDateTime == start && us.end.toLocalDateTime == end)
        case None =>
          // Se nenhum slot é escolhido, significa que não havia slots disponíveis que correspondiam aos critérios
          availabilities.forall(usedSlots.contains)

  property("should return first slot when multiple slots are available") = forAll(genAvailabilityList, genODuration):
    (availabilities: List[Availability], duration: ODuration) =>

      // Filtra os slots disponíveis que correspondem à duração necessária
      val availableSlots = availabilities.filter(slot => {
        val slotDuration = java.time.Duration.between(slot.start.toLocalDateTime, slot.end.toLocalDateTime)
        // Converte ODuration para java.time.Duration para comparação
        val durationAsJavaString = duration.toString
        val durationAsJava = java.time.Duration.parse(durationAsJavaString)
        // Verifica se a duração do slot é igual à duração necessária
        slotDuration == durationAsJava
      })

      if (availableSlots.nonEmpty)
        val (slotOpt, _) = Availability.chooseFirstPossibleAvailabilitiesSlot(availabilities, duration, List.empty)
        slotOpt match
          case Some((start, _, _)) =>
            // Verifica se o slot escolhido é o primeiro slot disponível na lista de disponibilidades
            val firstAvailableSlot = availableSlots.headOption
            firstAvailableSlot.exists(_.start.toLocalDateTime == start)
          case None =>
            true
      else
        // Se não houver slots disponíveis que correspondam à duração necessária, o método deve retornar None
        val (slotOpt, _) = Availability.chooseFirstPossibleAvailabilitiesSlot(availabilities, duration, availabilities)
        slotOpt.isEmpty
}