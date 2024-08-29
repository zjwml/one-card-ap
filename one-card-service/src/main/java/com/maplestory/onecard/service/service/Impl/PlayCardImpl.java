package com.maplestory.onecard.service.service.Impl;

import com.maplestory.onecard.model.domain.BattleInfo;
import com.maplestory.onecard.model.domain.CardInfo;
import com.maplestory.onecard.model.domain.UserInfo;
import com.maplestory.onecard.model.mapper.BattleInfoMapper;
import com.maplestory.onecard.model.mapper.CardInfoMapper;
import com.maplestory.onecard.model.mapper.UserInfoMapper;
import com.maplestory.onecard.service.constant.OneCardConstant;
import com.maplestory.onecard.service.service.PlayCard;
import com.maplestory.onecard.service.vo.PlayCardInVo;
import com.maplestory.onecard.service.vo.PlayCardOutVo;
import com.maplestory.onecard.service.vo.ResponseJson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class PlayCardImpl implements PlayCard {
    private final String log001 = "PlayCardImpl happened:";

    @Autowired
    private BattleInfoMapper battleInfoMapper;

    @Autowired
    private CardInfoMapper cardInfoMapper;

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Override
    public ResponseJson<PlayCardOutVo> doService(PlayCardInVo inVo) {
        log.info("{}----------交易开始--------", log001);

        UserInfo player = userInfoMapper.selectByUserName(inVo.getUserName());
        if (null == player) {
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

        CardInfo playCard = cardInfoMapper.selectByPrimaryKey(Long.valueOf(inVo.getCardId()));
        //检查牌是否能出
        if (this.isUnavailableCard(battleInfo, playCard, player)) {
            log.error("{}--------不能出这张牌:{}-----", log001, inVo.getRoomNumber());
            return ResponseJson.failure(OneCardConstant.Code_OtherFail, "不能出这张牌");
        }
        //开始处理出牌
        //攻击2
        if ("attack2".equals(playCard.getPoint())) {
            battleInfo.setAttackLevel(battleInfo.getAttackLevel() + 2 > 20 ? 20 : battleInfo.getAttackLevel());
            battleInfo.setCardPrevious(String.valueOf(playCard.getId()));
            battleInfo.setCardPlay(String.valueOf(playCard.getId()));
        }
        //攻击3
        if ("attack3".equals(playCard.getPoint())) {
            battleInfo.setAttackLevel(battleInfo.getAttackLevel() + 3 > 20 ? 20 : battleInfo.getAttackLevel());
        }
        //奥兹
        if ("hero".equals(playCard.getPoint()) && "red".equals(playCard.getColor())) {
            battleInfo.setAttackLevel(battleInfo.getAttackLevel() + 2 > 20 ? 20 : battleInfo.getAttackLevel());
        }
        //胡克
        if ("hero".equals(playCard.getPoint()) && "blue".equals(playCard.getColor())) {
            if (!Objects.equals(battleInfo.getPlayer1(), player.getId())) {
                List<String> deck = new ArrayList<>(Arrays.asList(battleInfo.getDeck().split(",")));
                String tmp = getCards(deck, 2);
                battleInfo.setHand1(tmp);
                battleInfo.setDeck(getCards(deck, deck.size()));
            }
            if (!Objects.equals(battleInfo.getPlayer2(), player.getId())) {
                List<String> deck = new ArrayList<>(Arrays.asList(battleInfo.getDeck().split(",")));
                String tmp = getCards(deck, 2);
                battleInfo.setHand2(tmp);
                battleInfo.setDeck(getCards(deck, deck.size()));
            }
            if (!Objects.equals(battleInfo.getPlayer3(), player.getId())) {
                List<String> deck = new ArrayList<>(Arrays.asList(battleInfo.getDeck().split(",")));
                String tmp = getCards(deck, 2);
                battleInfo.setHand3(tmp);
                battleInfo.setDeck(getCards(deck, deck.size()));
            }
            if (!Objects.equals(battleInfo.getPlayer4(), player.getId())) {
                List<String> deck = new ArrayList<>(Arrays.asList(battleInfo.getDeck().split(",")));
                String tmp = getCards(deck, 2);
                battleInfo.setHand4(tmp);
                battleInfo.setDeck(getCards(deck, deck.size()));
            }
        }
        //反转
        if ("attack3".equals(playCard.getPoint())) {
            battleInfo.setDirection(-battleInfo.getDirection());
        }


        PlayCardOutVo outVo = new PlayCardOutVo();
        BeanUtils.copyProperties(battleInfo, outVo);
        return ResponseJson.ok(outVo);
    }

    /**
     * 检查这个牌能不能出
     *
     * @param battleInfo 战斗信息
     * @param card       卡信息
     * @param player     用户
     * @return Boolean
     */
    private boolean isUnavailableCard(BattleInfo battleInfo, CardInfo card, UserInfo player) {
        if (!Objects.equals(battleInfo.getPlayPlayer(), player.getId())) {
            return true;
        }
        if ("black".equals(card.getColor())) {
            return false;
        }
        CardInfo previousCard = cardInfoMapper.selectByPrimaryKey(Long.valueOf(battleInfo.getCardPrevious()));
        if ("hero".equals(previousCard.getPoint()) && !Objects.equals(previousCard.getColor(), card.getColor())) {
            return true;
        }
        if ("attack2".equals(previousCard.getPoint())) {
            if ("attack3".equals(card.getPoint())) {
                return false;
            }
            if ("hero".equals(card.getPoint()) && "red".equals(card.getColor())) {
                return false;
            }
        }
        if ("attack3".equals(previousCard.getPoint())) {
            if ("hero".equals(card.getPoint()) && "red".equals(card.getColor())) {
                return false;
            }
        }

        return !Objects.equals(previousCard.getColor(), card.getColor()) && !Objects.equals(previousCard.getPoint(), card.getPoint());
    }

    private String getCards(List<String> deck, int cardNum) {
        List<String> childDeck = new ArrayList<>(deck.subList(0, cardNum));
        List<String> tmp = deck.subList(0, cardNum);
        tmp.clear();
        StringBuilder result = new StringBuilder(childDeck.get(0));
        for (String item : childDeck) {
            result.append(",").append(item);
        }
        return result.toString();
    }
}
