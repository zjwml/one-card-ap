package com.maplestory.onecard.controller;

import com.maplestory.onecard.service.service.BattleQuery;
import com.maplestory.onecard.service.service.BattleStart;
import com.maplestory.onecard.service.service.DrawCard;
import com.maplestory.onecard.service.service.PlayCard;
import com.maplestory.onecard.service.vo.BattleQueryInVo;
import com.maplestory.onecard.service.vo.BattleQueryOutVo;
import com.maplestory.onecard.service.vo.BattleStartInVo;
import com.maplestory.onecard.service.vo.BattleStartOutVo;
import com.maplestory.onecard.service.vo.DrawCardInVo;
import com.maplestory.onecard.service.vo.DrawCardOutVo;
import com.maplestory.onecard.service.vo.PlayCardInVo;
import com.maplestory.onecard.service.vo.PlayCardOutVo;
import com.maplestory.onecard.service.vo.ResponseJson;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/battle")
@RestController
@Slf4j
public class BattleController {

    @Autowired
    private BattleStart battleStart;

    @Autowired
    private PlayCard playCard;

    @Autowired
    private DrawCard drawCard;

    @Autowired
    private BattleQuery battleQuery;

    @PostMapping(value = "/query")
    @ResponseBody
    @CrossOrigin
    public ResponseJson<BattleQueryOutVo> query(@RequestBody @Valid BattleQueryInVo inVo) {

        return battleQuery.doService(inVo);
    }

    @PostMapping(value = "/start")
    @ResponseBody
    @CrossOrigin
    public ResponseJson<BattleStartOutVo> start(@RequestBody @Valid BattleStartInVo inVo) {

        return battleStart.doService(inVo);
    }

    @PostMapping(value = "/play")
    @ResponseBody
    @CrossOrigin
    public ResponseJson<PlayCardOutVo> play(@RequestBody @Valid PlayCardInVo inVo) {

        return playCard.doService(inVo);
    }

    @PostMapping(value = "/draw")
    @ResponseBody
    @CrossOrigin
    public ResponseJson<DrawCardOutVo> draw(@RequestBody @Valid DrawCardInVo inVo) {

        return drawCard.doService(inVo);
    }

}
