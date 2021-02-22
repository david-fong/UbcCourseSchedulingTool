# :date: UBC Course Scheduling Tool

> One does not simply register for courses at UBC.
>
> -Me, 2019



## :mortar_board: The Motivation for this Project:

Registering for my third-year courses was a frustrating process. Sources on course requirements were inconsistent. [UBC's main site](https://you.ubc.ca/ubc_programs/computer-engineering/) had an incomplete listing, and so did [the ECE department's site](https://www.ece.ubc.ca/academic-programs/undergraduate/programs/computer-engineering-program). I could only find an explanation of one category of requirements on [UBC's degree navigator](https://degree-navigator.as.it.ubc.ca "link broken"), which was cluttered with ~400 other highly specific requirements, and I could only find information on elective requirements in [The ECE electives document](https://www.ece.ubc.ca/sites/default/files/CPEN%20-%202018%20May.pdf), which included some courses that were no longer offered, and other courses only for Electrical students and not Computer Engineering students (because of the two being under the ECE department). There was no holistic view making indications of those limitations.

Most of my time making worklists was spent finding out what I could actually use, and how not to end up in a dead end. I ended up having a very limited number of choices for Worklists, despite making them manually instead of basing them off of standard timetables. I kept thinking to myself- "a computer could do this". As in- a computer could filter out courses and sections that I don't meet requirements for, and not as in- a computer could fix these inconsistencies, inconveniences, and dated pieces of information that form another face of the problem. In the process of working on this project, I've found and been impressed by several other similar projects. But- in my own way, I want to try to go further.



## :mount_fuji: Goals

1. Automate worklist creation. There are three levels of thoroughness when it comes to solving this problem layer:
   - Check that a worklist is valid. That is- a student meets all requirements to register for each section of each course in the worklist, there are no scheduling conflicts, and all contained sections have remaining seating available. This is the level that UBC currently goes to with their registration site and the degree navigator site.
   - Generate valid worklists given a set of course sections. This is the level that two existing projects, [UBC Course Scheduler - Yichen](https://github.com/Schemetrical/UBCScheduler), and [UBC Course Schedule Creator - Nick Wu](https://github.com/smart-cs) go to.
   - Generate valid worklists given a a student's program of study, year of study, and elective preferences. This is the level that this project aims to go to- and perhaps to go beyond.
1. Create an algorithm to define course sections (which currently is done by individuals) that minimizes course section conflicts for the vast majority of students. This would be like writing a program to generate a Sudoku *puzzle*- a unique solution that remains to be solved by a user, and not just any puzzle, but one that can be proven to be difficult to solve, which- believe me- is not a simple thing to do. It requires learning how people who are good at Sudoku play Sudoku, reasoning about what makes a difficult puzzle difficult, and much more reasoning to reliably reproduce that effect in a reasonable amount of time. (I don't speak from experience). Of course- in our case, we would like to define course sections in a way that makes it easy for students to create worklists (Ie. that minimize encounters with scheduling conflicts). This is a dream for this project. Something that I would love to approach and attempt, but also that I don't know if I can find the time to do with love and care (or even without love and care).



## :cd: High-level Algorithm Description:

1. Begin with complex program requirements imposed on the user such as those in the [The ECE electives document](https://www.ece.ubc.ca/sites/default/files/CPEN%20-%202018%20May.pdf), and a set of user-preferences such as "Earliest class time", or "Try to fit in an hour long lunch break everyday".
1. Weed out courses where the user doesn't meet the course's requirements (ie. prereqs, coreqs, student-reqs). Allow the user to select unusable courses to override to still consider in further operations. See [`Requirement::requireOf`](ucst-utils/src/main/java/com/dvf/ucst/utils/requirement/Requirement.java).
1. Create a collection of all combinations of Courses that satisfy the complex program requirements. See [`MatchingRequirementIf::getAllBarelyPassingCombinations`](ucst-utils/src/main/java/com/dvf/ucst/utils/requirement/matching/MatchingRequirementIf.java).
1. Create a collection of all conflict-free Worklists (containing *CourseSections* and not their *Courses*) for each of the previously generated Course combinations by trying all combinations of their CourseSections together (short circuiting upon encountering conflicts to avoid wasteful computation). See [`PickyBuildGenerator::generateAllFullPickyBuilds`](ucst-utils/src/main/java/com/dvf/ucst/utils/pickybuild/PickyBuildGenerator.java).
1. Sort the conflict-free Worklists by their adherence to the user's preferences.

## :monkey: Data Structures:
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



## :spider: Data:
- Please see [`The Spiders`](ucst-core/src/main/java/com/dvf/ucst/core/spider/Spider.java) for the classes which will pull registration data on Courses, their Sections, and STT's from UBC's public pages on their registration site.
- See [`The FacultyNodes`](ucst-core/src/main/java/com/dvf/ucst/core/faculties) for the constants that define the URL's for all the necessary UBC registration site pages that the Spiders will pull from, and that define the file structure for the local cache of that data in xml format.



### :file_folder: Google Drive Folder: [link](https://drive.google.com/drive/folders/1BmgHv7Mdu5VeI8_ZaramyXntM39VEjx8 "open for collaborators")
