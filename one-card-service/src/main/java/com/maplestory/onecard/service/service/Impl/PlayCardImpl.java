package com.maplestory.onecard.service.service.Impl;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.maplestory.onecard.model.domain.BattleInfo;
import com.maplestory.onecard.model.domain.CardInfo;
import com.maplestory.onecard.model.domain.UserInfo;
import com.maplestory.onecard.service.constant.OneCardConstant;
import com.maplestory.onecard.service.service.PlayCard;
import com.maplestory.onecard.service.util.ListUtils;
import com.maplestory.onecard.service.vo.BattleInfoSubOutVo;
import com.maplestory.onecard.service.vo.PlayCardInVo;
import com.maplestory.onecard.service.vo.PlayCardOutVo;
import com.maplestory.onecard.service.vo.ResponseJson;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class PlayCardImpl extends CommonService implements PlayCard {
    private final String log001 = "PlayCardImpl happened:";

    @SneakyThrows
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

            addPlayCardIntoDeck(battleInfo);

            battleInfo.setPlayCard(objectMapper.writeValueAsString(cardInfo));
            battleInfo.setPlayPlayer(userInfo.getId());
            battleInfoMapper.updateByPrimaryKey(battleInfo);
            PlayCardOutVo outVo = new PlayCardOutVo();
            BattleInfoSubOutVo battleInfoSubOutVo = getBattleInfoSubOutVo(battleInfo,userInfo);
            outVo.setBattleInfoSubOutVo(battleInfoSubOutVo);
            return ResponseJson.ok(outVo);
        }
        log.info("{}---------房间[{}]，开始处理玩家[{}]出牌", log001, inVo.getRoomNumber(), inVo.getUserName());
        List<String> players = ListUtils.StringToStringList(battleInfo.getPlayers());
        ObjectNode hands = (ObjectNode) objectMapper.readTree(battleInfo.getHands());
        String str = hands.get(String.valueOf(userInfo.getId())).asText();
        List<CardInfo> hand = objectMapper.readValue(str, objectMapper.getTypeFactory().constructParametricType(List.class, CardInfo.class));
        for (int i = 0; i < hand.size(); i++) {
            CardInfo s = hand.get(i);
            if (Objects.equals(s.getId(), cardInfo.getId())) {
                hand.remove(i);
                break;
            }
        }
        log.info("{}---------房间[{}]开始判断玩家[{}]是否赢了", log001, inVo.getRoomNumber(), inVo.getUserName());
        //手牌为空就是赢了
        if (hand.isEmpty()) {
            log.info("{}---------房间[{}]，玩家[{}]赢了，游戏结束", log001, inVo.getRoomNumber(), inVo.getUserName());
            //设置出牌者和结束就行了
            battleInfo.setPlayPlayer(userInfo.getId());
            battleInfo.setStatus(OneCardConstant.Battle_Status_end);
            battleInfoMapper.updateByPrimaryKey(battleInfo);
            PlayCardOutVo outVo = new PlayCardOutVo();
            BattleInfoSubOutVo battleInfoSubOutVo = getBattleInfoSubOutVo(battleInfo,userInfo);
            outVo.setBattleInfoSubOutVo(battleInfoSubOutVo);
            return ResponseJson.ok(outVo);
        }
        //接下来是战斗继续
        log.info("{}---------房间[{}]，玩家[{}]还有手牌，战斗继续", log001, inVo.getRoomNumber(), inVo.getUserName());
        //刚出变色牌
        if ("change".equals(cardInfo.getPoint())) {
            battleInfo.setStatus(OneCardConstant.Battle_Status_changing);

            addPlayCardIntoDeck(battleInfo);
            battleInfo.setPlayCard(objectMapper.writeValueAsString(cardInfo));

            battleInfoMapper.updateByPrimaryKey(battleInfo);
            PlayCardOutVo outVo = new PlayCardOutVo();
            BattleInfoSubOutVo battleInfoSubOutVo = getBattleInfoSubOutVo(battleInfo,userInfo);
            outVo.setBattleInfoSubOutVo(battleInfoSubOutVo);
            return ResponseJson.ok(outVo);
        }
        //反转
        if ("goback".equals(cardInfo.getPoint())) {
            battleInfo.setDirection(-battleInfo.getDirection());

            return nextTurn(userInfo, battleInfo, cardInfo);
        }
        //跳过
        if ("jump".equals(cardInfo.getPoint())) {
            battleInfo.setTurn((battleInfo.getTurn() + players.size() + battleInfo.getDirection()) % players.size());

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
            addTwoToOthers(userInfo, battleInfo);
            return nextTurn(userInfo, battleInfo, cardInfo);
        }
        //伊莉娜
        if ("hero".equals(cardInfo.getPoint()) && "green".equals(cardInfo.getColor())) {
            //检查每个人的手牌，删除绿色的，都放进牌堆
            deleteGreenCard(battleInfo);

            battleInfo.setTurn(battleInfo.getTurn() + players.size() + battleInfo.getDirection() % players.size());
            CardInfo playCard = objectMapper.readValue(battleInfo.getPlayCard(), CardInfo.class);
            if (!"change".equals(playCard.getPoint())) {
                addPlayCardIntoDeck(battleInfo);
            }
            battleInfo.setPlayCard(objectMapper.writeValueAsString(cardInfo));
            battleInfo.setPlayPlayer(userInfo.getId());
            hands = getNewHands(userInfo, battleInfo, cardInfo);
            battleInfo.setHands(objectMapper.writeValueAsString(hands));
            battleInfo.setStatus(OneCardConstant.Battle_Status_changing);

            battleInfoMapper.updateByPrimaryKey(battleInfo);
            PlayCardOutVo outVo = new PlayCardOutVo();
            BattleInfoSubOutVo battleInfoSubOutVo = getBattleInfoSubOutVo(battleInfo,userInfo);
            outVo.setBattleInfoSubOutVo(battleInfoSubOutVo);
            return ResponseJson.ok(outVo);
        }
        //如果是数字牌
        if (cardInfo.getPoint().matches("^\\d$")) {
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
            BattleInfoSubOutVo battleInfoSubOutVo = getBattleInfoSubOutVo(battleInfo,userInfo);
            outVo.setBattleInfoSubOutVo(battleInfoSubOutVo);
            return ResponseJson.ok(outVo);
        }


        PlayCardOutVo outVo = new PlayCardOutVo();
        BattleInfoSubOutVo battleInfoSubOutVo = getBattleInfoSubOutVo(battleInfo,userInfo);
        outVo.setBattleInfoSubOutVo(battleInfoSubOutVo);
        return ResponseJson.ok(outVo);
    }

    @SneakyThrows
    private ResponseJson<PlayCardOutVo> nextTurn(UserInfo userInfo, BattleInfo battleInfo, CardInfo cardInfo) {
        List<String> players = ListUtils.StringToStringList(battleInfo.getPlayers());
        battleInfo.setTurn((battleInfo.getTurn() + players.size() + battleInfo.getDirection()) % players.size());
        CardInfo previousCard = objectMapper.readValue(battleInfo.getPlayCard(), CardInfo.class);
        if (!"change".equals(previousCard.getPoint())) {
            addPlayCardIntoDeck(battleInfo);
        }
        battleInfo.setPlayCard(objectMapper.writeValueAsString(cardInfo));
        battleInfo.setPlayPlayer(userInfo.getId());
        ObjectNode hands = getNewHands(userInfo, battleInfo, cardInfo);
        battleInfo.setHands(hands.asText());

        battleInfoMapper.updateByPrimaryKey(battleInfo);
        PlayCardOutVo outVo = new PlayCardOutVo();
        BattleInfoSubOutVo battleInfoSubOutVo = getBattleInfoSubOutVo(battleInfo,userInfo);
        outVo.setBattleInfoSubOutVo(battleInfoSubOutVo);
        return ResponseJson.ok(outVo);
    }

    @SneakyThrows
    private ObjectNode getNewHands(UserInfo userInfo, BattleInfo battleInfo, CardInfo cardInfo) {
        ObjectNode hands = (ObjectNode) objectMapper.readTree(battleInfo.getHands());
        String str = String.valueOf(hands.get(userInfo.getId().toString()));
        List<CardInfo> hand = objectMapper.readValue(str, objectMapper.getTypeFactory().constructParametricType(List.class, CardInfo.class));
        for (int i = 0; i < hand.size(); i++) {
            CardInfo s = hand.get(i);
            if (Objects.equals(s.getId(), cardInfo.getId())) {
                hand.remove(i);
                break;
            }
        }
        hands.put(userInfo.getId().toString(), objectMapper.writeValueAsString(hand));
        return hands;
    }

    /**
     * 胡克给其他每个人都加2张卡
     *
     * @param player     出胡克的人
     * @param battleInfo 战斗信息
     */
    @SneakyThrows
    private void addTwoToOthers(UserInfo player, BattleInfo battleInfo) {
        List<CardInfo> deck = objectMapper.readValue(battleInfo.getDeck(), objectMapper.getTypeFactory().constructParametricType(List.class, CardInfo.class));
        List<String> players = ListUtils.StringToStringList(battleInfo.getPlayers());
        ObjectNode hands = (ObjectNode) objectMapper.readTree(battleInfo.getHands());

        for (String play : players) {
            if (Objects.equals(play, player.getId().toString())) {
                continue;
            }
            String str = String.valueOf(hands.get(play));
            List<CardInfo> hand = objectMapper.readValue(str, objectMapper.getTypeFactory().constructParametricType(List.class, CardInfo.class));
            hand.addAll(getCards(deck, 2));
            hands.put(play, objectMapper.writeValueAsString(hand));
        }
        battleInfo.setHands(objectMapper.writeValueAsString(hands));
        battleInfo.setDeck(objectMapper.writeValueAsString(deck));
    }

    /**
     * 伊莉娜从手牌删除绿卡，放回牌堆
     *
     * @param battleInfo 战斗信息
     */
    @SneakyThrows
    private void deleteGreenCard(BattleInfo battleInfo) {
        List<CardInfo> deck = objectMapper.readValue(battleInfo.getDeck(), objectMapper.getTypeFactory().constructParametricType(List.class, CardInfo.class));
        List<String> players = ListUtils.StringToStringList(battleInfo.getPlayers());
        ObjectNode hands = (ObjectNode) objectMapper.readTree(battleInfo.getHands());

        for (String play : players) {
            String str = String.valueOf(hands.get(play));
            List<CardInfo> hand = objectMapper.readValue(str, objectMapper.getTypeFactory().constructParametricType(List.class, CardInfo.class));
            for (int i = hand.size() - 1; i >= 0; i--) {
                CardInfo s = hand.get(i);
                //12-24是绿卡
                if (12 < s.getId() && s.getId() < 24) {
                    deck.add(s);
                    hand.remove(i);
                }
            }
            hands.put(play, objectMapper.writeValueAsString(hand));
        }
        battleInfo.setDeck(objectMapper.writeValueAsString(deck));
        battleInfo.setHands(objectMapper.writeValueAsString(hands));
    }

    /**
     * 把上一张卡加入牌堆
     *
     * @param battleInfo 战斗信息
     * @throws RuntimeException jackson错误
     */
    @SneakyThrows
    private void addPlayCardIntoDeck(BattleInfo battleInfo) {
        List<CardInfo> deck = objectMapper.readValue(battleInfo.getDeck(), objectMapper.getTypeFactory().constructParametricType(List.class, CardInfo.class));
        CardInfo playCard = objectMapper.readValue(battleInfo.getPlayCard(), CardInfo.class);
        deck.add(playCard);
        sortByColorThenPoint(deck);
        battleInfo.setDeck(objectMapper.writeValueAsString(deck));
    }

    /**
     * 检查这个牌能不能出
     *
     * @param battleInfo 战斗信息
     * @param card       卡信息
     * @param player     用户
     * @return Boolean
     */
    @SneakyThrows
    private boolean isUnavailableCard(BattleInfo battleInfo, CardInfo card, UserInfo player) {
        List<String> players = ListUtils.StringToStringList(battleInfo.getPlayers());
        if (!Objects.equals(players.get(Math.toIntExact(battleInfo.getTurn())), player.getId().toString())) {
            return true;
        }
        if ("black".equals(card.getColor())) {
            return false;
        }
        CardInfo previousCard = objectMapper.readValue(battleInfo.getPlayCard(), CardInfo.class);
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


}
