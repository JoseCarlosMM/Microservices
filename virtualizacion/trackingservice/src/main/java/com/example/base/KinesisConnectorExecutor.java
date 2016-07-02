/*
 * Copyright 2013-2014 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Amazon Software License (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 * http://aws.amazon.com/asl/
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.example.base;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.amazonaws.auth.AWSCredentials;
import com.example.utils.KinesisUtils;
import com.example.utils.RedshiftUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.RegionUtils;
import com.amazonaws.services.cloudformation.AmazonCloudFormation;
import com.amazonaws.services.cloudformation.AmazonCloudFormationClient;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.kinesis.connectors.KinesisConnectorConfiguration;
import com.amazonaws.services.kinesis.connectors.KinesisConnectorExecutorBase;
import com.amazonaws.services.s3.AmazonS3Client;

/**
 * This class defines the execution of a Amazon Kinesis Connector.
 * 
 */
public abstract class KinesisConnectorExecutor<T, U> extends KinesisConnectorExecutorBase<T, U> {

    private static final Log LOG = LogFactory.getLog(KinesisConnectorExecutor.class);

    // Create AWS Resource constants
    private static final String CREATE_KINESIS_INPUT_STREAM = "createKinesisInputStream";
    private static final String CREATE_KINESIS_OUTPUT_STREAM = "createKinesisOutputStream";
    private static final String CREATE_DYNAMODB_DATA_TABLE = "createDynamoDBDataTable";
    private static final String CREATE_REDSHIFT_CLUSTER = "createRedshiftCluster";
    private static final String CREATE_REDSHIFT_DATA_TABLE = "createRedshiftDataTable";
    private static final String CREATE_REDSHIFT_FILE_TABLE = "createRedshiftFileTable";
    private static final String CREATE_S3_BUCKET = "createS3Bucket";
    private static final String CREATE_ELASTICSEARCH_CLUSTER = "createElasticsearchCluster";
    private static final boolean DEFAULT_CREATE_RESOURCES = true;

    // Create Amazon DynamoDB Resource constants
    private static final String DYNAMODB_KEY = "dynamoDBKey";
    private static final String DYNAMODB_READ_CAPACITY_UNITS = "readCapacityUnits";
    private static final String DYNAMODB_WRITE_CAPACITY_UNITS = "writeCapacityUnits";
    private static final Long DEFAULT_DYNAMODB_READ_CAPACITY_UNITS = 1l;
    private static final Long DEFAULT_DYNAMODB_WRITE_CAPACITY_UNITS = 1l;
    // Create Amazon Redshift Resource constants
    private static final String REDSHIFT_CLUSTER_IDENTIFIER = "redshiftClusterIdentifier";
    private static final String REDSHIFT_DATABASE_NAME = "redshiftDatabaseName";
    private static final String REDSHIFT_CLUSTER_TYPE = "redshiftClusterType";
    private static final String REDSHIFT_NUMBER_OF_NODES = "redshiftNumberOfNodes";
    private static final int DEFAULT_REDSHIFT_NUMBER_OF_NODES = 2;

    // Create Amazon S3 Resource constants
    private static final String S3_BUCKET = "s3Bucket";

    // Elasticsearch Cluster Resource constants
    private static final String EC2_ELASTICSEARCH_FILTER_NAME = "tag:type";
    private static final String EC2_ELASTICSEARCH_FILTER_VALUE = "elasticsearch";

    // Create Stream Source constants
    private static final String CREATE_STREAM_SOURCE = "createStreamSource";
    private static final String LOOP_OVER_STREAM_SOURCE = "loopOverStreamSource";
    private static final boolean DEFAULT_CREATE_STREAM_SOURCE = false;
    private static final boolean DEFAULT_LOOP_OVER_STREAM_SOURCE = false;
    private static final String INPUT_STREAM_FILE = "inputStreamFile";

    // Class variables
    protected final KinesisConnectorConfiguration config;
    private final Properties properties;

    /**
     * Create a new KinesisConnectorExecutor based on the provided configuration (*.propertes) file.
     * 
     * @param configFile
     *        The name of the configuration file to look for on the classpath
     */
    String content;
    public KinesisConnectorExecutor(String configFile, String content) {
        this.content= content;
        InputStream configStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(configFile);

        if (configStream == null) {
            String msg = "Could not find resource " + configFile + " in the classpath";
            throw new IllegalStateException(msg);
        }
        properties = new Properties();
        try {
            properties.load(configStream);
            configStream.close();
        } catch (IOException e) {
            String msg = "Could not load properties file " + configFile + " from classpath";
            throw new IllegalStateException(msg, e);
        }
        AWSCredentialsProvider prov =new AWSCredentialsProvider() {
            @Override
            public AWSCredentials getCredentials() {
               return new AWSCredentials() {
                   @Override
                   public String getAWSAccessKeyId() {
                       return "AKIAJMOK3KHKIRPDXQBA";
                   }

                   @Override
                   public String getAWSSecretKey() {
                       return "s2MEQ6k4acTakezkqFdoYWw909DqLwIro8ZeR9Iy";
                   }
               };
            }

            @Override
            public void refresh() {

            }
        };
        this.config = new KinesisConnectorConfiguration(properties, prov);
        setupAWSResources();
        setupInputStream();

        // Initialize executor with configurations
        super.initialize(config,content);
    }

