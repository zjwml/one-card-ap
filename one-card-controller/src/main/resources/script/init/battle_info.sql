-- onecard.battle_info definition

CREATE TABLE `battle_info`
(
    `id`           bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
    `turn`         bigint(20) NOT NULL DEFAULT 0 COMMENT '回合',
    `attack_level` tinyint(4) NOT NULL DEFAULT 0 COMMENT '攻击点数',
    `direction`    tinyint(4) NOT NULL DEFAULT -1 COMMENT '顺序',
    `deck`         varchar(500)        DEFAULT NULL COMMENT '牌堆',
    `play_player`  bigint(20)          DEFAULT NULL COMMENT '出牌者',
    `play_card`    varchar(2)          DEFAULT NULL COMMENT '当前出的牌',
    `room_number`  varchar(4)          DEFAULT NULL COMMENT '房间号',
    `status`       varchar(2)          DEFAULT '00' COMMENT '00-未开始，01-进行中，02-已结束，03-选颜色',
    `players`      varchar(50)         DEFAULT NULL COMMENT '用户ID集合',
    `hands`        varchar(400)        DEFAULT NULL COMMENT '手牌集合',
    PRIMARY KEY (`id`)
) COMMENT ='对战信息';