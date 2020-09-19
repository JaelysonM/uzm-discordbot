package com.uzm.core.utilities;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {
  public static String extractYoutubeURL(String ytUrl) {
    String vId = null;
    Pattern pattern = Pattern.compile(
      "http(?:s)?:\\/\\/(?:m.)?(?:www\\.)?youtu(?:\\.be\\/|be\\.com\\/(?:watch\\?(?:feature=youtu.be\\&)?v=|v\\/|embed\\/|user\\/(?:[\\w#]+\\/)+))([^&#?\\n]+)",
      Pattern.CASE_INSENSITIVE);
    Matcher matcher = pattern.matcher(ytUrl);
    if (matcher.matches()){
      vId = matcher.group(1);
    }
    return vId;
  }
  public static String capitalize(final String line) {
    return Character.toUpperCase(line.charAt(0)) + line.substring(1);
  }
  public static int i = 0;
  public static String generateWaiting() {
    if (i < 3) {
      i++;
    } else {
      i = 0;
    }
    StringBuilder potin = new StringBuilder();
    for (int x = 0; x < i; x++) {
      potin.append(".");
    }
    return potin.toString();
  }
  public static boolean is(Manager.NType t, Object o) {
    if (t == null) {
      return false;
    }
    if (t == Manager.NType.INTEGER) {
      try {
        Integer.valueOf((String) o);
        return true;

      } catch (Exception e) {
        return false;
      }
    } else if (t == Manager.NType.DOUBLE) {
      try {
        Double.valueOf((String) o);
        return true;

      } catch (Exception e) {
        return false;
      }
    } else if (t == Manager.NType.FLOAT) {
      try {
        Float.valueOf((String) o);
        return true;

      } catch (Exception e) {
        return false;
      }
    }
    return false;
  }
}
