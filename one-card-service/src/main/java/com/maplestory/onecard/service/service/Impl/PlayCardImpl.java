package com.maplestory.onecard.service.service.Impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.maplestory.onecard.model.domain.BattleInfo;
import com.maplestory.onecard.model.domain.CardInfo;
import com.maplestory.onecard.model.domain.UserInfo;
import com.maplestory.onecard.model.mapper.BattleInfoMapper;
import com.maplestory.onecard.model.mapper.CardInfoMapper;
import com.maplestory.onecard.model.mapper.UserInfoMapper;
import com.maplestory.onecard.service.constant.OneCardConstant;
import com.maplestory.onecard.service.service.PlayCard;
import com.maplestory.onecard.service.util.ListUtils;
import com.maplestory.onecard.service.vo.PlayCardInVo;
import com.maplestory.onecard.service.vo.PlayCardOutVo;
import com.maplestory.onecard.service.vo.ResponseJson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

    ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public ResponseJson<PlayCardOutVo> doService(PlayCardInVo inVo) {
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

        CardInfo cardInfo = cardInfoMapper.selectByPrimaryKey(Long.valueOf(inVo.getCardId()));
        //检查牌是否能出
        if (this.isUnavailableCard(battleInfo, cardInfo, userInfo)) {
            log.error("{}--------不能出这张牌:{}-----", log001, inVo.getRoomNumber());
            return ResponseJson.failure(OneCardConstant.Code_OtherFail, "不能出这张牌");
        }
        //开始处理出牌
        //先处理非实体牌
        //选好了变色伊莉娜或者是选好了变色牌
        if (OneCardConstant.Battle_Status_changing.equals(battleInfo.getStatus())) {
            battleInfo.setStatus(OneCardConstant.Battle_Status_battling);

            battleInfo.setDeck(battleInfo.getDeck() + "," + battleInfo.getPlayCard());
            battleInfo.setPlayCard(String.valueOf(cardInfo.getId()));

            battleInfoMapper.updateByPrimaryKey(battleInfo);
            PlayCardOutVo outVo = new PlayCardOutVo();
            BeanUtils.copyProperties(battleInfo, outVo);
            return ResponseJson.ok(outVo);
        }
        //判断是否赢了
        List<String> players = ListUtils.StringToStringList(battleInfo.getPlayers());
        ObjectNode hands;
        try {
            hands = (ObjectNode) objectMapper.readTree(battleInfo.getHands());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        String str = hands.get(String.valueOf(userInfo.getId())).toString();
        List<String> hand = ListUtils.StringToStringList(str);
        for (int i = 0; i < hand.size(); i++) {
            String s = hand.get(i);
            if (Objects.equals(s, cardInfo.getId().toString())) {
                hand.remove(i);
                break;
            }
        }
        battleInfo.setHands(hands.asText());
        //手牌为空就是赢了
        if (hand.isEmpty()) {
            //设置出牌者和结束就行了
            battleInfo.setPlayPlayer(userInfo.getId());
            battleInfo.setStatus(OneCardConstant.Battle_Status_end);
            battleInfoMapper.updateByPrimaryKey(battleInfo);
            PlayCardOutVo outVo = new PlayCardOutVo();
            BeanUtils.copyProperties(battleInfo, outVo);
            return ResponseJson.ok(outVo);
        }
        //接下来是战斗继续

        //刚出变色牌
        if ("change".equals(cardInfo.getPoint())) {
            battleInfo.setStatus(OneCardConstant.Battle_Status_changing);

            battleInfo.setDeck(battleInfo.getDeck() + "," + battleInfo.getPlayCard());
            battleInfo.setPlayCard(String.valueOf(cardInfo.getId()));

            battleInfoMapper.updateByPrimaryKey(battleInfo);
            PlayCardOutVo outVo = new PlayCardOutVo();
            BeanUtils.copyProperties(battleInfo, outVo);
            return ResponseJson.ok(outVo);
        }
        //反转
        if ("goback".equals(cardInfo.getPoint())) {
            battleInfo.setDirection(-battleInfo.getDirection());

            return nextTurn(userInfo, battleInfo, cardInfo);
        }
        //跳过
        if ("jump".equals(cardInfo.getPoint())) {
            battleInfo.setTurn(battleInfo.getTurn() + 2L * battleInfo.getDirection());

            return nextTurn(userInfo, battleInfo, cardInfo);
        }
        //攻击2
        if ("attack2".equals(cardInfo.getPoint())) {
            battleInfo.setAttackLevel(battleInfo.getAttackLevel() + 2 > OneCardConstant.Attack_Max ? OneCardConstant.Attack_Max : battleInfo.getAttackLevel());
            return nextTurn(userInfo, battleInfo, cardInfo);
        }
        //攻击3
        if ("attack3".equals(cardInfo.getPoint())) {
            battleInfo.setAttackLevel(battleInfo.getAttackLevel() + 3 > OneCardConstant.Attack_Max ? OneCardConstant.Attack_Max : battleInfo.getAttackLevel());
            return nextTurn(userInfo, battleInfo, cardInfo);
        }
        //奥兹
        if ("hero".equals(cardInfo.getPoint()) && "red".equals(cardInfo.getColor())) {
            battleInfo.setAttackLevel(battleInfo.getAttackLevel() + 5 > OneCardConstant.Attack_Max ? OneCardConstant.Attack_Max : battleInfo.getAttackLevel());
            return nextTurn(userInfo, battleInfo, cardInfo);
        }
        //米哈尔
        if ("hero".equals(cardInfo.getPoint()) && "yellow".equals(cardInfo.getColor())) {
            battleInfo.setAttackLevel(0);
            return nextTurn(userInfo, battleInfo, cardInfo);
        }
        //胡克
        if ("hero".equals(cardInfo.getPoint()) && "blue".equals(cardInfo.getColor())) {
            try {
                addTwoToOthers(userInfo, battleInfo);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            return nextTurn(userInfo, battleInfo, cardInfo);
        }
        //伊莉娜
        if ("hero".equals(cardInfo.getPoint()) && "green".equals(cardInfo.getColor())) {
            //检查每个人的手牌，删除绿色的，都放进牌堆
            try {
                deleteGreenCard(battleInfo);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }

            battleInfo.setTurn(battleInfo.getTurn() + players.size() + battleInfo.getDirection() % players.size());
            if (!"change".equals(battleInfo.getPlayCard())) {
                String deck = battleInfo.getDeck() + ("," + battleInfo.getPlayCard());
                battleInfo.setDeck(deck);
            }
            battleInfo.setPlayCard(String.valueOf(cardInfo.getId()));
            battleInfo.setPlayPlayer(userInfo.getId());
            hands = getNewHands(userInfo, battleInfo, cardInfo);
            battleInfo.setHands(hands.asText());
            battleInfo.setStatus(OneCardConstant.Battle_Status_changing);

            battleInfoMapper.updateByPrimaryKey(battleInfo);
            PlayCardOutVo outVo = new PlayCardOutVo();
            BeanUtils.copyProperties(battleInfo, outVo);
            return ResponseJson.ok(outVo);
        }

        //如果是数字牌
        if (cardInfo.getPoint().matches("^\\d$")) {
            //先攻击
            List<String> deck = ListUtils.StringToStringList(battleInfo.getDeck());
            //超过20张手牌就输了
            if (hand.size() + battleInfo.getAttackLevel() > 20) {
                hands.remove(String.valueOf(userInfo.getId()));
                for (int i = 0; i < players.size(); i++) {
                    String s = players.get(i);
                    if (Objects.equals(s, userInfo.getId().toString())) {
                        players.remove(i);
                        break;
                    }
                }
                for (int i = 0; i < hand.size(); i++) {
                    String card = hand.get(i);
                    if (Objects.equals(card, cardInfo.getId().toString())) {
                        hand.remove(i);
                        break;
                    }
                }
                battleInfo.setTurn(battleInfo.getTurn() + players.size() + battleInfo.getDirection() % players.size());
                battleInfo.setDeck(ListUtils.StringListToString(deck) + "," + ListUtils.StringListToString(hand));
                battleInfo.setPlayCard(cardInfo.getId().toString());
                battleInfo.setPlayers(ListUtils.StringListToString(players));
                battleInfo.setHands(hands.asText());

                battleInfoMapper.updateByPrimaryKey(battleInfo);
                PlayCardOutVo outVo = new PlayCardOutVo();
                BeanUtils.copyProperties(battleInfo, outVo);
                return ResponseJson.ok(outVo);
            }
            nextTurn(userInfo, battleInfo, cardInfo);
        }
        //伊卡尔特
        if ("hero".equals(cardInfo.getPoint()) && "black".equals(cardInfo.getColor())) {
            battleInfo.setTurn(battleInfo.getTurn() + players.size() + battleInfo.getDirection() % players.size());

            battleInfo.setDeck(battleInfo.getDeck() + "," + cardInfo.getId());

            hands = getNewHands(userInfo, battleInfo, cardInfo);
            battleInfo.setHands(hands.asText());

            battleInfoMapper.updateByPrimaryKey(battleInfo);
            PlayCardOutVo outVo = new PlayCardOutVo();
            BeanUtils.copyProperties(battleInfo, outVo);
            return ResponseJson.ok(outVo);
        }


        PlayCardOutVo outVo = new PlayCardOutVo();
        BeanUtils.copyProperties(battleInfo, outVo);
        return ResponseJson.ok(outVo);
    }

    private ResponseJson<PlayCardOutVo> nextTurn(UserInfo userInfo, BattleInfo battleInfo, CardInfo cardInfo) {
        List<String> players = ListUtils.StringToStringList(battleInfo.getPlayers());
        ObjectNode hands;
        battleInfo.setTurn(battleInfo.getTurn() + players.size() + battleInfo.getDirection() % players.size());
        if (!"change".equals(battleInfo.getPlayCard())) {
            String deck = battleInfo.getDeck() + "," + battleInfo.getPlayCard();
            battleInfo.setDeck(deck);
        }
        battleInfo.setPlayCard(String.valueOf(cardInfo.getId()));
        battleInfo.setPlayPlayer(userInfo.getId());
        hands = getNewHands(userInfo, battleInfo, cardInfo);
        battleInfo.setHands(hands.asText());

        battleInfoMapper.updateByPrimaryKey(battleInfo);
        PlayCardOutVo outVo = new PlayCardOutVo();
        BeanUtils.copyProperties(battleInfo, outVo);
        return ResponseJson.ok(outVo);
    }

    private ObjectNode getNewHands(UserInfo userInfo, BattleInfo battleInfo, CardInfo cardInfo) {
        ObjectNode hands;
        try {
            hands = (ObjectNode) objectMapper.readTree(battleInfo.getHands());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        String str = String.valueOf(hands.get(String.valueOf(userInfo.getId())));
        List<String> hand = ListUtils.StringToStringList(str);
        for (int i = 0; i < hand.size(); i++) {
            String s = hand.get(i);
            if (Objects.equals(s, cardInfo.getId().toString())) {
                hand.remove(i);
                break;
            }
        }
        hands.put(userInfo.getId().toString(), hands.asText());
        return hands;
    }

    /**
     * 胡克给其他每个人都加2张卡
     *
     * @param player     出胡克的人
     * @param battleInfo 战斗信息
     */
    private void addTwoToOthers(UserInfo player, BattleInfo battleInfo) throws JsonProcessingException {
        List<String> deck = ListUtils.StringToStringList(battleInfo.getDeck());
        List<String> players = ListUtils.StringToStringList(battleInfo.getPlayers());
        ObjectNode hands = (ObjectNode) objectMapper.readTree(battleInfo.getHands());

        for (String play : players) {
            if (Objects.equals(play, player.getId().toString())) {
                continue;
            }
            String handstr = String.valueOf(hands.get(play));
            handstr += "," + getCards(deck, 2);
            hands.put(play, handstr);
        }
        battleInfo.setHands(hands.asText());
        battleInfo.setDeck(ListUtils.StringListToString(deck));
    }

    /**
     * 伊莉娜从手牌删除绿卡，放回牌堆
     *
     * @param battleInfo 战斗信息
     */
    private void deleteGreenCard(BattleInfo battleInfo) throws JsonProcessingException {
        List<String> deck = ListUtils.StringToStringList(battleInfo.getDeck());
        List<String> players = ListUtils.StringToStringList(battleInfo.getPlayers());
        ObjectNode hands = (ObjectNode) objectMapper.readTree(battleInfo.getHands());

        for (String player : players) {
            String str = String.valueOf(hands.get(player));
            List<String> hand = ListUtils.StringToStringList(str);
            for (int i = hand.size() - 1; i >= 0; i--) {
                String s = hand.get(i);
                //12-24是绿卡
                if (12 < Integer.getInteger(s) && Integer.getInteger(s) < 24) {
                    deck.add(s);
                    hand.remove(i);
                }
            }
            hands.put(player, ListUtils.StringListToString(hand));
        }
        battleInfo.setDeck(ListUtils.StringListToString(deck));
        battleInfo.setHands(hands.asText());
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
        List<String> players = ListUtils.StringToStringList(battleInfo.getPlayers());
        if (!Objects.equals(players.get(Math.toIntExact(battleInfo.getTurn())), player.getId().toString())) {
            return true;
        }
        if ("black".equals(card.getColor())) {
            return false;
        }
        CardInfo previousCard = cardInfoMapper.selectByPrimaryKey(Long.valueOf(battleInfo.getPlayCard()));
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
