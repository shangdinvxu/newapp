package com.linkloving.rtring_new.db.sport;

import com.linkloving.band.dto.SportRecord;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zkx on 2016/3/15.
 */
public interface SportSqlInterface {

    /**（查）
     * 通过userId获取数据库中未上传数据集合
     * @param userId
     * @return
     */
        ArrayList<SportRecord> findHistoryWitchNoSync(String userId);

    /**（查）
     * 返回ArrayList<DaySynopic>记录.
     * @param userId 本地数据的所有者账号，本条件是读取本地数据的先决条件，否则就窜数据了！
     * @return
     */
        ArrayList<SportRecord> findHistory(String userId, String condition);

    /**  （增）
     *  应用层直接调用的插入数据库方法
     * @param list 运动数据集合
     * @param userId
     * @param hasSycToServer
     */
        void insertSportRecords(List<SportRecord> list, String userId, boolean hasSycToServer);

    /** （删）
     * 删除数据.
     * @param userId 本地数据的所有者账号，本条件是读取本地数据的先决条件，否则就窜数据了！
     * @param startDateTime >=开始时间
     * @param endDateTime <=结束时间
     * @param hasSycToServer true表示即使该数据未上传到服务端也被删除？
     * @return
     */
        long deleteDaySynopicWithRange(String userId, String startDateTime, String endDateTime , boolean hasSycToServer);

    /** （改）
     * 将指定时间范围内的数据标识为“已上传”。
     * @param userId
     * @param startTime 开始时间（>=）
     * @param endTime 结束时间（<=）
     * @return
     */
        long updateForSynced(String userId, String startTime , String endTime);
}
