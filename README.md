
#DOH death and Birth data Project

-spring boot 2.0.2.RELEASE

-to clone from gitLab: https://git.dts.utah.gov/zguler/springboot_XML_MySQL_Postgres_JDBCTemplate.git

-postman test:
			port [:7771] is using tunnelling to access 'mpi-api' and 'mpi-db-pg' on the test server				
			port [:5151] is using tunnelling locally
			
	[GET]	http://localhost:5151/hello
			http://localhost:5151/topics
			http://localhost:5151/db/getMYUser
			http://localhost:5151/db/getPGUser
	[POST]	http://localhost:5151/audit/test
			http://localhost:5151/fhir/Patient
			
			http://localhost:5151/fhir/Patient?given=Ruth&_format=json
			
			http://172.23.167.15:5151/hello

			http://localhost:5151/fhir/Patient?identifier=2016000018	
			http://localhost:5151/fhir/Patient?identifier=2016000018&_format=xml
			http://localhost:5151/fhir/Patient?given=Ruth
			http://localhost:5151/fhir/Patient?family=Arnold
			http://localhost:5151/fhir/Patient?family=Mangum&birthdate=1920-04-26&_format=xml
		
					
	[exceptions]				
			http://localhost:5151/fhir/Patient?given=Zakir&_format=xml	<==== 0 rows returned		
			http://localhost:5151/fhir/xPatient?given=Ruth&_format=json <==== invalid resource type "xPatient"
			http://localhost:5151/fhir/Patient?xxxgiven=Ruth&_format=json <== invalid parameter "ddd", "xxxgiven"

			
	[TEST_DEV]			
			https://mpiapi.dev.utah.gov/manager/html/			
			https://mpiapi.dev.utah.gov/dohapi/hello	<=========== 'hello' is a testing endpoint.					
			https://mpiapi.dev.utah.gov/dohapi/getMYUser
			https://mpiapi.dev.utah.gov/dohapi/db/getPGUser
			https://mpiapi.dev.utah.gov/dohapi/audit
			https://mpiapi.dev.utah.gov/dohapi/db/...
			https://mpiapi.dev.utah.gov/dohapi/topics
			
			https://mpiapi.dev.utah.gov/dohapi/fhir/Patient?identifier=2016000018
			https://mpiapi.dev.utah.gov/dohapi/fhir/Patient?identifier=2016000018&_format=xml
			https://mpiapi.dev.utah.gov/dohapi/fhir/Patient?given=Ruth
			https://mpiapi.dev.utah.gov/dohapi/fhir/Patient?family=Arnold
			https://mpiapi.dev.utah.gov/dohapi/fhir/Patient?family=Xtest
			


-DB: 	MySQL
		Postgres

-JPA
-JDBCTemplate

-returns XML. JSON




