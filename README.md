# Datapipeline
# Pipeline Consists of various modules:

Webserver
ETL Jobs
Redshift Warehouse Module
Analytics Module

# Overview
Data is captured in real time from the webserver from three different websites. The data collected from the webserver API is consumed by Kafka. Then, it is timely moved to the Landing Bucket on AWS S3. ETL jobs are written in spark and scheduled in DataPipeline to run every 10 minutes. After that, data will be landed to AWS Redshift by using Glue as the datapipeline.

# ETL Flow
Data Collected from the API is consumed by kafka.
Data Pipeline consumed data from kafa and temporary stored data in EC2 instances.
Spark job is triggered which reads the data from working zone and apply transformation is processed by EMR on EC2 which is used to configure CPU, memory, storage, and networking capacity for the instances.
Dataset is repartitioned and land the processed data on s3 bucketss3 module.
Warehouse module of ETL jobs picks up data from the s3 bucket and stages it into the Redshift staging tables.
AWS Glue crawler is used to set up the ETL jobs. It is also used to create and to update one or more tables in AWS Glue Data Catalog by editing Scala scripts and to populate the tables by using data from the S3 Bucket.
AWS Glue jobs is used to load the glue tables from Amazon Glue to Amazon Redshift.
ETL job execution is completed once the Data Warehouse is updated.

# Environment Setup
Hardware Used
EMR - I used a 3 node cluster with below Instance Types:

m5.xlarge
4 vCore, 16 GiB memory, EBS only storage
EBS Storage:64 GiB

Redshift: 
For Redshift I used 2 Node cluster with Instance Types dc2.large

Setting Up DataPipeline
Project uses AWS CLI to submit spark jobs in a jar file which is stored in the S3 bucket. This setup does not automatically set up the jar file for Datapipeline to trigger. You can install by running below command:

aws s3 cp s3://bucketyname/filename 
spark-submit ./filename run
Finally, checkout the Connection for setting up connection to EMR and S3 Bucket. Sample spark code is provided in the scr folder. 

Setting up EMR
Spinning up EMR cluster is pretty straight forward. 

Finally, pyspark uses python2 as default setup on EMR. To change to python3, setup environment variables:

export PYSPARK_DRIVER_PYTHON=python3
export PYSPARK_PYTHON=python3
Copy the ETL scripts to EMR and we have our EMR ready to run jobs.

Setting up Redshift
AWS Guide can be followed to run a Redshift cluster or alternatively Redshift_Cluster_IaC.py Script can be used to create cluster automatically.


# Testing the Limits
The hotel's stored data in this project is used to test the ETL pipeline on heavy load.

To test the pipeline I used 11.4 GB of data from hotel's stored data which is to be processed every 10 minutes (including datapipeline + populating data into warehouse + running analytical queries) by the pipeline which equates to around 68 GB/hour and about 1.6 TB/day.

# Scenarios
Data increase by 100x. read > write. write > read

Redshift: Analytical database, optimized for aggregation, also good performance for read-heavy workloads
Increase EMR cluster size to handle bigger volume of data
Pipelines would be run on 6am daily. how to update dashboard? would it still work?
Glue crawler is scheduled to run every 10 minutes and can be configured to run every morning at 7 AM if required.
Data quality operators are used at appropriate position. In case of data pipleine failures email triggers can be configured to let the team know about pipeline failures.


