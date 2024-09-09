package com.maplestory.onecard.service.service.Impl;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.maplestory.onecard.model.domain.BattleInfo;
import com.maplestory.onecard.model.domain.CardInfo;
import com.maplestory.onecard.model.domain.UserInfo;
import com.maplestory.onecard.service.constant.OneCardConstant;
import com.maplestory.onecard.service.service.DrawCard;
import com.maplestory.onecard.service.util.ListUtils;
import com.maplestory.onecard.service.vo.BattleInfoSubOutVo;
import com.maplestory.onecard.service.vo.DrawCardInVo;
import com.maplestory.onecard.service.vo.DrawCardOutVo;
import com.maplestory.onecard.service.vo.ResponseJson;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class DrawCardImpl extends CommonService implements DrawCard {
    private final String log001 = "DrawCardImpl happened:";

    @SneakyThrows
    @Override
    public ResponseJson<DrawCardOutVo> doService(DrawCardInVo inVo) {
        log.info("{}----------交易开始--------", log001);

        UserInfo userInfo = userInfoMapper.selectByUserName(inVo.getUserName());
        if (null == userInfo) {
            log.error("{}--------用户不存在:{}-----", log001, inVo.getUserName());
            return ResponseJson.failure(OneCardConstant.Code_OtherFail, "用户不存在");
        }

        List<BattleInfo> battleInfoList = battleInfoMapper.selectByRoomNumber(inVo.getRoomNumber());
        if (battleInfoList.isEmpty()) {
            log.error("{}--------房间不存在:{}-----", log001, inVo.getRoomNumber());
            return ResponseJson.failure(OneCardConstant.Code_OtherFail, "房间不存在");
        }
        if (battleInfoList.size() > 1) {
            log.error("{}--------房间不唯一:{}-----", log001, inVo.getRoomNumber());
            return ResponseJson.failure(OneCardConstant.Code_OtherFail, "房间异常不唯一，请联系管理员");
        }
        BattleInfo battleInfo = battleInfoList.get(0);
        log.info("{}--------房间[{}]开始处理摸牌:-----", log001, inVo.getRoomNumber());

        List<CardInfo> deck = objectMapper.readValue(battleInfo.getDeck(), objectMapper.getTypeFactory().constructParametricType(List.class, CardInfo.class));
        List<UserInfo> players = objectMapper.readValue(battleInfo.getPlayers(), objectMapper.getTypeFactory().constructParametricType(List.class, UserInfo.class));
        List<CardInfo> hand = getHand(players, userInfo);
        if (!players.get(battleInfo.getTurn().intValue()).getId().equals(userInfo.getId())) {
            log.info("{}------房间[{}]此时不该[{}]出牌,", log001, battleInfo.getRoomNumber(), userInfo.getUserName());
            return ResponseJson.failure(OneCardConstant.Code_OtherFail,"未轮到出牌");
        }

        hand.addAll(getCards(deck, battleInfo.getAttackLevel() + 1));
        setHand(players,userInfo,hand);
        //如果手牌数超过20，则输了，放回牌堆
        if (hand.size() > OneCardConstant.Hand_Max) {
            deck.addAll(hand);
            for (int i = 0; i < players.size(); i++) {
                if (players.get(i).getId().equals(userInfo.getId())) {
                    players.remove(i);
                    break;
                }
            }
        }
        battleInfo.setPlayers(objectMapper.writeValueAsString(players));

        battleInfo.setTurn((battleInfo.getTurn() + battleInfo.getDirection() + players.size()) % players.size());
        battleInfo.setDeck(objectMapper.writeValueAsString(deck));
        battleInfo.setAttackLevel(0);

        battleInfoMapper.updateByPrimaryKey(battleInfo);
        DrawCardOutVo outVo = new DrawCardOutVo();
        BattleInfoSubOutVo battleInfoSubOutVo = getBattleInfoSubOutVo(battleInfo, userInfo);
        outVo.setBattleInfoSubOutVo(battleInfoSubOutVo);
        log.info("{}--------房间[{}]结束处理摸牌:-----", log001, inVo.getRoomNumber());
        return ResponseJson.ok(outVo);
    }
}
