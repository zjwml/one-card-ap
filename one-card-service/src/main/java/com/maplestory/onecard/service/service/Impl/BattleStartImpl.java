package com.maplestory.onecard.service.service.Impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.maplestory.onecard.model.domain.BattleInfo;
import com.maplestory.onecard.model.domain.CardInfo;
import com.maplestory.onecard.model.domain.UserInfo;
import com.maplestory.onecard.service.constant.OneCardConstant;
import com.maplestory.onecard.service.service.BattleStart;
import com.maplestory.onecard.service.util.ListUtils;
import com.maplestory.onecard.service.vo.BattleInfoSubOutVo;
import com.maplestory.onecard.service.vo.BattleStartInVo;
import com.maplestory.onecard.service.vo.BattleStartOutVo;
import com.maplestory.onecard.service.vo.ResponseJson;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class BattleStartImpl extends CommonService implements BattleStart {

    private final String log001 = "BattleStartImpl happened:";

    @SneakyThrows
    @Override
    public ResponseJson<BattleStartOutVo> doService(BattleStartInVo inVo) {
        log.info("{}----------交易开始--------", log001);
        UserInfo userInfo = userInfoMapper.selectByUserName(inVo.getUserName());
        if (null == userInfo) {
            log.error("{}--------用户不存在:{}-----", log001, inVo.getUserName());
            return ResponseJson.failure(OneCardConstant.Code_OtherFail, "用户不存在");
        }

        List<BattleInfo> battleInfoList = battleInfoMapper.selectByRoomNumber(inVo.getRoomNumber());

        if (battleInfoList.size() == 0) {
            log.error("{}--------房间不存在:{}-----", log001, inVo.getRoomNumber());
            return ResponseJson.failure(OneCardConstant.Code_OtherFail, "房间不存在");
        }
        if (battleInfoList.size() > 1) {
            log.error("{}--------房间不唯一:{}-----", log001, inVo.getRoomNumber());
            return ResponseJson.failure(OneCardConstant.Code_OtherFail, "房间异常不唯一，请联系管理员");
        }
        BattleInfo battleInfo = battleInfoList.get(0);
        List<UserInfo> players = objectMapper.readValue(battleInfo.getPlayers(), objectMapper.getTypeFactory().constructParametricType(List.class, UserInfo.class));
        if (players.size() < 2) {
            log.error("{}--------房间{}人数不足2人:-----", log001, inVo.getRoomNumber());
            return ResponseJson.failure(OneCardConstant.Code_OtherFail, "房间人数不足2人");
        }
        //生成牌
        List<CardInfo> deck = cardInfoMapper.selectAvailable();
        //洗牌
        Collections.shuffle(deck);
        //发牌
        for (UserInfo player : players) {
            player.setHand(objectMapper.writeValueAsString(getCards(deck, 6)));
        }
        battleInfo.setPlayers(objectMapper.writeValueAsString(players));

        //取出第一张数字牌
        for (int i = 0; i < deck.size(); i++) {
            if (Objects.equals(deck.get(i).getCardType(), OneCardConstant.Card_Type_Digit)) {
                battleInfo.setPlayCard(objectMapper.writeValueAsString(deck.get(i)));
                deck.remove(i);
                break;
            }
        }
        //放进牌堆
        battleInfo.setDeck(objectMapper.writeValueAsString(getCards(deck, deck.size())));
        //第一回合上玩家1
        battleInfo.setTurn(0L);
        //更改状态
        battleInfo.setStatus(OneCardConstant.Battle_Status_battling);

        battleInfoMapper.updateByPrimaryKey(battleInfo);

        BattleInfoSubOutVo battleInfoSubOutVo = getBattleInfoSubOutVo(battleInfo, userInfo);

        BattleStartOutVo outVo = new BattleStartOutVo();
        outVo.setBattleInfoSubOutVo(battleInfoSubOutVo);
        return ResponseJson.ok(outVo);
    }
}
