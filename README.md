# maps-iyi3-mstu

# Project Details

- Project Name - Sprint 5: Maps
- Contributors - Isaac Yi (iyi3) and Michael Tu (mstu)
  > [!WARNING] > **Estimated Total Completion Time: \_ hours total** (for both of us combined)
- Repo link: https://github.com/cs0320-f23/maps-iyi3-mstu

# Design Choices

One of the main choices that we had to make was how exactly we wanted to display the information. There was a lot that we could present and a lot that the user wanted to see. We ultimately decided that a split-screen website -- with the map on one half and the repl on the other -- would cater to our user's purpose the best.

The REPL half was basically the same in terms of front end. We did have to reduce some of it to fit in the page and change the accessibility, but the functionality still worked the same as in previous sprints. We also added new commands, such as load_json and search_json, as outlined by the user stories. Our load_json method basically would take in a json file and display its information on the map. One interesting choice that we made, however, was to automatically load the GeoJson. This way, there would always be some information that the user can see and interact with on the map. Our search_json takes in a keyword(s) and highlights on the map properties whose description contain the keyword.

# Testing

Testing for this project was extremely extensive. For one, we used much of the functionality from our previous sprints. We kept these tests all the same because we did not edit these but rather, we wanted to build upon them. Thus, in our REPL half, we knew that the commands that our users might use (search, broadband, load, view, etc...) all worked the same way as they did in our previous sprints.



# Errors/Bugs

No errors/bugs that we know of.

# How to Run
