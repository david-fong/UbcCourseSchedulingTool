# :date: UBC Course Scheduling Tool


### :mortar_board: The goal:

Create a program (and wrapping desktop application) where a user can provide their program of study, and a collection of courses they have completed, and be returned with a collection of Worklists for their upcoming session that meet all their program requirements. As stated in this project's repo description, "One does not simply register for courses at UBC"- *but*, it shouldn't have to be that way. There are really exciting developments and efforts being made by UBC in this area [(The Scheduling Project)](https://facultystaff.students.ubc.ca/enrolment-services/scheduling-records-systems-management/scheduling-services/scheduling-project) that perhaps this project can supplement as a mature proof-of-concept for scheduling automation.

1. UBC has all the information needed to validate a worklist. They could automate it, but they haven't yet. Efforts have been made with great results by others [Ex. UBCScheduler by Yichen](https://yichen.dev/UBCScheduler/), but not to the extent of effort that *this* projects sets out for.
1. Sources of course requirements are inconsistent. For the CPEN program, one is incomplete [(UBC's main site)](https://you.ubc.ca/ubc_programs/computer-engineering/), one is mixed with courses that are only for ELEC students- due to CPEN being lumped up with it as ECE [(The ECE electives document)](https://www.ece.ubc.ca/sites/default/files/CPEN%20-%202018%20May.pdf), one could use a css update [(the ECE department's site)](https://www.ece.ubc.ca/academic-programs/undergraduate/programs/computer-engineering-program), one on top of that is *incredibly, incredibly* cluttered [(UBC's degree navigator)](https://degree-navigator.as.it.ubc.ca "link broken"). **None of them completely agree with each other**. This is a problem that needs to be solved by effort and communication- not by algorithms. It is not a goal for this project to solve, but remains as much of a hope as for the other goals that it will be brought attention to.

That is the public face of the goal- but there is also a personal side: I want to practice managing a Java project after being a participant in one during my first COOP term, and to struggle through the process of searching for libraries, choosing them, and learning how to use them. This is in opposition to how my school assignments spoon-feed me structure. Through this, I hope to thoroughly challenge myself, demonstrate my ability to learn independently, and in the process create something that can help many people. My head is in the clouds- but [wouldn't it be lovely](https://www.youtube.com/watch?v=q5fW7sERw7I&t=4m24s)?

### :file_folder: Google Drive Folder: [link](https://drive.google.com/drive/folders/1BmgHv7Mdu5VeI8_ZaramyXntM39VEjx8 "open for collaborators")

---

### :cd: High-level Algorithm Description:

1. Begin with complex program requirements imposed on the user such as those in the [The ECE electives document](https://www.ece.ubc.ca/sites/default/files/CPEN%20-%202018%20May.pdf), and a set of user-preferences such as "Earliest class time", or "Make sure there is a lunch break everyday".
1. Weed out courses where the user doesn't meet the course's requirements (ie. prereqs, coreqs, student-reqs). Allow the user to select unusable courses to override to still consider in further operations. See [`Requirement::requireOf`](Utils/source/com/dvf/ucst/utils/requirement/Requirement.java).
1. Create a collection of all combinations of Courses that satisfy the complex program requirements. See [`MatchingRequirementIf::getAllBarelyPassingCombinations`](Utils/source/com/dvf/ucst/utils/requirement/matching/MatchingRequirementIf.java).
1. Create a collection of all conflict-free Worklists (containing *CourseSections* and not their *Courses*) for each of the previously generated Course combinations by trying all combinations of their CourseSections together (short circuiting upon encountering conflicts to avoid wasteful computation). See [`PickyBuildGenerator::generateAllFullPickyBuilds`](Utils/source/com/dvf/ucst/utils/pickybuild/PickyBuildGenerator.java).
1. Sort the conflict-free Worklists by their adherence to the user's preferences.

### :monkey: Data Structures:
- Faculties/Departments:
  - Have a collection of offered courses
  - May have a collection of STT's for their programs
  - May have other Faculties/Departments listed under them
- Courses:
  - Belong to a Faculty/Department
  - Impose prerequisites and co-requisites (ex. labs and tutorials)
  - Impose student-based requirements such as year of study or program
  - Have offered Sections in the categories of Lectures, Labs, and Tutorials
- Course Sections:
  - Have a lecturer
  - Have a semester
  - Have blocks (meeting places + time enclosures)
  - Have complex seating restrictions and availabilities
  - If of the Lecture type, may have required Lab and/or Tutorial options to take together
- Requirements:
  - Logical variants (like AND or OR) that require other requirements to pass in a certain fashion
  - Matching requirements that require a test subject to contain matches of candidates

### :spider: Data Data Data:
- Please see [Spider.java](Data/source/com/dvf/ucst/data/spider/Spider.java).

---

### :oncoming_automobile: High-level Roadmap:
1. Planning and Learning:
   - List out gripes with UBC's registration site's interface, and how they propose to address them.
1. Groundwork Representation:
   - Implement all of the representation and algorithms for Requirements while writing and performing tests.
1. Fetching, Parsing, and Storing Data:
   - Work on the spiders (fetch html from ubc's registration pages and save as local xml files)
1. GUI Design and Implementation:
   - Design the layout of the GUI.
   - Implement the GUI.
