/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2020 Ilya Pokolev
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package io.zold.telegrambot;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.net.http.*;

import java.util.logging.LogManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App {
    private final static HttpClient http = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1).build();
    private static final Logger log = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) throws IOException, InterruptedException {
        try {
            LogManager.getLogManager().readConfiguration(new FileInputStream("logging.properties"));
        } catch (IOException e) {
            LogManager.getLogManager().readConfiguration(
                App.class.getClassLoader().getResourceAsStream("logging.properties"));
        }
        MultiConfig config = new MultiConfig(args);
    }
}