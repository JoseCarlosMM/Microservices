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
package com.example.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class RedshiftUtils {

    private static Log LOG = LogFactory.getLog(RedshiftUtils.class);



    /**
     * Helper method to create a Amazon Redshift table
     * 
     * @param redshiftURL
     *        The JDBC URL of the Amazon Redshift database
     * @param loginProperties
     *        A properties file containing the authentication credentials for the database
     * @param tableName
     *        The table to create
     * @param fields
     *        A list of column specifications that will be comma separated in the create table
     *        statement
     * @throws SQLException
     *         Table creation failed
     */
    public static void createRedshiftTable(String redshiftURL,
            Properties loginProperties,
            String tableName,
            List<String> fields) throws SQLException {
        Connection conn = DriverManager.getConnection(redshiftURL, loginProperties);
        Statement stmt = conn.createStatement();
        stmt.execute("CREATE TABLE " + tableName + " " + toSQLFields(fields) + ";");
        stmt.close();
        conn.close();
    }

    /**
     * Helper method to build a field String for creating the table in the format (field1, field2,
     * ...)
     * 
     * @param fields
     * @return String in the format (field1, field2, ...)
     */
    private static String toSQLFields(List<String> fields) {
        StringBuilder s = new StringBuilder();
        s.append("(");
        for (String field : fields) {
            s.append(field);
            s.append(",");
        }
        s.replace(s.length() - 1, s.length(), "");
        s.append(")");
        return s.toString();
    }

    /**
     * Helper method to determine if a table exists in the Amazon Redshift database
     * 
     * @param loginProperties
     *        A properties file containing the authentication credentials for the database
     * @param redshiftURL
     *        The JDBC URL of the Amazon Redshift database
     * @param tableName
     *        The table to check existence of
     * @return true if connection to the database is successful and the table exists, otherwise
     *         false
     */
    public static boolean tableExists(Properties loginProperties, String redshiftURL, String tableName) {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(redshiftURL, loginProperties);

            Statement stmt = conn.createStatement();
            stmt.executeQuery("SELECT * FROM " + tableName + " LIMIT 1;");
            stmt.close();
            conn.close();
            return true;
        } catch (SQLException e) {
            LOG.error(e);
            try {
                conn.close();
            } catch (Exception e1) {
            }
            return false;
        }
    }

}
