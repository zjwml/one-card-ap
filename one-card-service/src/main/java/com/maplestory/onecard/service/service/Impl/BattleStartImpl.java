package com.maplestory.onecard.service.service.Impl;

import com.maplestory.onecard.model.domain.BattleInfo;
import com.maplestory.onecard.model.domain.CardInfo;
import com.maplestory.onecard.model.mapper.BattleInfoMapper;
import com.maplestory.onecard.model.mapper.CardInfoMapper;
import com.maplestory.onecard.service.constant.OneCardConstant;
import com.maplestory.onecard.service.service.BattleStart;
import com.maplestory.onecard.service.vo.BattleStartInVo;
import com.maplestory.onecard.service.vo.BattleStartOutVo;
import com.maplestory.onecard.service.vo.ResponseJson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class BattleStartImpl implements BattleStart {

    private final String log001 = "BattleStartImpl happened:";

    @Autowired
    private BattleInfoMapper battleInfoMapper;

    @Autowired
    private CardInfoMapper cardInfoMapper;

    @Override
    public ResponseJson<BattleStartOutVo> doService(BattleStartInVo inVo) {
        log.info("{}----------交易开始--------", log001);

        List<BattleInfo> battleInfoList = new ArrayList<>();
        try {
            log.info("{}--------开始查询是否存在这个房间-----", log001);
            battleInfoList = battleInfoMapper.selectByRoomNumber(inVo.getRoomNumber());
            log.info("{}--------结束查询是否存在这个房间-----", log001);
        } catch (Exception e) {
            log.error("{}--------查询失败:{}-----", log001, e);
            return ResponseJson.failure(OneCardConstant.Code_OtherFail, "查询用户失败" + e.getMessage());
        }
        if (battleInfoList.size() == 0) {
            log.error("{}--------房间不存在:{}-----", log001, inVo.getRoomNumber());
            return ResponseJson.failure(OneCardConstant.Code_OtherFail, "房间不存在");
        }
        if (battleInfoList.size() > 1) {
            log.error("{}--------房间不唯一:{}-----", log001, inVo.getRoomNumber());
            return ResponseJson.failure(OneCardConstant.Code_OtherFail, "房间异常不唯一，请联系管理员");
        }
        BattleInfo battleInfo = battleInfoList.get(0);
        //生成牌
        List<CardInfo> deck = new ArrayList<>();
        try {
            log.info("{}--------开始查询卡牌-----", log001);
            deck = cardInfoMapper.selectAvailable();
            log.info("{}--------结束查询卡牌-----", log001);
        } catch (Exception e) {
            log.error("{}--------查询失败:{}-----", log001, e);
            return ResponseJson.failure(OneCardConstant.Code_OtherFail, "查询卡牌失败");
        }
        //洗牌
        Collections.shuffle(deck);
        //发牌1
        battleInfo.setHand1(this.getHand(deck,6));
        //发牌2
        battleInfo.setHand2(this.getHand(deck,6));
        //发牌3
        battleInfo.setHand3(this.getHand(deck,6));
        //发牌4
        battleInfo.setHand4(this.getHand(deck,6));
        //取出第一张数字牌
        for (int i = 0; i < deck.size(); i++) {
            if (Objects.equals(deck.get(i).getCardType(), OneCardConstant.Card_Type_Digit)){
                battleInfo.setCardPrevious(deck.get(i).getId().toString());
                deck.remove(i);
                break;
            }
        }
        //放进牌堆
        battleInfo.setDeck(this.getHand(deck,deck.size()));
        //第一回合上玩家1
        battleInfo.setPlayPlayer(battleInfo.getPlayer1());
        battleInfo.setNextPlayer(battleInfo.getPlayer2());
        //更改状态
        battleInfo.setStatus(OneCardConstant.Battle_Status_battling);

        battleInfoMapper.updateByPrimaryKey(battleInfo);

        BattleStartOutVo outVo = new BattleStartOutVo();

        BeanUtils.copyProperties(battleInfo,outVo);

        return ResponseJson.ok(outVo);
    }

    private String getHand(List<CardInfo> deck,int cardNum) {
        List<CardInfo> childDeck = new ArrayList<>(deck.subList(0, cardNum));
        List<CardInfo> tmp = deck.subList(0, cardNum);
        tmp.clear();
        StringBuilder result = new StringBuilder(childDeck.get(0).getId().toString());
        for (CardInfo item : childDeck) {
            result.append(",").append(item.getId().toString());
        }
        return result.toString();
    }
}
