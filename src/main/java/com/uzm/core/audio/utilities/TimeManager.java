package com.uzm.core.audio.utilities;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class TimeManager {


public static String[] MONTHS_ARRAY = {"janeiro", "fevereiro", "março", "abril", "maio", "junho", "julho", "agosto", "setembro", "outubro", "novembro", "dezembro"};
public static String[] DAYS_ARRAY = {"domingo", "segunda-feira", "terça-feira", "quarta-feira", "quinta-feira", "sexta-feira", "sábado"};

  public static long generateTime(TimeUnit tipo, int tempo) {

    return System.currentTimeMillis() + tipo.toMillis(tempo);
  }

  public static String formatTime(Date date) {
    int hour = (date.getHours() > 12 ? (date.getHours() - 12) :date.getHours());
    return formatNumber(hour) + ":" + formatNumber(date.getMinutes()) + " " +(date.getHours() > 12 ?"da tarde": "da manhã");
  }

  public static String formatDate(Date date) {
    return date.getDate() + " de " +  MONTHS_ARRAY[date.getMonth()] + " de " + (date.getYear() + 1900);
  }

  public static String  formatDateBR(Date date) {
  return DAYS_ARRAY[date.getDay()]  + ", " + formatDate(date) + " às " + formatTime(date);
  }
  public static String formatNumber(int number) {
    return number < 10 ? "0" + number : String.valueOf(number);
  }
  public static long createtime(TimeUnit tipo, int tempo) {
    return tipo.toMillis(tempo);
  }
  @SuppressWarnings("deprecation")
  public static boolean isTime(String time) {
    Date date = new Date(System.currentTimeMillis());


    if (date.getHours() == Integer.parseInt(time.split(":")[0]) && date.getMinutes() == Integer.valueOf(time.split(":")[1]) && date.getSeconds() == Integer
      .parseInt(time.split(":")[2])) {
      return true;
    } else {
      return false;
    }

  }

  public static String getTimeUntil(long epoch) {
    epoch -= System.currentTimeMillis();
    return getTime(epoch);
  }

  public static String getTime(long ms) {
    ms = (long) Math.ceil(ms / 1000.0);
    StringBuilder sb = new StringBuilder(40);

    if (ms / 31449600 > 0) {
      long years = ms / 31449600;
      sb.append(years + (years == 1 ? " ano " : " anos "));
      ms -= years * 31449600;
    }
    if (ms / 2620800 > 0) {
      long months = ms / 2620800;
      sb.append(months + (months == 1 ? " mês " : " meses "));
      ms -= months * 2620800;
    }
    if (ms / 604800 > 0) {
      long weeks = ms / 604800;
      sb.append(weeks + (weeks == 1 ? " semana " : " semanas "));
      ms -= weeks * 604800;
    }
    if (ms / 86400 > 0) {
      long days = ms / 86400;
      sb.append(days + (days == 1 ? " dia " : " dias "));
      ms -= days * 86400;
    }
    if (ms / 3600 > 0) {
      long hours = ms / 3600;
      sb.append(hours + (hours == 1 ? " hora " : " horas "));
      ms -= hours * 3600;
    }
    if (ms / 60 > 0) {
      long minutes = ms / 60;
      sb.append(minutes + (minutes == 1 ? " minuto " : " minutos "));
      ms -= minutes * 60;
    }
    if (ms > 0) {
      sb.append(ms + (ms == 1 ? " segundo " : " segundos "));
    }
    if (sb.length() > 1) {
      sb.replace(sb.length() - 1, sb.length(), "");
    } else {
      sb = new StringBuilder("Acabado");
    }

    return sb.toString();
  }

  public static String formatTimeHM(long ms) {
    ms = (long) Math.ceil(ms / 1000.0);

    SimpleDateFormat sdfDate = new SimpleDateFormat("mm:ss");
    Date d = new Date();

    if (ms / 60 > 0) {
      long minutes = ms / 60;
      d.setMinutes((int) minutes);
      ms -= minutes * 60;
    } else {
      d.setMinutes(0);
    }
    d.setSeconds((int) ms);


    return sdfDate.format(d);
  }

}
