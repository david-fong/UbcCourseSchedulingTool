# UBC Course Scheduling Tool


### Brainstorming:

##### language:
I'm thinking either Java or Javascript. Javascript would make it very easy to fetch data from the web, be more accessible to users, and make it very easy to create the GUI. But, I'm _really_ leaning toward Java because it's natively strongly typed, and I think the goal is complicated enough that it would really benefit from- or even _needs_ that.
##### The goal:
Create a program where a user can input their program of study, the year, and their previously taken courses, along with optional preferences (more on this later), and courses they want to take or are required to take. If possible, it would be better to have information on required courses and allowed electives stored so the user doesn't need to input this, but we're not sure how best to do that yet. The program would output an ordere list of valid schedules meeting all their credit requirement, sorted by favorability according to any provided preferences.
##### Program Behaviour:
0. Get a collection of course requirements imposed on the user.
0. Fetch data on each of those course's prereqs and coreqs, and its avaliable sections' restricted seating.
0. Weed out courses where the user doesn't meet prereqs, and sections where user doesn't meet seating restrictions.
0. Create a collection of all conflict-free schedules.
0. Sort the conflict-free schedules by user preferences that indicate favorability.
##### Data Structures:
// TODO
