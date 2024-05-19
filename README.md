# The Viva to be Scheduled Problem

## Index

- [The Viva to be Scheduled Problem](#the-viva-to-be-scheduled-problem)
    - [The Problem](#the-problem)
    - [Domain Model](#domain-model)
    - [Objective](#objective)
    - [Milestones Development Process](#milestones-development-process)
        - [MS01](#ms01)
            - [Overview (Main Decisions)](#overview-main-decisions)
            - [Possible alternatives](#possible-alternatives)
            - [Possible future improvements](#possible-future-improvements)

## The Problem

This repository serves as the central hub for both the documentation and the source code of the project crafted for
the "Técnicas Avançadas de Programação" (Advanced Programming Techniques) course, which is part of the Master’s in
Computer Engineering curriculum at the Instituto Superior de Engenharia do Porto (ISEP).
The challenge at hand involves the intricate scheduling of Master of Science (MSc) dissertation defenses, also known
colloquially and in academic terms as "viva," from the Latin "viva voce," meaning "by live voice." This scheduling
endeavor includes multiple stakeholders, each viva needing to be arranged at the most opportune time within the confines
of existing scheduling constraints. Moreover, within a specified time frame, numerous vivas need coordination, with
various participants often being involved in multiple defenses.

This problem is classified as NP-hard, indicating that in its unconstrained form, it is intractable. However, applying
specific constraints related to the availability of the involved entities (resources) can render the problem manageable,
or "tractable." The initial phase of this project, which marks the first milestone, will start by addressing only a
subset of these constraints. As the project progresses through its phases, particularly in milestones M1 and M3, it will
gradually incorporate more constraints and refine the algorithms designed to optimize the scheduling process. These
algorithms aim to progressively enhance the quality and feasibility of the scheduling solutions, illustrating a
methodical approach to tackling this complex problem.

## Domain Model

![img.png](./assets/domain-v3.png)

The domain was created based on an analysis conducted on the input XML files for the problem's use case.
The domain classes consist of Agenda, Viva, and Resources, which comprises a list of Teachers and Externals.

Both Teachers and Externals have availabilities that include start and end datetime, as well as the subject's
preferences.
The Resources class was introduced to combine both Externals and Teachers, providing a unified structure for managing
resource's availability.

## Objective

The main objective of this work is the design and development of an application using functional
programming techniques.

## Milestones Development Process

### MS01

#### Overview (Main Decisions)

The initial milestone of our project was developed using the Test Driven Development (TDD) methodology, a strategy
particularly beneficial at the onset when the domain model concepts were still being defined and no code base existed.
To foster robust development and minimize potential bugs from the outset, we prioritized writing unit tests that
verified the accuracy and stability of our domain model before beginning the actual coding.
Starting with these tests allowed us to mitigate possible bugs and clearly define the expected functionality of the
code.
This approach not only facilitated a clearer development process but also helped maintain the integrity of the domain
model throughout the project’s progression.
By continuously updating and referring to our comprehensive suite of unit tests, we were able to quickly identify and
rectify defects arising from recent changes, ensuring a stable and reliable codebase.

The Classes were created in the `io` package for reading XML files and loading data into memory.
To separate responsibilities and ease maintenance in case of issues, a class was created for each context, such
as `AgendaIO`, `VivaIO`, and finally `ResourceIO`, which encompasses the concepts of teachers and externals.

The decision to group the concepts of teachers and externals was made due to the similarity in their data structure.
Consolidating them into a single Resource simplifies computing their data for the final goal of the proposed problem, as
there's no need to compare between two different data structures.

As for the algorithm, the function `makeTheAlgorithmHappen` takes an agenda structure containing availability
information of teachers and external agents, as well as details about "vivas" (likely an academic defense or
presentation) and the expected duration for each event.
Initially, the function extracts and groups the availabilities of teachers and external agents by their unique
identifiers. It then uses these availabilities to attempt to schedule the "vivas", ensuring that the schedules of the
president, advisor, and supervisor are synchronized without conflicts.

For each "viva", the `CreateSchedule` function creates a schedule attempting to allocate times where all necessary
participants are simultaneously available. The ExtractAvail function then looks for overlaps in the available times of
the participants that meet the required duration for the "viva". If found, these overlaps are considered as feasible
times.

Furthermore, the `findBestCombinedAvailability` function is used to determine the best possible overlap of times
considering the required participants and the specified duration. It searches for time intervals where all participants
are available at the same time and that the interval is sufficient as per the specified duration.

Finally, the `processSchedules` function processes all possible "viva" schedules to filter and select those that are not
only feasible but also do not overlap with others already selected, ensuring the best allocation of times for all
sessions. The list of selected times is then returned as the final result of the function, sorted by the start of each
available time.

#### Possible alternatives

#### Possible future improvements

As we move forward, several areas stand out for enhancement.

- **Incorporation of the role `Co-advisor`**: In the future the team intends to incorporate the role `Co-advisor` in the
  algorithm, since it was forgotten and this fail was noticed too late to act on it.

- **Implementation of More Tests**: While our current test suite provides valuable coverage, more test will fortify the
  reliability of our software. Introducing functional tests to validate interactions between components will further
  validate the correctness of our codebase.

- **Improvement of Project Structure**: Enhancing the organization and clarity of our project structure will improve
  development processes and facilitate collaboration among team members.

- **Optimization of Code**: Identifying and addressing certain code duplication and inefficiencies within our codebase
  will enhance the efficiency and readability of our application.

- **Creation of More Input Test Files**: Increasing our collection of input test files with more diverse agendas will
  enhance the comprehensiveness of our testing efforts. Also, with this, we can uncover more error cases that might
  otherwise go unnoticed, bolstering the resilience and adaptability of our software.

By prioritizing these among other possible improvements, we aim to elevate the quality, reliability, and performance of
our application project, allowing us to deliver a solution that exceeds expectations and withstands the demands of
evolving requirements.

### MS02

The use of generators and property-based testing in software development offers numerous advantages in terms of both
efficiency and code quality.
Generators allow for the creation of iterators that produce values on demand without the need to store all values in
memory, resulting in more efficient memory usage and improved performance, especially when handling large volumes of
data.
Additionally, they simplify and enhance the readability of the code. Property-based testing, on the other hand, focuses
on defining properties that function outputs must satisfy and automatically generates numerous test cases to verify
these properties.
This significantly expands test coverage, helping to identify bugs that would not be discovered through traditional
testing methods.
This systematic approach explores various usage scenarios and edge case inputs, documents the expected behavior of the
system, and facilitates code comprehension and maintenance.
Combining these techniques leads to more robust, reliable, and maintainable software, substantially improving
development and testing processes and ensuring the delivery of high-quality software.

#### Property tests

- **ChooseFirstPossibleAvailabilitiesSlotTest:**
    - **should always return a valid availability or none**: tests the chooseFirstPossibleAvailabilitiesSlot method to
      ensure it either returns a valid available slot or none if no suitable slot is found. The test uses generators to
      create random lists of availabilities and used slots along with a duration. It then checks that if a slot is
      chosen, the duration of the slot matches the required duration and that the chosen slot is not in the list of used
      slots. If no slot is chosen, it validates that this is because all potential slots were either unavailable or did
      not meet the criteria.
    - **should return first slot when multiple slots are available**: verifies that when there are multiple available
      slots that match the required duration, the method correctly returns the first suitable slot. This property
      filters the generated availabilities to find those that match the required duration and checks if the method
      returns the first slot in this filtered list. If there are no available slots that meet the criteria, it ensures
      that the method appropriately returns None. This property ensures that the selection logic prioritizes the first
      available slot, validating the correct ordering and selection mechanism of the
      chooseFirstPossibleAvailabilitiesSlot method.
- **PreVivaToMapTest:**
    - **preVivaToMap should maintain role-availability correspondence**: ensures that the method correctly maps roles to
      their corresponding availabilities. It generates a list of PreViva instances, applies the preVivaToMap method, and
      then checks if the output map matches the expected result, where each role is associated with its respective
      availabilities as given in the input.
    - **preVivaToMap should not modify original input data**: verifies that the method does not alter the original list
      of PreViva instances. After generating a list of PreViva instances and applying the preVivaToMap method, it checks
      that each PreViva object remains the same in memory, ensuring that the input data remains unchanged
      post-processing. This is important for ensuring that the method is purely functional and does not have unintended
      side effects
    - **preVivaToMap should maintain correct availabilities for each role**: ensures that the availabilities associated
      with each role in the result are accurate. After generating a list of PreViva instances and applying the
      preVivaToMap method, it extracts the expected availabilities for each role and checks if the output map correctly
      reflects these availabilities. This property validates the accuracy of the mapping process, confirming that each
      role's availabilities are correctly preserved and represented in the output.



- **AvailabilityGenerators:**
    - **generateAvailabilityFromDay**: it receives a day and outputs an availability for that day. It starts randomly in
      the day and the ends between 2 and 6 hours. then it tries to create it. If it fails it tries to generate again.
    - **generateAvailabilityListForADay**: it generates between 10 and 20 availabilities for a Day. It also uses the
      makeNonOverlapping function so it doesn't generate overlapping availabilities.


### VivaTest

- **generatePresident**: Generates a `President` ID that is not already in the provided list of IDs. Ensures unique ID assignment for the President role.
- **generateAdvisor**: Generates an `Advisor` ID that is not already in the provided list of IDs. Ensures unique ID assignment for the Advisor role.
- **generateCoAdvisor**: Generates a `CoAdvisor` ID from either teacher or external IDs that is not already in the provided list of IDs. Ensures unique ID assignment for the CoAdvisor role.
- **generateSupervisor**: Generates a `Supervisor` ID from external IDs that is not already in the provided list of IDs. Ensures unique ID assignment for the Supervisor role.
- **generateSupervisorList**: Generates a list of `Supervisors` from a list of IDs, ensuring no duplicates within the list. Uses a recursive helper function to build the list.
- **generateCoAdvisorList**: Generates a list of `CoAdvisors` from a list of IDs, ensuring no duplicates within the list. Uses a recursive helper function to build the list.
- **generateViva**: Generates a `Viva` instance by creating unique IDs for all roles (President, Advisor, Supervisors, CoAdvisors) and returns the `Viva` object along with the list of used IDs.
- **generateVivaList**: Generates a list of `Viva` instances of a specified size, ensuring each `Viva` has unique IDs for all roles.
- **Properties**:
    - **Each Viva in List doesn't have the same President and Advisor**: Validates that the President and Advisor roles in each `Viva` have unique IDs.
    - **No Supervisors have the same ID within a Viva**: Ensures that there are no duplicate Supervisor IDs within each `Viva`.
    - **No CoAdvisors have the same ID within a Viva**: Ensures that there are no duplicate CoAdvisor IDs within each `Viva`.
    - **Each Viva has unique IDs across President, Advisor, Supervisors, and CoAdvisors**: Confirms that all IDs within a `Viva` are unique across all roles.

### IDTest

- **generateID**: Generates a general `ID` with a prefix of either "T" for Teacher or "E" for External followed by a 3-digit number. Validates the correctness of the generated ID.
- **generateTeacherID**: Generates a `TeacherID` with a prefix "T" followed by a 3-digit number. Ensures the generated ID conforms to the Teacher ID format.
- **generateExternalID**: Generates an `ExternalID` with a prefix "E" followed by a 3-digit number. Ensures the generated ID conforms to the External ID format.
- **generateUniqueIDs**: Generates a list of unique `IDs` up to a specified maximum number, ensuring there are no duplicates.
- **generateTeacherIdThatsNotOnList**: Generates a `TeacherID` that is not in the provided list of IDs. Ensures unique ID assignment.
- **generateExternalIdThatsNotOnList**: Generates an `ExternalID` that is not in the provided list of IDs. Ensures unique ID assignment.
- **generateIDThatsNotOnList**: Generates a general `ID` that is not in the provided list of IDs. Ensures unique ID assignment.
- **Properties**:
    - **IDs are in the correct format**: Validates that all generated `IDs` conform to the expected format.
    - **Teacher IDs are in the correct format**: Ensures that all generated `TeacherIDs` are valid and conform to the expected format.
    - **External IDs are in the correct format**: Ensures that all generated `ExternalIDs` are valid and conform to the expected format.
    - **IDs are unique**: Checks that the generated list of `IDs` contains only unique entries, with no duplicates.
    - **Teacher IDs are unique**: Ensures that all `TeacherIDs` in the generated list are unique.
    - **External IDs are unique**: Ensures that all `ExternalIDs` in the generated list are unique.

### linkVivaWithResourceTest

- **generateVivaWithTeachersAndExternals**: Generates a `Viva` object along with lists of `Teacher` and `External` objects based on a given day. Ensures that IDs are properly assigned and do not overlap.
- **generateAValidAgendaViva**: Generates a list of `Viva` objects, lists of `Teacher` and `External` objects, and a duration, ensuring that all generated entities are valid and consistent with each other.
- **Properties**:
    - **all generated teacher and external IDs should be present in the list of IDs from Viva**: Ensures that all generated teacher and external IDs are included in the `Viva` object, maintaining consistency between the `Viva` and the resources.
    - **the lengths of generated lists should match the number of distinct IDs**: Verifies that the lengths of generated teacher and external ID lists match the number of distinct IDs, ensuring no duplication.
    - **Testing linkVivaWithResource**: A placeholder property to ensure that the `generateAValidAgendaViva` function works as expected, producing valid and consistent data.

### makeTheAlgorithmHappen

- **generatePossibleSchedule**: Generates a possible schedule (`ScheduleOut`) by creating a list of `PreViva` objects, calculating their duration, and generating a schedule using the `algorithm` function. Ensures that the scheduling algorithm produces valid outputs.
- **Properties**:
    - **Putting the generated PreVivas in the algorithm**: Ensures that the generated schedule does not have overlapping `PosViva` intervals. Converts `start` and `end` strings to `OTime` objects and verifies that no two intervals overlap, ensuring the correctness of the scheduling algorithm.

### PreVivaTest

- **generatePreVivaList**: Generates a list of `PreViva` objects along with an `ODuration`. Utilizes a valid agenda to create linked viva sessions and resources.
- **Properties**:
    - **PreViva objects are correctly linked**: Validates that all generated `PreViva` objects are correctly linked with resources.
    - **PreViva objects have valid duration**: Ensures that all generated `PreViva` objects conform to the specified duration format.
    - **PreViva lists are unique**: Checks that the generated list of `PreViva` objects contains only unique entries, with no duplicates.
    - **PreViva objects conform to requirements**: Ensures that all generated `PreViva` objects meet the necessary requirements and constraints.


### updateAvailabilitySlotsTest

- **createODuration**: Helper method to create an `ODuration` from hours, minutes, and seconds. Validates the format and correctness of the duration string.
- **nonEmptyAvailabilityList**: Generates a non-empty list of `Availability` objects, ensuring there is always at least one availability slot.
- **generateDuration**: Generates an `ODuration` by selecting random hours, minutes, and seconds, and ensures the generated duration is valid.
- **nonEmptyUsedSlotsList**: Generates a non-empty list of used availability slots, ensuring there is always at least one used slot.
- **updateAvailabilitySlots**: Property test that verifies the `updateAvailabilitySlots` function correctly updates availability slots based on used slots and a given duration. Ensures no overlaps in updated slots, all updated slots meet the minimum duration requirement, all used slots are reflected in the updated slots, original slots are included when there are no used slots, and all original availabilities are considered.
- **Properties**:
    - **No overlaps in updated slots**: Validates that the updated availability slots do not overlap with each other.
    - **Valid duration for all updated slots**: Ensures that all updated slots meet the minimum duration requirement.
    - **All used slots are reflected in updated slots**: Ensures that all used slots are properly accounted for in the updated slots.
    - **Original slots included when no used slots**: Verifies that original availability slots are included in the updated slots when there are no used slots.
    - **All availabilities are considered**: Ensures that all original availability slots are taken into account when updating the slots.