package com.linkloving.rtring_new.logic.UI.ranking.rankinglistitem;

/**
 * Created by ZJC on 2016/3/17.
 */
public class RankingVO {
    private String user_id, nickname, user_sex, distance, rank, rank_time, user_avatar_file_name, ent_portal_url, ent_name, what_s_up, zan, yizan;
    private Integer step;
    private boolean virtual;

    public String getUser_id()
    {
        return user_id;
    }

    public void setUser_id(String user_id)
    {
        this.user_id = user_id;
    }

    public String getNickname()
    {
        return nickname;
    }

    public void setNickname(String nickname)
    {
        this.nickname = nickname;
    }

    public String getUser_sex()
    {
        return user_sex;
    }

    public void setUser_sex(String user_sex)
    {
        this.user_sex = user_sex;
    }

    public String getDistance()
    {
        return distance;
    }

    public void setDistance(String distance)
    {
        this.distance = distance;
    }

    public String getRank()
    {
        return rank;
    }

    public void setRank(String rank)
    {
        this.rank = rank;
    }

    public String getRank_time()
    {
        return rank_time;
    }

    public void setRank_time(String rank_time)
    {
        this.rank_time = rank_time;
    }

    public String getUser_avatar_file_name()
    {
        return user_avatar_file_name;
    }

    public void setUser_avatar_file_name(String user_avatar_file_name)
    {
        this.user_avatar_file_name = user_avatar_file_name;
    }

    public String getEnt_portal_url()
    {
        return ent_portal_url;
    }

    public void setEnt_portal_url(String ent_portal_url)
    {
        this.ent_portal_url = ent_portal_url;
    }

    public String getWhat_s_up()
    {
        return what_s_up;
    }

    public void setWhat_s_up(String what_s_up)
    {
        this.what_s_up = what_s_up;
    }

    public String getZan()
    {
        return zan;
    }

    public void setZan(String zan)
    {
        this.zan = zan;
    }

    public String getYizan()
    {
        return yizan;
    }

    public void setYizan(String yizan)
    {
        this.yizan = yizan;
    }

    public boolean isVirtual()
    {
        return virtual;
    }

    public void setVirtual(boolean virtual)
    {
        this.virtual = virtual;
    }

    public Integer getStep()
    {
        return step;
    }

    public void setStep(Integer step)
    {
        this.step = step;
    }

    public String getEnt_name()
    {
        return ent_name;
    }

    public void setEnt_name(String ent_name)
    {
        this.ent_name = ent_name;
    }

    @Override
    public String toString() {
        return "RankingVO{" +
                "distance='" + distance + '\'' +
                ", user_id='" + user_id + '\'' +
                ", nickname='" + nickname + '\'' +
                ", user_sex='" + user_sex + '\'' +
                ", rank='" + rank + '\'' +
                ", rank_time='" + rank_time + '\'' +
                ", user_avatar_file_name='" + user_avatar_file_name + '\'' +
                ", ent_portal_url='" + ent_portal_url + '\'' +
                ", ent_name='" + ent_name + '\'' +
                ", what_s_up='" + what_s_up + '\'' +
                ", zan='" + zan + '\'' +
                ", yizan='" + yizan + '\'' +
                ", step=" + step +
                ", virtual=" + virtual +
                '}';
    }
}
