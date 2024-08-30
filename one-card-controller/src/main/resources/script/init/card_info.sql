-- onecard.card_info definition

CREATE TABLE `card_info`
(
    `id`          bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
    `color`       varchar(10)         DEFAULT NULL COMMENT '颜色',
    `point`       varchar(20)         DEFAULT NULL COMMENT '点数',
    `description` varchar(100)        DEFAULT NULL COMMENT '描述',
    `image`       varchar(100)        DEFAULT NULL COMMENT '图片路径',
    `available`   tinyint(1) NOT NULL DEFAULT 1 COMMENT '1-可进牌堆，0-不可进',
    `card_type`   tinyint(4)          DEFAULT NULL COMMENT '0-数字牌，1-功能牌，2-变色伊莉娜，3-卡背',
    PRIMARY KEY (`id`)
)COMMENT ='卡牌信息';