This is a note file to understand how the system works


The "Black box" is the system the runs the entire simultion in the background
this includes:
- converting the move jsons to objects
- ordering the moves
- playing out the moves and there effects
- handle mid turn monster changes
- converting the final result of the turn into a json to read the results

the "Front Face" of the black box will depend on the implementation. 
We can enclose the black box idea by passing in the TurnInfoPackage into the box aswell. That way 