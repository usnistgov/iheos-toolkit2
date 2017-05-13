

When successful, perform the following validations:

<ul>
<li> Single document returned
<li> RepositoryUniqueId matches request
<li> DocumentUniqueId matches request
<li> MimeType matches metadata from test 12311
<li> Document hash, as calculated after the retrieve, matches value in metadata from test 12311
<li> HomeCommunityId matches configuration
</ul>

<p>
missing_home - show proper error (XDSMissingHomeCommunityId) returned when 
homeCommunityId not in request


