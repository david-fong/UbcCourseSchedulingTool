# UBC Course Scheduling Tool


### Brainstorming:

##### The goal:
Create a program where a user can input their program of study, the year, and their previously taken courses, along with optional preferences (more on this later), and courses they want to take or are required to take. If possible, it would be better to have information on required courses and allowed electives stored so the user doesn't need to input this. The program would output an ordered collection of valid schedules meeting all their credit requirements, sorted by favorability according to any provided user preferences.

##### Program Behaviour:
(Please see [lower-level procedure (WIP)](Core/source/org/bse/core/registration/scheduler/SchedulerMonkey.java))

0. Get a collection of course requirements imposed on the user.
0. Fetch data on each of those course's prereqs and coreqs, and its available sections' restricted seating.
0. Weed out courses where the user doesn't meet prereqs.
0. Create failure-type groups including each course that failed that requirement-type (ex. seating, is a waitlist, prerequisite), and sort these groups internally by how close they were to meeting the requirements. Allow the user to select failures to ignore and consider in further operations, and make sure to warn if a section failed for multiple reasons.
0. Create a collection of all courses that the user cared about that still have sections that they could register in. Sort this collection by restrictiveness (ex. number of usable sections).
0. Create a collection of all conflict-free schedules by trying combinations using a tree structure where nodes at a common level represent the addition of a course section block (corresponding to that level and its parent course's order in the sorted collection from the previous step) in an attempt to meet a program-requirement.
0. Sort the conflict-free schedules by user preferences that indicate favorability.

##### Data Structures:
- Courses:
  - Have a faculty
  - Impose prerequisites and co-requisites (ex. labs and tutorials)
- Course Sections:
  - Have a lecturer
  - Have a semester
  - Have blocks (meeting places + time enclosures)
  - Have complex seating restrictions
- Requirements:
  - Logical variants (like AND or OR) that require other requirements
  - Matching requirements that require a test subject to contain matches of candidates

##### Data Data Data:
- Please see [Spider.java](Data/source/bse/data/utils/Spider.java).
