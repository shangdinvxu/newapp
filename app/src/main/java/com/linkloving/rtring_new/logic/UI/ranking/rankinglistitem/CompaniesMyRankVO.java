package com.linkloving.rtring_new.logic.UI.ranking.rankinglistitem;

/**
 * Created by Linkloving on 2016/3/29.
 */
public class CompaniesMyRankVO
{
    private String distance;//距离
    private String rank;//排名
    private String zan;//赞

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public String getZan() {
        return zan;
    }

    public void setZan(String zan) {
        this.zan = zan;
    }

    @Override
    public String toString() {
        return "CompaniesMyRankVO{" +
                "distance='" + distance + '\'' +
                ", rank='" + rank + '\'' +
                ", zan='" + zan + '\'' +
                '}';
    }
}