    /**
     * Returns an {@link AWSCredentialsProvider} with the permissions necessary to accomplish all specified
     * tasks. At the minimum it will require read permissions for Amazon Kinesis. Additional read permissions
     * and write permissions may be required based on the Pipeline used.
     * 
     * @return
     */
    public AWSCredentialsProvider getAWSCredentialsProvider() {
        return new DefaultAWSCredentialsProviderChain();
    }

    /**
     * Setup necessary AWS resources for the samples. By default, the Executor does not create any
     * AWS resources. The user must specify true for the specific create properties in the
     * configuration file.
     */
    private void setupAWSResources() {
        if (parseBoolean(CREATE_KINESIS_INPUT_STREAM, DEFAULT_CREATE_RESOURCES, properties)) {
            KinesisUtils.createInputStream(config);
        }

        if (parseBoolean(CREATE_KINESIS_OUTPUT_STREAM, DEFAULT_CREATE_RESOURCES, properties)) {
            KinesisUtils.createOutputStream(config);
        }

        if (parseBoolean(CREATE_DYNAMODB_DATA_TABLE, DEFAULT_CREATE_RESOURCES, properties)) {
            String key = properties.getProperty(DYNAMODB_KEY);
            Long readCapacityUnits =
                    parseLong(DYNAMODB_READ_CAPACITY_UNITS, DEFAULT_DYNAMODB_READ_CAPACITY_UNITS, properties);
            Long writeCapacityUnits =
                    parseLong(DYNAMODB_WRITE_CAPACITY_UNITS, DEFAULT_DYNAMODB_WRITE_CAPACITY_UNITS, properties);
            //createDynamoDBTable(key, readCapacityUnits, writeCapacityUnits);
        }

        if (parseBoolean(CREATE_REDSHIFT_CLUSTER, DEFAULT_CREATE_RESOURCES, properties)) {
            String clusterIdentifier = properties.getProperty(REDSHIFT_CLUSTER_IDENTIFIER);
            String databaseName = properties.getProperty(REDSHIFT_DATABASE_NAME);
            String clusterType = properties.getProperty(REDSHIFT_CLUSTER_TYPE);
            int numberOfNodes = parseInt(REDSHIFT_NUMBER_OF_NODES, DEFAULT_REDSHIFT_NUMBER_OF_NODES, properties);
            //createRedshiftCluster(clusterIdentifier, databaseName, clusterType, numberOfNodes);
        }

        if (parseBoolean(CREATE_REDSHIFT_DATA_TABLE, DEFAULT_CREATE_RESOURCES, properties)) {
            createRedshiftDataTable();
        }

        if (parseBoolean(CREATE_REDSHIFT_FILE_TABLE, DEFAULT_CREATE_RESOURCES, properties)) {
            //createRedshiftFileTable();
        }

        if (parseBoolean(CREATE_S3_BUCKET, DEFAULT_CREATE_RESOURCES, properties)) {
            String s3Bucket = properties.getProperty(S3_BUCKET);
            //createS3Bucket(s3Bucket);
        }

        if (parseBoolean(CREATE_ELASTICSEARCH_CLUSTER, DEFAULT_CREATE_RESOURCES, properties)) {
            //createElasticsearchCluster();
        }
    }

    /**
     * Helper method to spawn the {@link StreamSource} in a separate thread.
     */
    private void setupInputStream() {
        if (parseBoolean(CREATE_STREAM_SOURCE, DEFAULT_CREATE_STREAM_SOURCE, properties)) {
            String inputFile = properties.getProperty(INPUT_STREAM_FILE);
            StreamSource streamSource;
            if (config.BATCH_RECORDS_IN_PUT_REQUEST) {
                streamSource =
                        new BatchedStreamSource(content,config, inputFile, parseBoolean(LOOP_OVER_STREAM_SOURCE,
                                DEFAULT_LOOP_OVER_STREAM_SOURCE,
                                properties));

            } else {
                streamSource =
                        new StreamSource(content, config, inputFile, parseBoolean(LOOP_OVER_STREAM_SOURCE,
                                DEFAULT_LOOP_OVER_STREAM_SOURCE,
                                properties));
            }
            Thread streamSourceThread = new Thread(streamSource);
            LOG.info("Starting stream source.");
            streamSourceThread.start();
        }
    }



