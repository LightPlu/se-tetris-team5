package se.tetris.team5.network;

import java.io.*;
import java.util.*;

/**
 * 최근 접속한 IP 주소 저장 및 관리
 */
public class RecentIPManager {
  private static final String IP_HISTORY_FILE = "tetris_recent_ips.txt";
  private static final int MAX_HISTORY = 10; // 최대 10개까지 저장
  
  private List<String> recentIPs;

  public RecentIPManager() {
    this.recentIPs = new ArrayList<>();
    loadHistory();
  }

  /**
   * IP 히스토리 파일에서 로드
   */
  private void loadHistory() {
    File file = new File(IP_HISTORY_FILE);
    if (!file.exists()) {
      return;
    }
    
    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
      String line;
      while ((line = reader.readLine()) != null) {
        line = line.trim();
        if (!line.isEmpty() && !recentIPs.contains(line)) {
          recentIPs.add(line);
        }
      }
      System.out.println("[RecentIPManager] Loaded " + recentIPs.size() + " recent IPs");
    } catch (IOException e) {
      System.err.println("[RecentIPManager] Error loading history: " + e.getMessage());
    }
  }

  /**
   * IP 히스토리 파일에 저장
   */
  private void saveHistory() {
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(IP_HISTORY_FILE))) {
      for (String ip : recentIPs) {
        writer.write(ip);
        writer.newLine();
      }
      System.out.println("[RecentIPManager] Saved " + recentIPs.size() + " recent IPs");
    } catch (IOException e) {
      System.err.println("[RecentIPManager] Error saving history: " + e.getMessage());
    }
  }

  /**
   * 새 IP 주소 추가
   */
  public void addIP(String ip) {
    if (ip == null || ip.trim().isEmpty()) {
      return;
    }
    
    ip = ip.trim();
    
    // 이미 있으면 제거 (맨 앞으로 이동하기 위해)
    recentIPs.remove(ip);
    
    // 맨 앞에 추가
    recentIPs.add(0, ip);
    
    // 최대 개수 제한
    while (recentIPs.size() > MAX_HISTORY) {
      recentIPs.remove(recentIPs.size() - 1);
    }
    
    saveHistory();
  }

  /**
   * 최근 IP 목록 가져오기
   */
  public List<String> getRecentIPs() {
    return new ArrayList<>(recentIPs);
  }

  /**
   * 가장 최근 IP 가져오기
   */
  public String getMostRecentIP() {
    if (recentIPs.isEmpty()) {
      return "";
    }
    return recentIPs.get(0);
  }

  /**
   * 히스토리 초기화
   */
  public void clearHistory() {
    recentIPs.clear();
    saveHistory();
  }

  /**
   * IP 유효성 검사 (간단한 형식 체크)
   */
  public static boolean isValidIP(String ip) {
    if (ip == null || ip.trim().isEmpty()) {
      return false;
    }
    
    // IPv4 형식: xxx.xxx.xxx.xxx
    String[] parts = ip.split("\\.");
    if (parts.length != 4) {
      return false;
    }
    
    try {
      for (String part : parts) {
        int num = Integer.parseInt(part);
        if (num < 0 || num > 255) {
          return false;
        }
      }
      return true;
    } catch (NumberFormatException e) {
      return false;
    }
  }
}
