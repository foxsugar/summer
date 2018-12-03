package com.code.server.login.service;

import com.code.server.db.Service.ClubService;
import com.code.server.db.Service.UserClubService;
import com.code.server.db.model.Club;
import com.code.server.db.model.UserClub;
import com.code.server.login.config.ServerConfig;
import com.code.server.redis.service.RedisManager;
import com.code.server.util.SpringUtil;

import java.util.*;

/**
 * Created by sunxianping on 2018/1/16.
 */
public class ClubManager {


    private static ClubManager instance;

    private Map<String, Club> clubMap = new HashMap<>();

    private UserClub userClub;


    public synchronized static ClubManager getInstance() {
        if (instance == null) {
            instance = new ClubManager();
        }
        return instance;
    }

    private ClubManager() {

    }


    public String getClubId() {

        while (true) {
            String id = genId();

            boolean isHas = clubMap.containsKey(id);

            if (!isHas) {
                return id;
            }
        }

    }

    /**
     * 从数据库读取俱乐部数据
     */
    public void loadAll() {
        //是否带钱的俱乐部
        boolean loadClubMoney = SpringUtil.getBean(ServerConfig.class).getHasClubMoney() == 1;
        ClubService clubService = SpringUtil.getBean(ClubService.class);
        clubService.getClubDao().findAll().forEach(club -> {
            clubMap.put(club.getId(), club);
            //redis里没数据
            if (loadClubMoney && RedisManager.getClubRedisService().getUserMoneyCount(club.getId()) == 0) {
                clubMoneyWrite2Redis(club);
            }

        });
    }

    /**
     * 俱乐部钱写进redis
     *
     * @param club
     */
    private void clubMoneyWrite2Redis(Club club) {
        club.getClubInfo().getMember().values().forEach(clubMember -> {
            RedisManager.getClubRedisService().addClubUserMoney(club.getId(), clubMember.getUserId(), clubMember.getMoney());
        });
    }

    public void saveAll() {
        //存 玩家 俱乐部 映射关系
        if (userClub != null) {

            UserClubService userClubService = SpringUtil.getBean(UserClubService.class);
            userClubService.getUserClubDao().save(userClub);
        }

        //存俱乐部
        ClubService clubService = SpringUtil.getBean(ClubService.class);

        boolean saveClubMoney = SpringUtil.getBean(ServerConfig.class).getHasClubMoney() == 1;
        if (saveClubMoney) {
            for (Club club : clubMap.values()) {
                redis2ClubMoney(club);
            }
        }

        clubService.getClubDao().save(clubMap.values());

    }


    /**
     * redis 写入俱乐部钱
     *
     * @param club
     */
    private void redis2ClubMoney(Club club) {
        Map<String, String> money = RedisManager.getClubRedisService().getMoneyMap(club.getId());
        club.getClubInfo().getMember().values().forEach(clubMember -> {
            if (money.containsKey("" + clubMember.getUserId())) {
                clubMember.setMoney(Double.valueOf(money.get("" + clubMember.getUserId())));
            }
        });
    }

    public Club getClubById(String id) {
        return clubMap.get(id);
    }

    /**
     * 获得玩家俱乐部
     *
     * @return
     */
    public UserClub getUserClub() {
        if (userClub == null) {

            UserClubService userClubService = SpringUtil.getBean(UserClubService.class);
            UserClub userClub = userClubService.getUserClubById(1);
            if (userClub == null) {
                userClub = new UserClub();
                userClub.setId(1);
                userClub.setUser_club(new HashMap<>());
                userClubService.save(userClub);
            } else {
                for (Map.Entry<String, List<String>> entry : userClub.getUser_club().entrySet()) {
                    Set<String> set = new HashSet<>();
                    set.addAll(entry.getValue());
                    List<String> newList = new ArrayList<>();
                    newList.addAll(set);
                    entry.setValue(newList);
                }
                userClubService.save(userClub);
            }
            this.userClub = userClub;
        }
        return userClub;
    }

    /**
     * 得到俱乐部数量
     *
     * @param userId
     * @return
     */
    public int getUserClubNum(long userId) {
        UserClub userClub = getUserClub();
        int size = 0;
        if (userClub.getUser_club().containsKey("" + userId)) {
            size = userClub.getUser_club().get("" + userId).size();
        }
        return size;
    }

    /**
     * 得到玩家的俱乐部
     *
     * @param userId
     * @return
     */
    public List<String> getUserClubs(long userId) {
        UserClub userClub = getUserClub();
        if (userClub.getUser_club().containsKey("" + userId)) {
            return userClub.getUser_club().get("" + userId);
        }
        return new ArrayList<>();
    }

    /**
     * 玩家加新的俱乐部
     *
     * @param userId
     * @param clubId
     */
    public void userAddClub(long userId, String clubId) {
        UserClub userClub = getUserClub();
        if (!userClub.getUser_club().containsKey("" + userId)) {
            List<String> li = new ArrayList<>();
            li.add(clubId);
            userClub.getUser_club().put("" + userId, li);
        } else {
            List<String> l = userClub.getUser_club().get("" + userId);
            l.add(clubId);
            userClub.getUser_club().put("" + userId, l);
        }
    }

    public void userRemoveClub(long userId, String clubId) {
        List<String> clubs = getUserClubs(userId);
        if (clubs != null) {
            clubs.remove(clubId);
        }
    }


    /**
     * 生成俱乐部id
     *
     * @return
     */
    private String genId() {
        int id = new Random().nextInt(999999);
        String s = "000000" + id;
        int len = s.length();
        return s.substring(len - 6, len);
    }


    public Map<String, Club> getClubMap() {
        return clubMap;
    }

    public ClubManager setClubMap(Map<String, Club> clubMap) {
        this.clubMap = clubMap;
        return this;
    }
}
