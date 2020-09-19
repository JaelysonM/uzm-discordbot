package com.uzm.core.audio.utilities;

import java.util.ArrayList;
import java.util.List;


public class Scroller {

  private int position;

  private String name;
  private Scroller s;
  private List<String> list;

  public Scroller get() {
    return s;

  }

  public String getN() {
    return name;

  }

  /**
   * @param message      The String to scroll
   * @param width        The width of the window to scroll across (i.e. 16 for signs)
   * @param spaceBetween The amount of spaces between each repetition
   * @param colourChar   The colour code character you're using (i.e. & or �)
   */
  public Scroller(String message, int width, int spaceBetween) {
    name = message;
    s = this;

    list = new ArrayList<>();

    // Validation
    // String is too short for window
    if (message.length() < width) {
      StringBuilder sb = new StringBuilder(message);
      while (sb.length() < width)
        sb.append(" ");
      message = sb.toString();
    }

    // Allow for colours which add 2 to the width
    width -= 2;

    // Invalid width/space size
    if (width < 1)
      width = 1;
    if (spaceBetween < 0)
      spaceBetween = 0;



    // Add substrings
    for (int i = 0; i < message.length() - width; i++)
      list.add(message.substring(i, i + width));

    // Add space between repeats
    StringBuilder space = new StringBuilder();
    for (int i = 0; i < spaceBetween; ++i) {
      list.add(message.substring(message.length() - width + (i > width ? width : i), message.length()) + space);
      if (space.length() < width)
        space.append(" ");
    }

    // Wrap
    for (int i = 0; i < width - spaceBetween; ++i)
      list.add(message.substring(message.length() - width + spaceBetween + i, message.length()) + space + message.substring(0, i));

    // Join up
    for (int i = 0; i < spaceBetween; i++) {
      if (i > space.length())
        break;
      list.add(space.substring(0, space.length() - i) + message.substring(0, width - (spaceBetween > width ? width : spaceBetween) + i));
    }
  }

  /**
   * @return Gets the next String to display
   */
  public String next() {
    StringBuilder sb = getNext();



    return sb.toString();

  }
  public boolean isEnded() {
    return position>= list.size();
  }

  private StringBuilder getNext() {
    return new StringBuilder(list.get(position++ % list.size()).substring(0));
  }

}
