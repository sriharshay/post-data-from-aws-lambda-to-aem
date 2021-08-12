package com.test.aws.lambda.feed;

import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.StringUtils;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.test.aws.lambda.feed.model.Country;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

/**
 * POST data
 */
public class PostData {
    private static final AmazonS3 s3 = AmazonS3ClientBuilder.defaultClient();
    private static final Logger logger = LoggerFactory.getLogger(PostData.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final String PROTOCOL = "https";
    private static final int PORT = 4502;
    private static final String HOST = "localhost";
    private static final String username = "admin";
    private static final String password = "admin";

    public void handler(S3Event event) {
        event.getRecords().forEach(record -> {
            S3ObjectInputStream s3inputStream = s3
                    .getObject(record.getS3().getBucket().getName(), record.getS3().getObject().getKey())
                    .getObjectContent();
            try {
                logger.info("Reading data from s3 record");
                List<Country> feed = Arrays
                        .asList(objectMapper.readValue(s3inputStream, Country[].class));
                logger.info(feed.toString());
                s3inputStream.close();
                establishConnection(feed);
            } catch (JsonMappingException e) {
                logger.error("JsonMappingException", e);
                throw new RuntimeException("Error while processing S3 event", e);
            } catch (IOException | HttpException e) {
                logger.error("Exception", e);
                e.printStackTrace();
            }
        });
    }

    public void establishConnection(List<Country> countryList) throws IOException, HttpException {
        //HttpPost request = new HttpPost(String.format("%s://%s:%s/j_security_check", PROTOCOL, HOST, PORT) + "/content/hal/us/" + StringUtils.lowerCase(country.getId()) + "/");
        HttpClientContext context = HttpClientContext.create();
        CredentialsProvider provider = new BasicCredentialsProvider();
        provider.setCredentials(AuthScope.ANY,
                new UsernamePasswordCredentials(username, password));
        AuthCache authCache = new BasicAuthCache();
        HttpHost targetHost = new HttpHost(HOST, -1, PROTOCOL);
        authCache.put(targetHost, new BasicScheme());
        context.setCredentialsProvider(provider);
        context.setAuthCache(authCache);

        for (Country country : countryList) {
            HttpPost request = new HttpPost(String.format("%s://%s", PROTOCOL, HOST) + "/content/www/us/" + StringUtils.lowerCase(country.getId()) + "/");
            request.addHeader("Content-Type", "application/x-www-form-urlencoded");
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String json = gson.toJson(country);
            /*logger.info("establishConnection::json [{}]", json);
            json = URLEncoder.encode(objectMapper.writeValueAsString(country), StandardCharsets.UTF_8.toString());
            logger.info("establishConnection::URLEncoded JSON [{}]", json);*/
            StringEntity entity = new StringEntity(json);
            request.setEntity(entity);
            logger.info("establishConnection::entity information [{}]", entity);
            logger.info("establishConnection::Data to be posted [{}]", json);
            HttpClientConnectionManager poolingConnManager
                    = new PoolingHttpClientConnectionManager();
            try (CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(poolingConnManager).build();
                 CloseableHttpResponse response = httpClient.execute(request, context)) {
                System.out.println("Response code: " + response.getStatusLine().getStatusCode());
                HttpEntity responseEntity = response.getEntity();
                if (responseEntity != null) {
                    String result = EntityUtils.toString(responseEntity);
                    logger.info("responseEntity " + result);
                }
            }
        }
    }


}