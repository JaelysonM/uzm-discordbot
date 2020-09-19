package com.uzm.core.utilities;

import java.lang.management.ManagementFactory;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.MBeanServer;
import javax.management.ObjectName;



public class InternalStats {
  @SuppressWarnings("unused")
  private long messagesReceived;
  @SuppressWarnings("unused")
  private long commandsReceived;
  @SuppressWarnings("unused")
  private long loadedCommands;
  @SuppressWarnings("unused")
  private long guilds;
  @SuppressWarnings("unused")
  private long users;
  @SuppressWarnings("unused")
  private long roleCount;
  @SuppressWarnings("unused")
  private long textChannelCount;

  @SuppressWarnings("unused")
  private long voiceChannelCount;

  @SuppressWarnings("unused")
  private long musicPlayers;
  @SuppressWarnings("unused")
  private double used_ram;

  private double total_ram;

  @SuppressWarnings("unused")
  private double cpu_usage;

  public InternalStats(long messagesReceived, long commandsReceived, long loadedCommands, long guilds, long users, long roleCount, long textChannelCount, long voiceChannelCount,
    long musicPlayers) {
    this.messagesReceived = messagesReceived;
    this.commandsReceived = commandsReceived;
    this.loadedCommands = loadedCommands;
    this.guilds = guilds;
    this.users = users;
    this.roleCount = roleCount;
    this.textChannelCount = textChannelCount;
    this.voiceChannelCount = voiceChannelCount;
    this.musicPlayers = musicPlayers;
    this.total_ram = Runtime.getRuntime().totalMemory() / 1024 / 1024;
    this.used_ram = total_ram - Runtime.getRuntime().freeMemory() / 1024 / 1024;
    try {
      this.cpu_usage = getProcessCpuLoad();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static InternalStats collect() {
    long messagesReceived = 0;
    long commandsReceived = 0;
    long loadedCommands = 0;
    long guilds = 0;
    long users = 0;
    long roleCount = 0;
    long textChannelCount = 0;
    long voiceChannelCount = 0;
    long musicPlayers = 0;
    return new InternalStats(messagesReceived, commandsReceived, loadedCommands, guilds, users, roleCount, textChannelCount, voiceChannelCount, musicPlayers);
  }

  public static double getProcessCpuLoad() throws Exception {
    MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
    ObjectName name = ObjectName.getInstance("java.lang:type=OperatingSystem");
    AttributeList list = mbs.getAttributes(name, new String[] {"ProcessCpuLoad"});

    if (list.isEmpty())
      return Double.NaN;

    Attribute att = (Attribute) list.get(0);
    Double value = (Double) att.getValue();

    // usually takes a couple of seconds before we get real values
    if (value == -1.0)
      return Double.NaN;
    // returns a percentage value with 1 decimal point precision
    return ((int) (value * 1000) / 10.0);
  }

  public long getUptime() {
    return ManagementFactory.getRuntimeMXBean().getUptime() / 1000;
  }


  public double getTotal_ram() {
    return total_ram;
  }

  public double getCpu_usage() {
    return cpu_usage;
  }

  public double getUsed_ram() {
    return used_ram;
  }
}