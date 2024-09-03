package com.maplestory.onecard.service.service.Impl;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.maplestory.onecard.model.domain.BattleInfo;
import com.maplestory.onecard.model.domain.CardInfo;
import com.maplestory.onecard.model.domain.UserInfo;
import com.maplestory.onecard.service.constant.OneCardConstant;
import com.maplestory.onecard.service.service.DrawCard;
import com.maplestory.onecard.service.util.ListUtils;
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
        ObjectNode hands = (ObjectNode) objectMapper.readTree(battleInfo.getHands());
        String str = hands.get(userInfo.getId().toString()).asText();
        List<CardInfo> hand = objectMapper.readValue(str, objectMapper.getTypeFactory().constructParametricType(List.class, CardInfo.class));

        hand.addAll(getCards(deck, battleInfo.getAttackLevel() + 1));
        if (hand.size() > OneCardConstant.Hand_Max) {
            deck.addAll(hand);
            hands.remove(userInfo.getId().toString());
            List<String> players = ListUtils.StringToStringList(battleInfo.getPlayers());
            for (int i = 0; i < players.size(); i++) {
                if (players.get(i).equals(userInfo.getId().toString())) {
                    players.remove(i);
                    break;
                }
            }
            battleInfo.setPlayers(ListUtils.StringListToString(players));
        } else {
            hands.put(userInfo.getId().toString(), objectMapper.writeValueAsString(hand));
        }

        battleInfo.setHands(objectMapper.writeValueAsString(hands));
        battleInfo.setDeck(objectMapper.writeValueAsString(deck));
        battleInfo.setAttackLevel(0);

        battleInfoMapper.updateByPrimaryKey(battleInfo);
        DrawCardOutVo outVo = new DrawCardOutVo();
        BeanUtils.copyProperties(battleInfo, outVo);
        log.info("{}--------房间[{}]结束处理摸牌:-----", log001, inVo.getRoomNumber());
        return ResponseJson.ok(outVo);
    }
}
