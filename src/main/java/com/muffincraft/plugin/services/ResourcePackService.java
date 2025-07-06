package com.muffincraft.plugin.services;

import com.muffincraft.plugin.MuffinCraftPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;
import org.bukkit.Bukkit;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.HexFormat;

/**
 * 리소스팩 관리 서비스
 * 플레이어 접속 시 자동으로 리소스팩을 적용합니다
 */
public class ResourcePackService implements Listener {
    private final MuffinCraftPlugin plugin;
    private String resourcePackUrl;
    private String resourcePackHash;
    private boolean resourcePackRequired;

    public ResourcePackService(MuffinCraftPlugin plugin) {
        this.plugin = plugin;
        loadConfig();
        
        // 백엔드에서 최신 리소스팩 정보 가져오기
        updateResourcePackFromBackend();
        
        // 이벤트 리스너 등록
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    /**
     * 설정에서 리소스팩 정보를 로드합니다
     */
    private void loadConfig() {
        this.resourcePackUrl = plugin.getConfig().getString("resourcepack.url", "");
        this.resourcePackHash = plugin.getConfig().getString("resourcepack.sha1", "");
        this.resourcePackRequired = plugin.getConfig().getBoolean("resourcepack.required", true);

        // 설정이 비어있다면 기본값 설정
        if (resourcePackUrl.isEmpty()) {
            resourcePackUrl = "http://your-server.com/muffincraft-resourcepack.zip";
            plugin.getConfig().set("resourcepack.url", resourcePackUrl);
        }
        
        if (resourcePackHash.isEmpty()) {
            resourcePackHash = generateDummyHash();
            plugin.getConfig().set("resourcepack.sha1", resourcePackHash);
        }
        
        plugin.getConfig().set("resourcepack.required", resourcePackRequired);
        plugin.saveConfig();

        plugin.getLogger().info("리소스팩 URL: " + resourcePackUrl);
        plugin.getLogger().info("리소스팩 필수 여부: " + resourcePackRequired);
    }

    /**
     * 플레이어 접속 시 리소스팩 적용
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        
        // 약간의 지연 후 리소스팩 적용 (접속 완료 후)
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            applyResourcePack(player);
            
            // 디버깅 정보 출력
            plugin.getLogger().info("플레이어 " + player.getName() + "에게 리소스팩 적용 시도");
            plugin.getLogger().info("리소스팩 URL: " + resourcePackUrl);
            plugin.getLogger().info("리소스팩 해시: " + resourcePackHash);
        }, 40L); // 2초 후
    }

    /**
     * 플레이어에게 리소스팩을 적용합니다
     */
    @SuppressWarnings("deprecation")
    public void applyResourcePack(Player player) {
        try {
            if (resourcePackUrl.isEmpty() || resourcePackUrl.equals("http://your-server.com/muffincraft-resourcepack.zip")) {
                player.sendMessage("§e리소스팩이 아직 설정되지 않았습니다.");
                player.sendMessage("§7관리자에게 문의하여 리소스팩을 설정해주세요.");
                return;
            }

            player.sendMessage("§aMuffinCraft 리소스팩을 다운로드 중입니다...");
            player.sendMessage("§7잠시만 기다려주세요.");

            // 리소스팩 적용 (deprecated 메서드지만 가장 안정적)
            try {
                if (resourcePackHash != null && !resourcePackHash.isEmpty() && !resourcePackHash.equals("dummy-hash")) {
                    // 해시가 있는 경우 해시와 함께 전송
                    byte[] hashBytes = HexFormat.of().parseHex(resourcePackHash);
                    player.setResourcePack(resourcePackUrl, hashBytes, resourcePackRequired);
                } else {
                    // 해시가 없는 경우 URL만 전송
                    player.setResourcePack(resourcePackUrl, (byte[])null, resourcePackRequired);
                }
                
                plugin.getLogger().info(player.getName() + "에게 리소스팩 전송: " + resourcePackUrl);
                
            } catch (Exception hashError) {
                // 해시 처리 실패 시 URL만으로 전송
                player.setResourcePack(resourcePackUrl, (byte[])null, resourcePackRequired);
                plugin.getLogger().warning("해시 처리 실패, URL만으로 리소스팩 전송: " + hashError.getMessage());
            }
            
        } catch (Exception e) {
            plugin.getLogger().warning("리소스팩 적용 중 오류 발생: " + e.getMessage());
            player.sendMessage("§c리소스팩 적용 중 오류가 발생했습니다.");
        }
    }

    /**
     * 리소스팩 상태 변경 이벤트 처리
     */
    @EventHandler
    public void onResourcePackStatus(PlayerResourcePackStatusEvent event) {
        Player player = event.getPlayer();
        PlayerResourcePackStatusEvent.Status status = event.getStatus();

        plugin.getLogger().info(player.getName() + "의 리소스팩 상태: " + status);

        switch (status) {
            case SUCCESSFULLY_LOADED:
                player.sendMessage("§aMuffinCraft 리소스팩이 성공적으로 적용되었습니다!");
                player.sendMessage("§6이제 커스텀 아이템들을 올바르게 볼 수 있습니다.");
                break;
                
            case DECLINED:
                if (resourcePackRequired) {
                    player.sendMessage("§c리소스팩이 필요합니다!");
                    player.sendMessage("§c게임을 정상적으로 즐기려면 리소스팩을 적용해주세요.");
                    // 필요하다면 여기서 플레이어를 킥하거나 제한을 둘 수 있습니다
                    // player.kickPlayer("리소스팩이 필요합니다.");
                } else {
                    player.sendMessage("§e리소스팩을 거부했습니다. 일부 아이템이 올바르게 표시되지 않을 수 있습니다.");
                }
                break;
                
            case FAILED_DOWNLOAD:
                player.sendMessage("§c리소스팩 다운로드에 실패했습니다.");
                player.sendMessage("§7인터넷 연결을 확인하거나 관리자에게 문의해주세요.");
                plugin.getLogger().warning(player.getName() + "의 리소스팩 다운로드 실패: " + resourcePackUrl);
                break;
                
            case ACCEPTED:
                player.sendMessage("§a리소스팩 다운로드를 시작합니다...");
                break;
                
            case INVALID_URL:
                player.sendMessage("§c리소스팩 URL이 잘못되었습니다.");
                plugin.getLogger().warning("리소스팩 URL이 잘못됨: " + resourcePackUrl);
                break;
                
            case FAILED_RELOAD:
                player.sendMessage("§c리소스팩 적용에 실패했습니다.");
                plugin.getLogger().warning(player.getName() + "의 리소스팩 적용 실패");
                break;
                
            case DISCARDED:
                player.sendMessage("§e리소스팩이 취소되었습니다.");
                break;
                
            default:
                plugin.getLogger().info("알 수 없는 리소스팩 상태: " + status);
                break;
        }
    }

    /**
     * 리소스팩 URL을 업데이트합니다
     */
    public void updateResourcePackUrl(String url) {
        this.resourcePackUrl = url;
        plugin.getConfig().set("resourcepack.url", url);
        plugin.saveConfig();
        plugin.getLogger().info("리소스팩 URL이 업데이트되었습니다: " + url);
    }

    /**
     * 리소스팩 해시를 업데이트합니다
     */
    public void updateResourcePackHash(String hash) {
        this.resourcePackHash = hash;
        plugin.getConfig().set("resourcepack.sha1", hash);
        plugin.saveConfig();
        plugin.getLogger().info("리소스팩 해시가 업데이트되었습니다.");
    }

    /**
     * 백엔드에서 리소스팩 정보를 가져와서 설정을 업데이트합니다
     */
    public void updateResourcePackFromBackend() {
        try {
            // 백엔드 API URL 구성
            String apiUrl = plugin.getConfig().getString("api.url", "http://localhost:4000/api");
            String infoUrl = apiUrl + "/resourcepack/info";
            
            plugin.getLogger().info("백엔드에서 리소스팩 정보를 가져오는 중: " + infoUrl);
            
            // HTTP 요청으로 리소스팩 정보 가져오기
            // 여기서는 간단히 설정된 URL을 사용하되, 실제로는 HTTP 클라이언트로 요청해야 함
            String backendUrl = apiUrl + "/resourcepack/download";
            
            // 설정 업데이트
            this.resourcePackUrl = backendUrl;
            plugin.getConfig().set("resourcepack.url", backendUrl);
            plugin.saveConfig();
            
            plugin.getLogger().info("리소스팩 URL이 백엔드로 업데이트되었습니다: " + backendUrl);
            
        } catch (Exception e) {
            plugin.getLogger().warning("백엔드에서 리소스팩 정보를 가져오는데 실패했습니다: " + e.getMessage());
            plugin.getLogger().warning("기본 설정을 사용합니다.");
        }
    }

    /**
     * 로컬 리소스팩 파일의 SHA-1 해시를 계산합니다
     */
    public String calculateFileHash(String filePath) {
        try {
            Path path = Paths.get(filePath);
            if (!Files.exists(path)) {
                plugin.getLogger().warning("리소스팩 파일을 찾을 수 없습니다: " + filePath);
                return null;
            }

            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            byte[] hash = digest.digest(Files.readAllBytes(path));
            return HexFormat.of().formatHex(hash);
            
        } catch (Exception e) {
            plugin.getLogger().warning("리소스팩 해시 계산 중 오류: " + e.getMessage());
            return null;
        }
    }

    /**
     * 더미 해시 생성 (실제 파일이 없을 때 사용)
     */
    private String generateDummyHash() {
        return "dummy-hash";
    }

    // Getters
    public String getResourcePackUrl() { return resourcePackUrl; }
    public String getResourcePackHash() { return resourcePackHash; }
    public boolean isResourcePackRequired() { return resourcePackRequired; }
}
