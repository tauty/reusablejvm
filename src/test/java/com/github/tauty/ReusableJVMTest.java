package com.github.tauty;

import org.junit.Test;

import java.util.LinkedList;

public class ReusableJVMTest extends ReusableJVM {

    @Test
    public void args_parsed_correctly() throws Exception {
        LinkedList args = ReusableJVM.parseArgs("java ShowArgs octopus squid \"sea cucumber\"");
        args.removeFirst();
        ReusableJVM.invokeMain(args);

        args = ReusableJVM.parseArgs("java ShowArgs tako ika \"na\r\nma\t\tko\"  \"\\\"tako\\\"\" octopus");
        args.removeFirst();
        ReusableJVM.invokeMain(args);
    }
}
