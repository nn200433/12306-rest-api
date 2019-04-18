package com.sinosun.train.model.response;

import com.alibaba.fastjson.PropertyNamingStrategy;
import com.alibaba.fastjson.annotation.JSONType;

import java.util.List;

/**
 * 列车时刻Entity
 * <p>
 * Created on 2019/03/07 09:52:18
 *
 * @author 猎隼丶止戈
 */
@JSONType(naming = PropertyNamingStrategy.PascalCase)
public class TrainStationTimeTable {

    /**
     * 动车信息
     */
    private List<TrainInfo> trainInfos;

    /**
     * 同处车站
     */
    private List<String> sameStations;

    public List<TrainInfo> getTrainInfos() {
        return trainInfos;
    }

    public void setTrainInfos(List<TrainInfo> trainInfos) {
        this.trainInfos = trainInfos;
    }

    public List<String> getSameStations() {
        return sameStations;
    }

    public void setSameStations(List<String> sameStations) {
        this.sameStations = sameStations;
    }
}
