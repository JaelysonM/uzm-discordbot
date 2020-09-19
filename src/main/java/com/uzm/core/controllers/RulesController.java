package com.uzm.core.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import com.google.api.client.util.Maps;

public class RulesController {

  public enum Punish {
    BAN,
    TEMPBAN,
    MUTE,
    TEMPMUTE,
    NA
  }


  /*
   * Format A-
   */
  private Punish punish;
  private String article;
  private String paragraph;
  private String text;
  public static HashMap<String, RulesController> rules = Maps.newHashMap();

  public RulesController(String article, String p, String text, Punish psh) {
    this.paragraph = p;
    this.article = article;
    this.punish = psh;
    this.text = text;
  }

  public String getArticle() {
    return article;
  }

  public String getText() {
    return text;
  }

  public String getParagraph() {
    return paragraph;
  }

  public Punish getPunish() {
    return punish;
  }

  public static List<RulesController> getDatas() {
    return new ArrayList<RulesController>(rules.values());
  }

  public static RulesController getRule(String article) {
    return rules.getOrDefault(article.toLowerCase(), null);
  }

  public static boolean is(String s) {
    Pattern p = Pattern.compile("^(Art\\dº/§\\dº)$");
    return p.matcher(s).find();
  }

  public static RulesController create(String art, String para, String text, Punish p) {
    String format = art.replace(" ", "") + "=" + para.replace(" ", "");
    if (rules.containsKey(format)) {
      return getRule(format);
    }

    rules.put(format.toLowerCase(), new RulesController(art, para, text, p));

    return getRule(art);
  }

  public RulesController getRuleByParagraphy(String paragraphy) {
    RulesController rule = null;
    for (RulesController r : getDatas()) {
      if (r.getParagraph().replace(" ", "").equalsIgnoreCase(paragraphy.replace(" ", ""))) {
        rule = r;
        break;

      }
    }
    return rule;
  }

  public RulesController getRuleByArticle(String a) {
    RulesController rule = null;
    for (RulesController r : getDatas()) {
      if (r.getArticle().replace(" ", "").equalsIgnoreCase(a.replace(" ", ""))) {
        rule = r;
        break;

      }
    }
    return rule;
  }


  public static void load() {
    RulesController.create("Art. 1º", "§ 1º", "É proibido qualquer tipo de desrespeito, tanto no âmbito moral quanto ético.", Punish.MUTE);
    RulesController.create("Art. 1º", "§ 2º", "Xingamentos intencionais,com a finalidade de ferir o próximo, não serão tolerados no servidor.", Punish.MUTE);
    RulesController.create("Art. 1º", "§ 3º", "É proibido atitudes racistas ou discriminatórias.", Punish.BAN);
    RulesController.create("Art. 1º", "§ 4º", "Desrespeitar staffs, principalmente CEOs, é proibido e imperdoável.", Punish.BAN);


    RulesController.create("Art. 2º", "§ 1º", "A divulgação de outros servidores é proibída.", Punish.MUTE);
    RulesController.create("Art. 2º", "§ 2º", "A divulgação de links suspeitos que contenham vírus, malwares ou spywares é proibída.", Punish.BAN);
    RulesController.create("Art. 2º", "§ 3º", "Divulgar conteúdo adulto em nossos canais de texto é terminantemente proibido", Punish.BAN);
    RulesController.create("Art. 2º", "§ 4º", "Divulgar imagens ou conteúdo de terceiros sem autorização é proibido.", Punish.BAN);


    RulesController.create("Art. 3º", "§ 1º", "A divulgação de outros servidores é proibída.", Punish.BAN);
    RulesController.create("Art. 3º", "§ 2º", "Ficar gritando em canais de voz ou pertubando os usuários é proibido no servidor.", Punish.BAN);
    RulesController.create("Art. 3º", "§ 3º", "Falar no canal de música não é permitido.", Punish.MUTE);
    RulesController.create("Art. 3º", "§ 4º", "Comandos só podem ser executados no seu devido canal.", Punish.NA);
    RulesController.create("Art. 3º", "§ 5º", "Não pertube staffs para entrar em suas salas entre em 'Sala para mover' e aguarde.", Punish.NA);
    RulesController.create("Art. 3º", "§ 6º", "É proibido abusar de bugs tanto de permissões quuanto de comandos.", Punish.BAN);
  }

}
