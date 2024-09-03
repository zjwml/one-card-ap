package com.maplestory.onecard.service.service.Impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.maplestory.onecard.model.domain.BattleInfo;
import com.maplestory.onecard.model.domain.CardInfo;
import com.maplestory.onecard.model.domain.UserInfo;
import com.maplestory.onecard.model.mapper.BattleInfoMapper;
import com.maplestory.onecard.model.mapper.CardInfoMapper;
import com.maplestory.onecard.model.mapper.UserInfoMapper;
import com.maplestory.onecard.service.util.ListUtils;
import com.maplestory.onecard.service.vo.BattleInfoSubOutVo;
import com.maplestory.onecard.service.vo.BattleQueryOutVo;
import com.maplestory.onecard.service.vo.ResponseJson;
import lombok.SneakyThrows;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public abstract class CommonService {

    protected final String log001 = "CommonService happened:";

    @Autowired
    protected BattleInfoMapper battleInfoMapper;

    @Autowired
    protected CardInfoMapper cardInfoMapper;

    @Autowired
    protected UserInfoMapper userInfoMapper;

    protected ObjectMapper objectMapper = new ObjectMapper();

    protected List<CardInfo> getCards(List<CardInfo> deck, Integer cardNum) {
        List<CardInfo> childDeck = new ArrayList<>(deck.subList(0, cardNum));
        List<CardInfo> tmp = deck.subList(0, cardNum);
        tmp.clear();
        return childDeck;
    }

    protected String getCardsId(List<String> deck, int cardNum) {
        List<String> childDeck = new ArrayList<>(deck.subList(0, cardNum));
        List<String> tmp = deck.subList(0, cardNum);
        tmp.clear();
        StringBuilder result = new StringBuilder(childDeck.get(0));
        for (String item : childDeck) {
            result.append(",").append(item);
        }
        return result.toString();
    }

    protected List<CardInfo> sortByColorThenPoint(List<CardInfo> hand) {
        hand.sort(Comparator.comparing(CardInfo::getColor).thenComparing(CardInfo::getPoint));
        return hand;
    }

    @SneakyThrows
    protected BattleInfoSubOutVo getBattleInfoSubOutVo(BattleInfo battleInfo, UserInfo userInfo) {
        BattleInfoSubOutVo subOutVo = new BattleInfoSubOutVo();

        BeanUtils.copyProperties(battleInfo, subOutVo);
        if (StringUtils.isNotBlank(battleInfo.getPlayCard())) {
            CardInfo playCard = objectMapper.readValue(battleInfo.getPlayCard(), CardInfo.class);
            subOutVo.setPlayCard(playCard);
        }
        if (StringUtils.isNotBlank(battleInfo.getPlayers())) {
            List<String> players = ListUtils.StringToStringList(battleInfo.getPlayers());
            List<UserInfo> userInfos = new ArrayList<>();
            for (String player : players) {
                userInfos.add(userInfoMapper.selectByPrimaryKey(Long.valueOf(player)));
            }
            subOutVo.setPlayers(userInfos);
        }
        if (StringUtils.isNotBlank(battleInfo.getHands())) {
            ObjectNode objectNode = (ObjectNode) objectMapper.readTree(battleInfo.getHands());
            ObjectNode hands = objectMapper.createObjectNode();
            Iterator<Map.Entry<String, JsonNode>> iterator = objectNode.fields();
            while (iterator.hasNext()) {
                Map.Entry<String, JsonNode> field = iterator.next();
                String fieldName = field.getKey();
                String fieldValue = field.getValue().asText();
                List<CardInfo> hand = objectMapper.readValue(fieldValue, objectMapper.getTypeFactory().constructParametricType(List.class, CardInfo.class));
                if (fieldName.equals(userInfo.getId().toString())) {
                    hands.put(fieldName, fieldValue);
                } else {
                    hands.put(fieldName, hand.size());
                }
            }
            subOutVo.setHands(hands);
        }


        return subOutVo;
    }
}
