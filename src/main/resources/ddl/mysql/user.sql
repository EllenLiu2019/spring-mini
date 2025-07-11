DROP TABLE IF EXISTS `user`;
create table `user` (
    `id`                        BIGINT         NOT NULL   AUTO_INCREMENT ,
    `name`                      VARCHAR(60)    NOT NULL,
    `birthday`                  TIMESTAMP      NOT NULL,
    PRIMARY KEY(`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

