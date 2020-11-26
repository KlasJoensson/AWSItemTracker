This is a simple application for tracking items, with the help of AWS
It is made by looking at this tutorial:
https://github.com/awsdocs/aws-doc-sdk-examples/tree/master/javav2/usecases/creating_secure_spring_app

To be able to get emails with a sandbox account you need to verify the emails you want to send to/from in the AWS SES.
You also need to create a database with the Amazon RDS MySQL DB instance to get the application running. See /src/main/resources/static/database.sql for info about the database setup.

My version of this application differs in some small and some lager ways from the one in the tutorial. 
Some of the differences are:
 * Added a SQL-script for the database 
 * Sets different settings from the properties file instead of having them in the code