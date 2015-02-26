package com.github.tauty;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;

public class ReusableJVMTest extends ReusableJVM {

    @Test
    public void args_parsed_correctly() throws Exception {
        LinkedList args = ReusableJVM.parseArgs("java ShowArgs octopus squid \"sea cucumber\"");
        ReusableJVM.invokeMain((String) args.removeFirst(), args);

        args = ReusableJVM.parseArgs("java ShowArgs tako ika \"na\r\nma\t\tko\"  \"\\\"tako\\\"\" octopus");
        ReusableJVM.invokeMain((String) args.removeFirst(), args);
    }
}
