/*
 * Copyright 2015 tetsuo.ohta[at]gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.tauty;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * @author tetz
 */
public class ReusableJVM {
 
    public static void main(String[] args) {
        final PrintStream STDOUT = System.out;
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final PrintStream printStream = new PrintStream(baos);
        System.setOut(printStream);
 
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            for (String line; null != (line = reader.readLine()); ) {
                exec(line, new Runnable() {
                    public void run() {
                        try {
                            printStream.flush();
                            baos.writeTo(STDOUT);
                            baos.reset();
                        } catch (IOException e) {
                            throw new IORuntimeException(e);
                        }
                    }
                });
                STDOUT.println("SUCCESS");
            }
        } catch (Throwable t) {
            STDOUT.println(t instanceof Exception ? "EXCEPTION" : "ERROR");
            t.printStackTrace();
        }
    }
 
    static void exec(String line, Runnable stdoutTransfer) throws Exception {
        LinkedList list = parseArgs(line);
        String commandName = (String) list.removeFirst();
        if (commandName.equals("java")) {
            String className = (String) list.removeFirst();
            Class clazz = Class.forName(className);
            Method main = clazz.getMethod("main", new Class[]{String[].class});
            main.invoke(null, new Object[]{toStringArray(list)});
        } else if (commandName.equals("sysout")) {
            stdoutTransfer.run();
        } else {
            throw new UnsupportedOperationException(commandName + " is not supported. Check the usage.");
        }
    }
 
    private static final Pattern DELIM_PTN = Pattern.compile("(\\s+|$|\\\")");
    private static final Pattern QUOTE_PTN = Pattern.compile("((\\\\\"|[^\"])*)\"\\s*");
 
    private static LinkedList parseArgs(String s) throws DoubleQuotationUnmatchException {
        LinkedList list = new LinkedList();
 
        int pos = 0;
        Matcher dm = DELIM_PTN.matcher(s);
        Matcher qm = QUOTE_PTN.matcher(s);
        while (dm.find(pos)) {
            if (dm.group().equals("\"")) {
                if (qm.find(dm.end())) {
                    list.add(decodeEcaped(qm.group(1)));
                    pos = qm.end();
                } else {
                    throw new DoubleQuotationUnmatchException(s);
                }
            } else {
                String param = s.substring(pos, dm.start());
                if (param == null || param.equals("")) break;
                list.add(param);
                pos = dm.end();
            }
        }
        return list;
    }
    
    private static String[] toStringArray(List list) {
        String[] result = new String[list.size()];
        for(int i = 0; i<list.size(); i++) {
            result[i] = (String) list.get(i);
        }
        return result;
    }
 
    private static String decodeEcaped(String s) {
        return s.replaceAll("\\\\r", "\r").replaceAll("\\\\n", "\n")
                .replaceAll("\\\\t", "\t").replaceAll("\\\\\"", "\"");
    }
 
    public static class DoubleQuotationUnmatchException extends Exception {
        /***/
        private static final long serialVersionUID = 1L;
        private DoubleQuotationUnmatchException(String argString) {
            super("The double quotations are unmatched. the text:" + argString);
        }
    }
 
    public static class IORuntimeException extends RuntimeException {
        /***/
        private static final long serialVersionUID = 1L;
        private IORuntimeException(IOException cause) {
            super(cause);
        }
    }
}
