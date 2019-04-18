package com.sinosun.train.service;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.sinosun.train.model.request.GetTrainStationTimeTableRequest;
import com.sinosun.train.model.response.TrainInfo;
import com.sinosun.train.model.response.TrainStationTimeTable;
import com.sinosun.train.model.response.TrainStationTimeTableResult;
import com.sinosun.train.utils.TrainHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * Created on 2019/03/08
 *
 * @author 猎隼丶止戈
 */
@Service
public class TrainStationTimeTableService {
    private static final Logger logger = LoggerFactory.getLogger(TrainStationTimeTableService.class);

    private static final String DATE_FORMAT = "yyyy-MM-dd";

    private static String baseUrl = "https://kyfw.12306.cn";
    private static String publicName = "/otn";
    private static String getTrainStationTimeTableUrlFmt = baseUrl + publicName +
            "/czxx/query?" +
            "train_start_date=%s&" +
            "train_station_name=%s&" +
            "train_station_code=%s&" +
            "randCode=";


    /**
     * 获取查询站点时刻表
     *
     * @param requestBody
     * @return
     */
    public TrainStationTimeTableResult getTrainStationTimeTable(GetTrainStationTimeTableRequest requestBody) {
        requestBody.validate();

        String trainStationName = requestBody.getTrainStationName();
        String trainStationCode = requestBody.getTrainStationCode();
        String trainStartDate = DateUtil.formatDate(requestBody.getTrainStartDate());

        return new TrainStationTimeTableResult(getAnalysisTrainStaionTimeTable(trainStationName, trainStationCode, trainStartDate));
    }

    /**
     * 查询列车站点时刻表
     *
     * @param trainStationName
     * @param trainStationCode
     * @param trainStartDate
     * @return
     */
    private TrainStationTimeTable getAnalysisTrainStaionTimeTable(String trainStationName, String trainStationCode, String trainStartDate) {
        // 生成请求链接
        String trainStaionTimeTableUrl = String.format(getTrainStationTimeTableUrlFmt, trainStartDate, trainStationName, trainStationCode);
        JSONObject ret12306 = TrainHelper.requestTo12306(trainStaionTimeTableUrl);
        JSONObject dataObj = ret12306.getJSONObject("data");

        TrainStationTimeTable trainStationTimeTable = new TrainStationTimeTable();

        // 返回结果不为空时
        JSONArray dataArray = dataObj.getJSONArray("data");
        if (!CollectionUtils.isEmpty(dataArray)) {
            List<TrainInfo> trainInfos = Lists.newArrayList();

            for (int i = 0; i < dataArray.size(); i++) {
                TrainInfo trainInfo = new TrainInfo();
                JSONObject trainStationTimeInfo = dataArray.getJSONObject(i);

                String startTrainDate = trainStationTimeInfo.getString("start_train_date");
                String trainNo = trainStationTimeInfo.getString("train_no");
                String startStationTelecode = trainStationTimeInfo.getString("start_station_telecode");
                String startStationName = trainStationTimeInfo.getString("start_station_name");
                String startStartTime = trainStationTimeInfo.getString("start_start_time");
                String endStationTelecode = trainStationTimeInfo.getString("end_station_telecode");
                String endStationName = trainStationTimeInfo.getString("end_station_name");
                String endArriveTime = trainStationTimeInfo.getString("end_arrive_time");
                String trainTypeCode = trainStationTimeInfo.getString("train_type_code");
                String trainTypeName = trainStationTimeInfo.getString("train_type_name");
                String trainClassCode = trainStationTimeInfo.getString("train_class_code");
                String trainClassName = trainStationTimeInfo.getString("train_class_name");
                String stationNo = trainStationTimeInfo.getString("station_no");
                String stationName = trainStationTimeInfo.getString("station_name");
                String stationTelecode = trainStationTimeInfo.getString("station_telecode");
                String stationTrainCode = trainStationTimeInfo.getString("station_train_code");
                String arriveDayDiff = trainStationTimeInfo.getString("arrive_day_diff");
                String arriveTime = trainStationTimeInfo.getString("arrive_time");
                String startTime = trainStationTimeInfo.getString("start_time");
                String startDayDiff = trainStationTimeInfo.getString("start_day_diff");
                String stopoverTime = trainStationTimeInfo.getString("stopover_time");
                String runningTime = trainStationTimeInfo.getString("running_time");
                String seatTypes = trainStationTimeInfo.getString("seat_types");
                String serviceType = trainStationTimeInfo.getString("service_type");
                String serviceTypeStr = "0".equals(serviceType) ? "无空调" : "有空调";

                trainInfo.setStartTrainDate(startTrainDate);
                trainInfo.setTrainNo(trainNo);
                trainInfo.setStartStationTelecode(startStationTelecode);
                trainInfo.setStartStationName(startStationName);
                trainInfo.setStartStartTime(startStartTime);
                trainInfo.setEndStationTelecode(endStationTelecode);
                trainInfo.setEndStationName(endStationName);
                trainInfo.setEndArriveTime(endArriveTime);
                trainInfo.setTrainTypeCode(trainTypeCode);
                trainInfo.setTrainTypeName(trainTypeName);
                trainInfo.setTrainClassCode(trainClassCode);
                trainInfo.setTrainClassName(trainClassName);
                trainInfo.setStationNo(stationNo);
                trainInfo.setStationName(stationName);
                trainInfo.setStationTelecode(stationTelecode);
                trainInfo.setStationTrainCode(stationTrainCode);
                trainInfo.setArriveDayDiff(arriveDayDiff);
                trainInfo.setArriveTime(arriveTime);
                trainInfo.setStartTime(startTime);
                trainInfo.setStartDayDiff(startDayDiff);
                trainInfo.setStopoverTime(stopoverTime);
                trainInfo.setRunningTime(runningTime);
                trainInfo.setSeatTypes(seatTypes);
                trainInfo.setServiceType(serviceType);
                trainInfo.setServiceTypeStr(serviceTypeStr);

                trainInfos.add(trainInfo);
            }

            trainStationTimeTable.setTrainInfos(trainInfos);
        }

        trainStationTimeTable.setSameStations(dataObj.getJSONArray("sameStations").toJavaList(String.class));

        return trainStationTimeTable;
    }

}
