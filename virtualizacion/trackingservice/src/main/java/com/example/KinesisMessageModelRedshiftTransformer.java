package com.example;



import com.amazonaws.services.kinesis.connectors.KinesisConnectorConfiguration;
import com.amazonaws.services.kinesis.connectors.redshift.RedshiftTransformer;
import com.example.base.KinesisMessageModel;


public class KinesisMessageModelRedshiftTransformer extends RedshiftTransformer<KinesisMessageModel> {
    private final char delim;

    /**
     * Creates a new KinesisMessageModelRedshiftTransformer.
     *
     * @param config The configuration containing the Amazon Redshift data delimiter
     */
    public KinesisMessageModelRedshiftTransformer(KinesisConnectorConfiguration config) {
        super(KinesisMessageModel.class);
        delim = '|';
    }

    @Override
    public String toDelimitedString(KinesisMessageModel record) {
        StringBuilder b = new StringBuilder();
        b.append(record.session )
                .append(delim)
                .append(record.campaign_id )
                .append(delim)
                .append(record.bid )
                .append(delim)
                .append(record.comission )
                .append(delim)
                .append(record.publisher_id )
                .append(delim)
                .append(record.advertiser_id )
                .append(delim)
                .append(record.query_id )
                .append(delim)
                .append(record.ad_id )
                .append(delim)
                .append(record.ad_url )
                .append(delim)
                .append(record.click_url )
                .append(delim)
                .append(record.headline )
                .append(delim)
                .append(record.description)
                .append(delim)
                .append(record.created_at )
                .append("\n");

        return b.toString();
    }

}