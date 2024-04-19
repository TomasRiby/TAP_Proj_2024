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

This repository serves as the central hub for both the documentation and the source code of the project crafted for the "Técnicas Avançadas de Programação" (Advanced Programming Techniques) course, which is part of the Master’s in Computer Engineering curriculum at the Instituto Superior de Engenharia do Porto (ISEP).
The challenge at hand involves the intricate scheduling of Master of Science (MSc) dissertation defenses, also known colloquially and in academic terms as "viva," from the Latin "viva voce," meaning "by live voice." This scheduling endeavor includes multiple stakeholders, each viva needing to be arranged at the most opportune time within the confines of existing scheduling constraints. Moreover, within a specified time frame, numerous vivas need coordination, with various participants often being involved in multiple defenses.

This problem is classified as NP-hard, indicating that in its unconstrained form, it is intractable. However, applying specific constraints related to the availability of the involved entities (resources) can render the problem manageable, or "tractable." The initial phase of this project, which marks the first milestone, will start by addressing only a subset of these constraints. As the project progresses through its phases, particularly in milestones M1 and M3, it will gradually incorporate more constraints and refine the algorithms designed to optimize the scheduling process. These algorithms aim to progressively enhance the quality and feasibility of the scheduling solutions, illustrating a methodical approach to tackling this complex problem.

## Domain Model

![img.png](./assets/domain-v3.png)

The domain was created based on an analysis conducted on the input XML files for the problem's use case.
The domain classes consist of Agenda, Viva, and Resources, which comprises a list of Teachers and Externals.

Both Teachers and Externals have availabilities that include start and end datetime, as well as the subject's preferences.
The Resources class was introduced to combine both Externals and Teachers, providing a unified structure for managing resource's availability.


## Objective
The main objective of this work is the design and development of an application using functional
programming techniques.

## Milestones Development Process

### MS01
#### Overview (Main Decisions)
The initial milestone of our project was developed using the Test Driven Development (TDD) methodology, a strategy particularly beneficial at the onset when the domain model concepts were still being defined and no code base existed. 
To foster robust development and minimize potential bugs from the outset, we prioritized writing unit tests that verified the accuracy and stability of our domain model before beginning the actual coding. 
Starting with these tests allowed us to mitigate possible bugs and clearly define the expected functionality of the code. 
This approach not only facilitated a clearer development process but also helped maintain the integrity of the domain model throughout the project’s progression. 
By continuously updating and referring to our comprehensive suite of unit tests, we were able to quickly identify and rectify defects arising from recent changes, ensuring a stable and reliable codebase.

The Classes were created in the `io` package for reading XML files and loading data into memory. 
To separate responsibilities and ease maintenance in case of issues, a class was created for each context, such as `AgendaIO`, `VivaIO`, and finally `ResourceIO`, which encompasses the concepts of teachers and externals.

The decision to group the concepts of teachers and externals was made due to the similarity in their data structure.
Consolidating them into a single Resource simplifies computing their data for the final goal of the proposed problem, as there's no need to compare between two different data structures.

As for the algorithm, it...ß
#### Possible alternatives

#### Possible future improvements

As we move forward, several areas stand out for enhancement.

- **Incorporation of the role `Co-advisor`**: In the future the team intends to incorporate the role `Co-advisor` in the algorithm, since it was forgotten and this fail was noticed too late to act on it.

- **Implementation of More Tests**: While our current test suite provides valuable coverage, more test will fortify the reliability of our software. Introducing functional tests to validate interactions between components will further validate the correctness of our codebase.

- **Improvement of Project Structure**: Enhancing the organization and clarity of our project structure will improve development processes and facilitate collaboration among team members.

- **Optimization of Code**: Identifying and addressing certain code duplication and inefficiencies within our codebase will enhance the efficiency and readability of our application.

- **Creation of More Input Test Files**: Increasing our collection of input test files with more diverse agendas will enhance the comprehensiveness of our testing efforts. Also, with this, we can uncover more error cases that might otherwise go unnoticed, bolstering the resilience and adaptability of our software.

By prioritizing these among other possible improvements, we aim to elevate the quality, reliability, and performance of our application project, allowing us to deliver a solution that exceeds expectations and withstands the demands of evolving requirements. 