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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;

import com.amazonaws.auth.AWSCredentials;
import com.example.utils.KinesisUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.regions.RegionUtils;
import com.amazonaws.services.kinesis.AmazonKinesisClient;
import com.amazonaws.services.kinesis.connectors.KinesisConnectorConfiguration;
import com.amazonaws.services.kinesis.model.PutRecordRequest;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * This class is a data source for supplying input to the Amazon Kinesis stream. It reads lines from the
 * input file specified in the constructor and emits them by calling String.getBytes() into the
 * stream defined in the KinesisConnectorConfiguration.
 */
public class StreamSource implements Runnable {
    private static Log LOG = LogFactory.getLog(StreamSource.class);
    protected AmazonKinesisClient kinesisClient;
    protected KinesisConnectorConfiguration config;
    protected final String inputFile;
    protected final boolean loopOverInputFile;
    protected ObjectMapper objectMapper;
    String content;

    /**
     * Creates a new StreamSource.
     * 
     * @param config
     *        Configuration to determine which stream to put records to and get {@link AWSCredentialsProvider}
     * @param inputFile
     *        File containing record data to emit on each line
     */
    public StreamSource(String content, KinesisConnectorConfiguration config, String inputFile) {
        this(content,config, inputFile, false);
    }

    /**
     * Creates a new StreamSource.
     * 
     * @param config
     *        Configuration to determine which stream to put records to and get {@link AWSCredentialsProvider}
     * @param inputFile
     *        File containing record data to emit on each line
     * @param loopOverStreamSource
     *        Loop over the stream source to continually put records
     */
    public StreamSource(String content, KinesisConnectorConfiguration config, String inputFile, boolean loopOverStreamSource) {
        this.config = config;
        this.content=content;
        this.inputFile = inputFile;
        this.loopOverInputFile = loopOverStreamSource;
        this.objectMapper = new ObjectMapper();
        kinesisClient = new AmazonKinesisClient(new AWSCredentialsProvider() {
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
        });
        kinesisClient.setRegion(RegionUtils.getRegion(config.REGION_NAME));
        if (config.KINESIS_ENDPOINT != null) {
            kinesisClient.setEndpoint(config.KINESIS_ENDPOINT);
        }
        KinesisUtils.createInputStream(config);
    }

    @Override
    public void run() {
        int iteration = 0;
        do {
            /*InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(inputFile);
            if (inputStream == null) {
                throw new IllegalStateException("Could not find input file: " + inputFile);
            }
            if (loopOverInputFile) {
                LOG.info("Starting iteration " + iteration + " over input file.");
            }*/
            try {
                processInputStream(content, iteration);
            } catch (IOException e) {
                LOG.error("Encountered exception while putting data in source stream.", e);
                break;
            }
            iteration++;
        } while (loopOverInputFile);
    }

    /**
     * Process the input file and send PutRecordRequests to Amazon Kinesis.
     * 
     * This function serves to Isolate StreamSource logic so subclasses
     * can process input files differently.
     * 
     * @param inputStream
     *        the input stream to process
     * @param iteration
     *        the iteration if looping over file
     * @throws IOException
     *         throw exception if error processing inputStream.
     */
    protected void processInputStream(String content, int iteration) throws IOException {
        KinesisMessageModel kinesisMessageModel = objectMapper.readValue(content, KinesisMessageModel.class);

        PutRecordRequest putRecordRequest = new PutRecordRequest();
        putRecordRequest.setStreamName(config.KINESIS_INPUT_STREAM);
        putRecordRequest.setData(ByteBuffer.wrap(content.getBytes()));
        putRecordRequest.setPartitionKey(kinesisMessageModel.session);
        kinesisClient.putRecord(putRecordRequest);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }
}
