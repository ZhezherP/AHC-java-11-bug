package com.test;

import org.asynchttpclient.AsyncCompletionHandler;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClientConfig;
import org.asynchttpclient.Dsl;
import org.asynchttpclient.RequestBuilder;
import org.asynchttpclient.Response;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StartUp {

    private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(100);

    public static void main(String[] args) {
        AsyncHttpClient client = createClient();

        while (true) {
            try {
                EXECUTOR.submit(() -> executeRequest(client));
                Thread.sleep(100);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void executeRequest(AsyncHttpClient client) {
        RequestBuilder builder = Dsl.get("https://www.google.com/")
                .setReadTimeout(500)
                .setRequestTimeout(500);

        CompletableFuture<Response> future = client.executeRequest(builder.build(), new AsyncCompletionHandler<Response>() {
            @Override
            public Response onCompleted(Response response) {
                return response;
            }
        })
                .toCompletableFuture()
                .thenCompose(response -> {
                    System.out.println("success");
                    return CompletableFuture.completedFuture(response);
                })
                .handle((response, throwable) -> {
                    if (throwable != null) {
                        System.out.println("failed " + throwable.getMessage());
                    }
                    return response;
                });
    }

    private static AsyncHttpClient createClient() {
        DefaultAsyncHttpClientConfig.Builder config = Dsl.config()
                .setIoThreadsCount(100)
                .setCookieStore(null)
                .setKeepAlive(true)
                .setUseLaxCookieEncoder(true);

        return Dsl.asyncHttpClient(config);
    }
}
