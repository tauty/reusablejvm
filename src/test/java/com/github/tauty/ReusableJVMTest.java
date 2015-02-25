package com.github.tauty;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ReusableJVMTest extends ReusableJVM {

    @Test
    public void args_parsed_correctly() throws Exception {
        ReusableJVM.exec("java ShowArgs octopus squid \"sea cucumber\"", null);
        ReusableJVM.exec("java ShowArgs tako ika \"na\r\nma\t\tko\"  \"\\\"tako\\\"\" octopus", null);
    }
}
