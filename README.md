# maps-iyi3-mstu

# Project Details

- Project Name - Sprint 5: Maps
- Contributors - Isaac Yi (iyi3) and Michael Tu (mstu)
  > [!WARNING] > **Estimated Total Completion Time: 45 hours total** (for both of us combined)
- Repo link: https://github.com/cs0320-f23/maps-iyi3-mstu

# Design Choices

One of the main choices that we had to make was how exactly we wanted to display the information. There was a lot that we could present and a lot that the user wanted to see. We ultimately decided that a split-screen website -- with the map on one half and the repl on the other -- would cater to our user's purpose the best.

The REPL half is basically the same in terms of front end. We did have to reduce some of it to fit in the page and change the accessibility, but the functionality still works the same as in previous sprints. We also added new commands, such as load_json and search_json, as outlined by the user stories. Our load_json method basically would take in a json file and display its information on the map. One interesting choice that we made, however, was to automatically load the GeoJson when users did not provide a file or when the filepath was invalid. This way, there would always be some information that the user can see and interact with on the map. Our search_json takes in a keyword(s) and highlights on the map properties whose description contain the keyword. 

Additionally, we allowed developers to filter the property data by allowing them to enter a minimum/maximum latitude/longitude that they wanted to focus on. We decided to cache these results, as they can be large and may often be reused by the same user.

On the map portion of the site, there is a lot that we thought of for our user. For starters, we can see the redlining overlays (from the gearup). When users would do search_json in the repl portion of the site, areas with descriptions that contained the keyword would be highlighted in bright pink so that our users would know which areas to look at. Additionally, we alloed users to click around the map. This would display the State, City, Holc_grade (redlining score), name, and broadband percentage. This way, our user is able to see all this information at once.

We also made a few choices regarding accessibility. Users are able to tab around the site, scroll through REPL history, enter commands, and navigate around the map, all without having to use their trackpad. Our voiceover reads whichever portion of the site the user is on and outputs (if that is what the users is looking at).



# Testing

Testing for this project was extremely extensive. For one, we used much of the functionality from our previous sprints. We kept these tests all the same because we did not edit these but rather, we wanted to build upon them. Thus, in our REPL half, we knew that the commands that our users might use (search, broadband, load, view, etc...) all worked the same way as they did in our previous sprints.

In terms of the map, one of the things that we tested was for the clicking functionality. We made sure that the clicks would produce the correct responses and matched what we expected to see. We did not test for overlays.

In the front end, many of the tests were built off of each other. For the integration testing, we made sure to combine many series of feasible commands a user might use, such as load csv, load json, view csv, search on the map.

We also made sure to do end-to-end testing, ensuring that our backend functionality matched with our front end results. 


# Errors/Bugs

No errors/bugs that we know of.

# How to Run

1. Make sure that both the backend server and the front end server are running
2. In the front end, we can enter commands in the command box
3. Commands for CSV are load {[filepath]}, view, search (column {optional}) (targetString)
4. json commands are load_json {[filepath]} or search_json{[keyword]}
5. click on the map to see a display of the state, city, holc grade, broadband percentage, and name
6. Tests: Run the playwright tests with the command npx playwright test
