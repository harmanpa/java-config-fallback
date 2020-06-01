/*
 * The MIT License
 *
 * Copyright 2020 peter.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package co.unruly.config;

import com.google.cloud.secretmanager.v1.AccessSecretVersionResponse;
import com.google.cloud.secretmanager.v1.SecretManagerServiceClient;
import java.io.IOException;

/**
 *
 * @author peter
 */
public class SecretsManagerGCE implements ConfigurationSource {

    private final SecretManagerServiceClient client;

    public SecretsManagerGCE() throws IOException {
        this.client = SecretManagerServiceClient.create();
    }

    @Override
    public String get(String key) {
        return get(accessSecretVersion(key));
    }

    private String get(AccessSecretVersionResponse response) {
        if (response != null && response.hasPayload()) {
            return response.getPayload().getData().toStringUtf8();
        }
        return null;
    }

    private AccessSecretVersionResponse accessSecretVersion(String key) {
        return this.client.accessSecretVersion(key);
    }

}
