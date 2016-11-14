

When successful, perform the following validations:

* Single document returned

* RepositoryUniqueId matches request

* DocumentUniqueId matches request

* MimeType matches metadata from test 12311

* Document hash, as calculated after the retrieve, matches value in metadata from test 12311

* HomeCommunityId matches configuration


==============================================
missing_home - show proper error (XDSMissingHomeCommunityId) returned when 
homeCommunityId not in request


