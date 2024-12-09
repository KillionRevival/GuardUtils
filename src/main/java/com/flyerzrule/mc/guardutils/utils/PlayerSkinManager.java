package com.flyerzrule.mc.guardutils.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.net.URI;
import java.net.HttpURLConnection;
import java.io.InputStreamReader;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.bukkit.entity.Player;

public class PlayerSkinManager {
  private static PlayerSkinManager instance;

  private Map<UUID, String> playerSkinCache;

  private PlayerSkinManager() {
    this.playerSkinCache = new HashMap<>();
  }

  public static PlayerSkinManager getInstance() {
    if (instance == null) {
      instance = new PlayerSkinManager();
    }
    return instance;
  }

  public String getPlayerSkin(Player player) {
    UUID uuid = player.getUniqueId();
    // Check if player is in cache
    if (playerSkinCache.containsKey(uuid)) {
      return playerSkinCache.get(uuid);
    }

    // If not in cache, get skin from Mojang API
    String skinUrl = retreiveSkinUrl(player);
    if (skinUrl == null) {
      return null;
    }
    addToCache(player, skinUrl);
    return skinUrl;
  }

  private void addToCache(Player player, String skinUrl) {
    UUID uuid = player.getUniqueId();
    playerSkinCache.put(uuid, skinUrl);
  }

  private String retreiveSkinUrl(Player player) {
      UUID uuid = player.getUniqueId();
      JsonObject jsonObject = getRequest("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid
                      + "?unsigned=false");

      if (jsonObject == null) {
        return null;
      }

      JsonObject properties = jsonObject.get("properties").getAsJsonArray().get(0).getAsJsonObject();
      return properties.get("value").getAsString();
  }

  private JsonObject getRequest(String url) {
    try {
      URI uri = URI.create(url);
      HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection();
      connection.setReadTimeout(5000);
      connection.setConnectTimeout(5000);
      connection.setRequestMethod("GET");
      connection.setDoOutput(true);
      InputStreamReader reader = new InputStreamReader(connection.getInputStream());
      return JsonParser.parseReader(reader).getAsJsonObject();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }
}