    /**
     * Helper method to create the data table in Amazon Redshift.
     */
    private void createRedshiftDataTable() {
        Properties p = new Properties();
        p.setProperty("user", config.REDSHIFT_USERNAME);
        p.setProperty("password", config.REDSHIFT_PASSWORD);
        if (RedshiftUtils.tableExists(p, config.REDSHIFT_URL, config.REDSHIFT_DATA_TABLE)) {
            LOG.info("Amazon Redshift data table " + config.REDSHIFT_DATA_TABLE + " exists.");
            return;
        }
        try {
            LOG.info("Creating Amazon Redshift data table " + config.REDSHIFT_DATA_TABLE);
            RedshiftUtils.createRedshiftTable(config.REDSHIFT_URL,
                    p,
                    config.REDSHIFT_DATA_TABLE,
                    getKinesisMessageModelFields());
        } catch (SQLException e) {
            String msg = "Could not create Amazon Redshift data table " + config.REDSHIFT_DATA_TABLE;
            throw new IllegalStateException(msg, e);
        }
    }

    /**
     * Helper method to create the file table in Amazon Redshift.
     */
    private void createRedshiftFileTable() {
        Properties p = new Properties();
        p.setProperty("user", config.REDSHIFT_USERNAME);
        p.setProperty("password", config.REDSHIFT_PASSWORD);
        if (RedshiftUtils.tableExists(p, config.REDSHIFT_URL, config.REDSHIFT_FILE_TABLE)) {
            LOG.info("Amazon Redshift file table " + config.REDSHIFT_FILE_TABLE + " exists.");
            return;
        }
        try {
            LOG.info("Creating Amazon Redshift file table " + config.REDSHIFT_FILE_TABLE);
            RedshiftUtils.createRedshiftTable(config.REDSHIFT_URL, p, config.REDSHIFT_FILE_TABLE, getFileTableFields());
        } catch (SQLException e) {
            String msg = "Could not create Amazon Redshift file table " + config.REDSHIFT_FILE_TABLE;
            throw new IllegalStateException(msg, e);
        }
    }

    /**
     * Helper method to build the data table.
     * 
     * @return Fields for the data table
     */
    private static List<String> getKinesisMessageModelFields() {
        List<String> fields = new ArrayList<String>();
        fields.add("session varchar(120) not null distkey sortkey,");
        fields.add("campaign_id integer");
        fields.add("bid decimal(16,2)");
        fields.add("comission decimal(16,2)");
        fields.add("publisher_id integer");
        fields.add("advertiser_id integer");
        fields.add("query_id varchar(120)");
        fields.add("ad_id integer");
        fields.add("ad_url varchar(250)");
        fields.add("click_url varchar(250)");
        fields.add("headline varchar(250)");
        fields.add("description varchar(250)");
        fields.add("created_at date");

        return fields;
    }

    /**
     * Helper method to help create the file table.
     * 
     * @return File table fields
     */
    private List<String> getFileTableFields() {
        List<String> fields = new ArrayList<String>();
        fields.add(config.REDSHIFT_FILE_KEY_COLUMN + " varchar(255) primary key");
        return fields;
    }

    /**
     * Helper method to create the Amazon S3 bucket.
     * 
     * @param s3Bucket
     *        The name of the bucket to create
     */
    /**
     * Helper metho

    /**
     * Helper method used to parse boolean properties.
     * 
     * @param property
     *        The String key for the property
     * @param defaultValue
     *        The default value for the boolean property
     * @param properties
     *        The properties file to get property from
     * @return property from property file, or if it is not specified, the default value
     */
    private static boolean parseBoolean(String property, boolean defaultValue, Properties properties) {
        return Boolean.parseBoolean(properties.getProperty(property, Boolean.toString(defaultValue)));
    }

    /**
     * Helper method used to parse long properties.
     * 
     * @param property
     *        The String key for the property
     * @param defaultValue
     *        The default value for the long property
     * @param properties
     *        The properties file to get property from
     * @return property from property file, or if it is not specified, the default value
     */
    private static long parseLong(String property, long defaultValue, Properties properties) {
        return Long.parseLong(properties.getProperty(property, Long.toString(defaultValue)));
    }

    /**
     * Helper method used to parse integer properties.
     * 
     * @param property
     *        The String key for the property
     * @param defaultValue
     *        The default value for the integer property
     * @param properties
     *        The properties file to get property from
     * @return property from property file, or if it is not specified, the default value
     */
    private static int parseInt(String property, int defaultValue, Properties properties) {
        return Integer.parseInt(properties.getProperty(property, Integer.toString(defaultValue)));
    }
}
