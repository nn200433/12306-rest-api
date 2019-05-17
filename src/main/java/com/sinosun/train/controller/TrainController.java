package com.sinosun.train.controller;

import com.alibaba.fastjson.JSONObject;
import com.sinosun.train.model.request.*;
import com.sinosun.train.model.response.*;
import com.sinosun.train.service.TrainStationService;
import com.sinosun.train.service.TrainStationTimeTableService;
import com.sinosun.train.service.TrainTicketService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

/**
 * Created on 2019/1/10 20:05.
 *
 * @author caogu
 */
@Api(value = "train", description = "12306接口")
@RestController
@RequestMapping(value = "/train/",
        method = RequestMethod.POST,
        produces = {MediaType.APPLICATION_JSON_VALUE},
        consumes = {MediaType.APPLICATION_JSON_VALUE}
)
public class TrainController {

    private static final Logger logger = LoggerFactory.getLogger(TrainController.class);

    @Autowired
    private TrainStationService trainStationService;

    @Autowired
    private TrainTicketService trainTicketService;

    @Autowired
    private TrainStationTimeTableService trainStationTimeTableService;

    /**
     * 获取所有动车站
     *
     * @param requestBody
     * @return
     */
    @ApiOperation(value = "获取所有动车站", notes = "请求参数如下：{}")
    @PostMapping(value = "getAllCity")
    public StationResult getAllCityHandler(@RequestBody JSONObject requestBody) {
        return trainStationService.getAllCity(requestBody.toJavaObject(NoneRequest.class));
    }

    /**
     * 获取热门动车站
     *
     * @param requestBody
     * @return
     */
    @ApiOperation(value = "获取热门动车站", notes = "请求参数如下：{}")
    @PostMapping(value = "getHotCity")
    public StationResult getHotCityHandler(@RequestBody JSONObject requestBody) {
        return trainStationService.getHotCity(requestBody.toJavaObject(NoneRequest.class));
    }

    /**
     * 获取 列车号 - 车次 映射关系表
     *
     * @param requestBody
     * @return
     */
    @ApiOperation(value = "获取 列车号 - 车次 映射关系表", notes = "请求参数如下：{}")
    @PostMapping(value = "getTrainCode")
    public TrainCodeResult getTrainCodeHandler(@RequestBody JSONObject requestBody) {
        return trainStationService.getAllTrainCode(requestBody.toJavaObject(NoneRequest.class));
    }

    /**
     * 动车站搜索
     *
     * @param requestBody
     * @return
     */
    @ApiOperation(value = "动车站搜索", notes = "请求参数如下：{\n" +
            "    \"Keyword\":\"福州\"\n" +
            "}")
    @PostMapping(value = "searchCity")
    public StationResult searchCityHandler(@RequestBody JSONObject requestBody) {
        return trainStationService.searchCity(requestBody.toJavaObject(SearchCityRequest.class));
    }

    /**
     * 获取动车票
     *
     * @param requestBody
     * @return
     */
    @ApiOperation(value = "获取动车票", notes = "请求参数如下：{\n" +
            "    \"FromStationCode\":\"FYS\",\n" +
            "    \"ToStationCode\":\"FZS\",\n" +
            "    \"FromDate\":\"2019-03-25\",\n" +
            "    \"IsStudent\":true\n" +
            "}")
    @PostMapping(value = "getTicketList")
    public TicketListResult getTicketListHandler(@RequestBody JSONObject requestBody) {
        return trainTicketService.getTicketList(requestBody.toJavaObject(GetTicketListRequest.class));
    }

    /**
     * 获取动车经停站信息
     *
     * @param requestBody
     * @return
     */
    @ApiOperation(value = "获取动车经停站信息", notes = "请求参数如下：{\n" +
            "    \"TrainCode\":\"G1670\",\n" +
            "    \"FromStationCode\":\"FYS\",\n" +
            "    \"ToStationCode\":\"AOH\",\n" +
            "    \"FromDate\":\"2019-03-25\"\n" +
            "}")
    @PostMapping(value = "getTrainLine")
    public TrainLineResult getTrainLineHandler(@RequestBody JSONObject requestBody) {
        return trainTicketService.getTrainLine(requestBody.toJavaObject(GetTrainLineRequest.class));
    }

    /**
     * 获取动车站时刻表
     *
     * @param requestBody
     * @return
     */
    @ApiOperation(value = "获取动车站时刻表", notes = "请求参数如下：{\n" +
            "    \"TrainCode\": \"G1670\",\n" +
            "    \"FromStationCode\": \"FYS\",\n" +
            "    \"ToStationCode\": \"AOH\",\n" +
            "    \"FromDate\": \"2019-03-25\"\n" +
            "}")
    @PostMapping(value = "getTrainStationTimeTable")
    public TrainStationTimeTableResult getTrainStationTimeTable(@RequestBody JSONObject requestBody) {
        return trainStationTimeTableService.getTrainStationTimeTable(requestBody.toJavaObject(GetTrainStationTimeTableRequest.class));
    }
}
