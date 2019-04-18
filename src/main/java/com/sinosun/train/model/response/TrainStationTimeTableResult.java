package com.sinosun.train.model.response;

import com.alibaba.fastjson.PropertyNamingStrategy;
import com.alibaba.fastjson.annotation.JSONType;
import com.google.common.base.MoreObjects;

/**
 * Created on 2019/03/07 09:52:18
 *
 * @author 猎隼丶止戈
 */
@JSONType(naming = PropertyNamingStrategy.PascalCase)
public class TrainStationTimeTableResult extends BaseResult {

    private TrainStationTimeTable result;

    public TrainStationTimeTableResult() {
    }

    public TrainStationTimeTableResult(TrainStationTimeTable result) {
        this.result = result;
    }

    public TrainStationTimeTable getResult() {
        return result;
    }

    public void setResult(TrainStationTimeTable result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("result", result)
                .toString();
    }
}
