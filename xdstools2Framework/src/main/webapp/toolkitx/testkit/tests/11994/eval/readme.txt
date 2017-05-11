

Step validate_original - verify the original DocumentEntry (had transformation added to it) from submit/submit has status Approved (xfrm/xfrm (adding transformation) didn't change the status)
	
Step validate_xfrm - verify transformed DocumentEntry from xfrm/xfrm is present and has status Approved
	
Step no_validate_rplc_xfrm - verify metadata from xfrm/xfrm_rplc was not stored (cannot add transformation to deprecated DocumentEntry)

