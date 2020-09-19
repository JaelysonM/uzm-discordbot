package com.uzm.core.utilities;

public class Utils {
  public static int random(int min, int max) {
    int range = max - min;
    return min + (int) (Math.random() * range);
  }

  public static boolean isNumeric(String strNum) {
    return strNum.matches("-?\\d+(\\.\\d+)?");
  }
}
