R.a - Add by-reference to submission set

Approach #1 (directory submit1)

Submit document in submission set

Submit new submission set (with a different patient id) with Reference 
association to first document

Submit new submission set with Reference association to non-existant
document, must fail

Submit new submission with Reference association to document in same submission, must fail.

Submit new submission with Original association to document in first submission, must fail.




Approach #2  (directory submit2)
	
Submit submission set with document

Submit document in submission set that adds itself to first submission set

Submit document in submission set that attempts to add itself to non-existant submission set, must fail




Approach #3 (not available yet)
	
Submit Submission set

Submit document in submission set

Submit association (in submission set) that adds document to first submission set
	
