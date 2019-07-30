# :date: UBC Course Scheduling Tool


### :mortar_board: The goal:
Create a program where a user can input their program of study, the year, and their previously taken courses, along with optional preferences (more on this later), and courses they want to take or are required to take. The program outputs an ordered collection of valid schedules meeting all their credit requirements, sorted by favorability according to any provided user preferences.

Ex. [Electives for CPEN](https://www.ece.ubc.ca/sites/default/files/CPEN%20-%202018%20May.pdf)

### :file_folder: Google Drive Folder:
[link](https://drive.google.com/drive/folders/1BmgHv7Mdu5VeI8_ZaramyXntM39VEjx8 "open for collaborators")

---

### :cd: Program Behaviour:
(Please see [lower-level procedure (WIP)](Core/source/org/bse/core/registration/scheduler/SchedulerMonkey.java))

1. Get a collection of course requirements imposed on the user.
1. Fetch data on each of those course's prereqs and coreqs, and its available sections' restricted seating.
1. Weed out courses where the user doesn't meet prereqs.
1. Create failure-type groups including each course that failed that requirement-type (ex. seating, is a waitlist, prerequisite), and sort these groups internally by how close they were to meeting the requirements. Allow the user to select failures to ignore and consider in further operations, and make sure to warn if a section failed for multiple reasons.
1. Create a collection of all courses that the user cared about that still have sections that they could register in. Sort this collection by restrictiveness (ex. number of usable sections).
1. Create a collection of all conflict-free schedules by trying combinations using a tree structure where nodes at a common level represent the addition of a course section block (corresponding to that level and its parent course's order in the sorted collection from the previous step) in an attempt to meet a program-requirement.
1. Sort the conflict-free schedules by user preferences that indicate favorability.

### :monkey: Data Structures:
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

### :spider: Data Data Data:
- Please see [Spider.java](Data/source/org/bse/data/spider/Spider.java).

---

### :oncoming_automobile: High-level Roadmap:
1. Planning and Learning:
   - Everyone: list out their gripes with UBC's web registration interface, and how they propose to address them.
   - Everyone: understand what the requirement system is used for.
1. Groundwork Representation:
   - Implement all of the representation and algorithms for Requirements while writing and performing tests.
1. Fetching, Parsing, and Storing Data:
   - Work on the spiders (fetch html from ubc's registration pages)
1. GUI Design and Implementation:
   - Design (as in sketch and not implement (yet)) the Graphical user interface.
   - Implement the GUI
