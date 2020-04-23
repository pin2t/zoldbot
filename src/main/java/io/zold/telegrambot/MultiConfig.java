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
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Configuration options combined from properties file and command line arguments
 * Command line arguments has a priority above the properties file
 *
 * @author Ilya Pokolev (pin2t@mail.ru)
 * @version $
 * @since 1.0
 */
public final class MultiConfig {
    private static final Logger log = LoggerFactory.getLogger(MultiConfig.class);
    private final Properties ini = new Properties();
    private final Map<String, String> args;

    /**
     * @todo add suffizes support for update interval, like 30s, 10m, 1h
     */
    public MultiConfig(final String[] args) {
        this.args = this.parseKV(args);
        if (this.args.containsKey("config"))
            try { this.ini.load(new FileInputStream(this.args.get("config"))); }
            catch (IOException e) {
                log.error("Error reading config file {}: {}", this.args.get("config"), e);
            }
        if (this.args.containsKey("configupdateinterval")) {
            long interval = Long.parseLong(this.args.get("configupdateinterval"));
            String file = this.args.get("config");
            Executors.newScheduledThreadPool(1).scheduleAtFixedRate(
                new TimerTask() {
                    public void run() {
                        synchronized (ini) {
                            try { ini.load(new FileInputStream(file)); }
                            catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                }, interval, interval, TimeUnit.SECONDS);
        }
    }

    public int intValue(final String name, final int def) {
        return Integer.parseInt(this.strValue(name, Integer.toString(def)));
    }

    public String strValue(final String name, final String def) {
        if (this.args.containsKey(name))
            return this.args.get(name);
        synchronized (this.ini) {
            return this.ini.getProperty(name, def);
        }
    }

    public boolean boolValue(final String name, final boolean def) {
        return Boolean.parseBoolean(this.strValue(name, Boolean.toString(def)));
    }


    public void print() {
        log.info("command line arguments:");
        for (Map.Entry<String, String> arg: args.entrySet())
            log.info("{}: {}", arg.getKey(), arg.getValue());
        log.info("file options:");
        for (Map.Entry<Object, Object> arg: ini.entrySet())
            log.info("{}: {}", arg.getKey().toString(), arg.getValue().toString());
    }

    /**  
     * Parses command line arguments as a --key value format
    */
    private Map<String, String>  parseKV(final String[] args) {
        Map<String, String> result = new HashMap<>(0);
        int i = 0;
        while (i < args.length) {
            final String arg = args[i];
            if (arg.startsWith("--")) {
                String key = args[i].toLowerCase().substring(2);
                if (i < args.length - 1 && !args[i + 1].startsWith("--")) {
                    result.put(key, args[i + 1]);
                    i++;
                } else
                    result.put(key, "");
            }
            i++;
        }
        return result;
    }
}
