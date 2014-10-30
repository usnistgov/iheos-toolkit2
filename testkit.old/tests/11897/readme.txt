SQ.b FindDocuments Stored Query

Stored Query must be run over SOAP 1.2.  

This testplan contains many many test steps each validating 
a feature of the FindDocuments stored query.  This test relies
on test 12346 to pre-load the Registry with known test data.

The test steps are:

and:
   tests AND logic in SQ
   eventCodeList having both T-D4909  AND  T-62002
   must return single document

classcode_one: 
   queries for: 
      classCode = 'Consult'
      status = 'Approved'
   must return: DocD

classcode_practicesetting:
  queries for:
    classCode = 'Communication'
    practiceSettingCode = 'Cardiology'
    status = 'Approved'
  must return: DocB

classcode_scheme_2: 
   queries for:
     classCode = ('Communication', 'Communication')
     classCodeScheme = ('Connect-a-thon classCodes','Connect-a-thon classCodes 2')
     status = 'Approved'
   must return: DocB, DocC

classcode_scheme_mismatch:
  queries for:
      classCode = 'Communication'
      classCodeScheme = ('Connect-a-thon classCodes', 'a') 
      status = 'Approved'
  must return: none (mismatch on codes and scheme)

classcode_two: 
   queries for: 
      classCode = 'Consult' or 'History and Physical'
      status = 'Approved'
   must return: DocD, DocE, DocA

confcode:
  queries for:
    confidentialityCode = ('1.3.6.1.4.1.21367.2006.7.101',
                                    '1.3.6.1.4.1.21367.2006.7.103')
    status = 'Approved'
  must return: DocB, DocC

creationtime_between:
  queries for:
    creationTimeFrom: 20040101
    creationTimeTo:  20050101
    status = 'Approved'
  must return: DocB

creationtime_left_edge:
  queries for:
    creationTimeFrom: 20041224
    creationTimeTo:  20050101
    status = 'Approved'
  must return: DocB

creationtime_right_edge:
  queries for:
    creationTimeFrom: 20041124
    creationTimeTo:  20041224
    status = 'Approved'
  must return: none

creationtime_practicesetting:
  queries for: 
    creation time:  20020101 thru 20060101
    practiceSettingCode: 'Dialysis'
    status = 'Approved'
  must return: DocD

deprecated:
  queries for:
    status = 'Deprecated'
  must return: DocE

eventcode:
  queries for:
    eventCodeList = 'Colonoscopy'
    status = 'Approved'
  must return: DocB

eventcode_scheme:
  queries for:
    eventCodeList = 'Colonoscopy'
    eventCodeList = 'Connect-a-thon eventCodeList'
    status = 'Approved'
  must return: DocB

formatcode:
  queries for:
    formatCode = 'CDAR2/IHE 1.0'
    status = 'Approved'
  must return: DocA, DocB, DocF, DocC

hcftc:
  queries for:
    healthcareFacilty: Outpatient
    status: 'Approved'
  must return: DocA, DocF, DocD

hcftc_scheme:
  queries for:
    healthcareFacilty: Outpatient
    healthcareFaciltyScheme: 'Connect-a-thon healthcareFacilityTypeCodes'
    status: 'Approved'
  must return: DocF, DocD

leafclass: 
   queries for:
      all approved documents, DocE is deprecated so not returned
   returns LeafClass   
   must return: DocA, DocB, DocC, DocD, DocF

no_matching_classcode:
   queries for class code not contained in test data
   must return: none

object_refs: 
   queries for:
      all approved documents, DocE is deprecated so not returned
   returns ObjectRefs   
   must return: DocA, DocB, DocC, DocD, DocF

old_scheme:
   query using old format.  must return error since code not in CE format

practicesetting:
  queries for:
    practiceSettingCode = 'Dialysis'
    status = 'Approved'
  must return: DocA, DocF, DocD

practicesetting_scheme:
  queries for:
    practiceSettingCode = 'Dialysis'
    practiceSettingCodeScheme = 'Connect-a-thon practiceSettingCodes'
    status = 'Approved'
  must return: DocA, DocD

  NOTE: there is a parameter $XDSDocumentEntryPracticeSettingCodeScheme
  in this stored query.  This is not a mistake.   Extra parameters
  must be accepted and ignored by the stored query request parser.
  This parameter is no longer valid given the Code Type parameter
  format change but the parameter must still be accepted (and ignored)
  because of this ebRS rule.

practicesetting_two:
  contains duplicate $XDSDocumentEntryPracticeSettingCode
  must return: error

servicestarttime:
  queries for:
    serviceStartTimeFrom: 2005
    serviceStartTimeTo: 2006
    status = 'Approved' 
  must return: DocC, DocD

servicestoptime:
  queries for:
    serviceStopTimeFrom: 2005
    serviceStopTimeTo: 2006
    status = 'Approved' 
  must return: DocC, DocD




