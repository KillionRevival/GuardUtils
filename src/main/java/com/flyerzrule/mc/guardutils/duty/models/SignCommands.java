package com.flyerzrule.mc.guardutils.duty.models;

import java.util.Set;

public class SignCommands {
  public static final String REGISTER_COMMAND = "[guardRegister]";
  public static final String RESIGN_COMMAND = "[guardResign]";
  public static final Set<String> COMMANDS = Set.of(REGISTER_COMMAND, RESIGN_COMMAND);
}
