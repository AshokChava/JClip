JClip
-
Version 1.0

This is a clip hisotry table created using java 1.7 and Java DB(Derby).

How this works?
-
This tool has simple window with three tabs. 
Clips : This stores current clipboard values. Every day this gets cleared, the old clips gets stored in ClipHistoryTable, suffixed with date like ClipHistory_3_11_14
ClipHistory: This stores old clipHistory value based on the date.
Old Clips: The clips from the ClipHistory are shown in this tab. Note the change of the tab title.

Where these Clips gets saved?
-
These clips are stroed in new database that gets created under <user.home>/.clipHistory. 

Features in next releases?
-
- Way to store the DB files in dropbox or some other cloud file store, instead of local disks.
- Feature to create and save notes along with clips


