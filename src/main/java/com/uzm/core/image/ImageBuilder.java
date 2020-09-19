package com.uzm.core.image;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.uzm.core.DiscordBot;
import com.uzm.core.audio.utilities.TimeManager;
import net.dv8tion.jda.api.entities.Member;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;

public class ImageBuilder {

  private static BufferedImage LAYER;
  public static BufferedImage TTT_BACKGROUND;
  public static BufferedImage VOLUME_BACKGROUND;

  public static BufferedImage NOTFOUND;

  static {
    try {
      LAYER = downloadImage("https://i.imgur.com/NYMOwC0.png");
      TTT_BACKGROUND= downloadImage("https://i.imgur.com/qc7ESQJ.png");
      VOLUME_BACKGROUND= downloadImage("https://i.imgur.com/HetwYOY.png");
      NOTFOUND= downloadImage("https://i.imgur.com/RsesscI.png");
    }catch (Exception ignored) {
    }
  }

  public static InputStream buildTrackImage(DiscordBot bot, AudioTrack track, String videoId) {
    try {
      BufferedImage base = downloadImage("https://img.youtube.com/vi/"+ videoId +"/maxresdefault.jpg");
      if (base == null) {
        base = downloadImage("https://img.youtube.com/vi/"+ videoId +"/0.jpg");;
      }
      BufferedImage background = new BufferedImage(1280, 720, BufferedImage.TYPE_INT_ARGB);
      Graphics2D backgroundGraphics = background.createGraphics();
      backgroundGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      backgroundGraphics.setBackground(Color.BLACK);
      backgroundGraphics.clearRect(0, 0, background.getWidth(), background.getHeight());

      Graphics2D imageGraphics = background.createGraphics();
      imageGraphics.translate(background.getWidth() / 2, background.getHeight() / 2);
      imageGraphics.translate(-base.getWidth(null) / 2, -base.getHeight(null) / 2);
      imageGraphics.drawImage(base, 0, 0, null);

      Graphics2D layer = background.createGraphics();
      layer.drawImage(LAYER, 0, 0, null);

      Graphics2D textGraphics = background.createGraphics();
      textGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      textGraphics.setFont(new Font("Arial", Font.PLAIN, 12));
      textGraphics.setColor(Color.WHITE);
      textGraphics.drawString(TimeManager.formatTimeHM(track.getDuration() * 45 / 100) + " / " +TimeManager.formatTimeHM(track.getDuration()) , 135, 706);
      textGraphics.setFont(new Font("Arial", Font.BOLD, 16));
      textGraphics.drawString(track.getInfo().title, 20, 27);


      ByteArrayOutputStream os = new ByteArrayOutputStream();
      ImageIO.write(background, "png", os);

      return new ByteArrayInputStream(os.toByteArray());

    } catch (IOException err) {
      err.printStackTrace();
      return null;
    }


  }


  public static BufferedImage downloadImage(String s) {
    try {
      URL url = new URL(s);
      URLConnection openConnection = url.openConnection();
      boolean check = true;
      openConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
      openConnection.connect();
      openConnection.setReadTimeout(500);
      openConnection.setConnectTimeout(1000);
      if (openConnection.getContentLength() > 8000000) {
        System.out.println(" file size is too big.");
        check = false;
      }
      BufferedImage img = null;
        InputStream in = new BufferedInputStream(openConnection.getInputStream());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        int n = 0;
        while (-1 != (n = in.read(buf))) {
          out.write(buf, 0, n);
        }
        out.close();
        in.close();
        byte[] response = out.toByteArray();
        img = ImageIO.read(new ByteArrayInputStream(response));

      return img;
    }catch (Exception err) {
        return null;
    }


  }

  public static BufferedImage circleMaskImage(BufferedImage master) {

    int diameter = Math.min(master.getWidth(), master.getHeight());
    BufferedImage mask = new BufferedImage(master.getWidth(), master.getHeight(), BufferedImage.TYPE_INT_ARGB);

    Graphics2D g2d = mask.createGraphics();
    applyQualityRenderingHints(g2d);
    g2d.fillOval(0, 0, diameter - 1, diameter - 1);
    g2d.dispose();

    BufferedImage masked = new BufferedImage(diameter, diameter, BufferedImage.TYPE_INT_ARGB);
    g2d = masked.createGraphics();
    applyQualityRenderingHints(g2d);
    int x = (diameter - master.getWidth()) / 2;
    int y = (diameter - master.getHeight()) / 2;
    g2d.drawImage(master, x, y, null);
    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.DST_IN));
    g2d.drawImage(mask, 0, 0, null);
    g2d.dispose();
    return masked;
  }
  public static void applyQualityRenderingHints(Graphics2D g2d) {

    g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
    g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
    g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
    g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
    g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

  }
  public static InputStream buildVolumeImage(String videoTitle, Member member, long volume) {
    try {
      BufferedImage background = new BufferedImage(500, 500, BufferedImage.TYPE_INT_ARGB);
      BufferedImage profile =  circleMaskImage(downloadImage(member.getUser().getAvatarUrl() + "?size=32"));
      Graphics2D imageGraphics = background.createGraphics();
      imageGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      imageGraphics.translate(background.getWidth() / 2, background.getHeight() / 2);
      imageGraphics.translate(-VOLUME_BACKGROUND.getWidth(null) / 2, -VOLUME_BACKGROUND.getHeight(null) / 2);
      imageGraphics.drawImage(VOLUME_BACKGROUND, 0, 0, null);
      imageGraphics.drawImage(profile, 53, 240, null);

      Graphics2D textGraphics = background.createGraphics();
      textGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      textGraphics.setFont(new Font("Arial", Font.BOLD, 13));
      textGraphics.setColor(Color.WHITE);
      textGraphics.drawString(videoTitle , 100, 215);
      textGraphics.setFont(new Font("Arial", Font.PLAIN, 40));
      textGraphics.drawString(volume + "%", 335, 460);
      textGraphics.setFont(new Font("Arial", Font.BOLD, 12));
      textGraphics.drawString("Solicitado por: " + member.getUser().getName(), 100, 260);
      textGraphics.dispose();
      textGraphics.setColor(Color.WHITE);

      Graphics2D rect = background.createGraphics();
      rect.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      rect.setColor(Color.WHITE);
      rect.fillRoundRect(334,(int)Math.max(-2.8*volume + 370, 90) , 74, 46, 20, 20); // to
      ByteArrayOutputStream os = new ByteArrayOutputStream();
      ImageIO.write(background, "png", os);

      return new ByteArrayInputStream(os.toByteArray());

    } catch (IOException err) {
      err.printStackTrace();
      return null;
    }


  }
  public static BufferedImage resize(BufferedImage img, int newW, int newH) {
    int w = img.getWidth();
    int h = img.getHeight();
    BufferedImage dimg = new BufferedImage(newW, newH, img.getType());
    Graphics2D g = dimg.createGraphics();
    g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
      RenderingHints.VALUE_INTERPOLATION_BILINEAR);
    g.drawImage(img, 0, 0, newW, newH, 0, 0, w, h, null);
    g.dispose();
    return dimg;
  }

}
