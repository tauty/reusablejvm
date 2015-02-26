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

import java.io.*;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author tetz
 */
public class ReusableJVM {

    public static void main(String[] args) throws IOException {
        // backup
        final PrintStream STDOUT = System.out;

        // create alternatives
        final ByteArrayOutputStream outBaos = new ByteArrayOutputStream();
        final PrintStream sysout = new PrintStream(outBaos);
        final ByteArrayOutputStream errBaos = new ByteArrayOutputStream();
        final PrintStream syserr = new PrintStream(errBaos);

        // replace them
        System.setOut(sysout);
        System.setErr(syserr);

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        for (String line; null != (line = reader.readLine()); ) {
            try {
                LinkedList list = parseArgs(line);
                String commandName = (String) list.removeFirst();
                if (commandName.equals("java")) {
                    invokeMain(commandName, list);
                    STDOUT.println("SUCCESS");
                } else if (commandName.equals("sysout")) {
                    sysout.flush();
                    outBaos.writeTo(STDOUT);
                    outBaos.reset();
                } else if (commandName.equals("syserr")) {
                    syserr.flush();
                    errBaos.writeTo(STDOUT);
                    errBaos.reset();
                } else if (commandName.equals("exit")) {
                    return;
                } else {
                    throw new UnsupportedOperationException(commandName + " is not supported. Check the usage.");
                }
            } catch (Throwable t) {
                STDOUT.println(t instanceof Exception ? "EXCEPTION" : "ERROR");
                t.printStackTrace();
                System.err.println();
                System.err.println("###END###");
            }
        }
    }

    static void invokeMain(String commandName, LinkedList list) throws Exception {
        String className = (String) list.removeFirst();
        Class clazz = Class.forName(className);
        Method main = clazz.getMethod("main", new Class[]{String[].class});
        main.invoke(null, new Object[]{toStringArray(list)});
    }

    private static final Pattern DELIM_PTN = Pattern.compile("(\\s+|$|\\\")");
    private static final Pattern QUOTE_PTN = Pattern.compile("((\\\\\"|[^\"])*)\"\\s*");

    static LinkedList parseArgs(String s) throws DoubleQuotationUnmatchException {
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
        for (int i = 0; i < list.size(); i++) {
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
}
