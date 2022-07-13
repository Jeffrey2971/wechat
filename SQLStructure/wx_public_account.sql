/*
 Navicat Premium Data Transfer

 Source Server         : @remote_mysql
 Source Server Type    : MySQL
 Source Server Version : 50737
 Source Host           : 120.76.100.173:3306
 Source Schema         : wx_public_account

 Target Server Type    : MySQL
 Target Server Version : 50737
 File Encoding         : 65001

 Date: 13/07/2022 15:17:45
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for basic_user_info
-- ----------------------------
DROP TABLE IF EXISTS `basic_user_info`;
CREATE TABLE `basic_user_info` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `subscribe` int(11) NOT NULL DEFAULT '0',
  `openid` varchar(128) NOT NULL,
  `language` varchar(6) DEFAULT NULL,
  `subscribe_time` datetime NOT NULL,
  `unionid` varchar(256) DEFAULT NULL,
  `remark` varchar(128) DEFAULT NULL,
  `groupid` int(11) DEFAULT '0',
  `tagid_list` varchar(128) DEFAULT NULL,
  `subscribe_scene` varchar(64) NOT NULL DEFAULT 'ADD_SCENE_OTHERS',
  `qr_scene` int(11) NOT NULL DEFAULT '0',
  `qr_scene_str` varchar(256) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `table_name_openid_uindex` (`openid`)
) ENGINE=InnoDB AUTO_INCREMENT=4541 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for share
-- ----------------------------
DROP TABLE IF EXISTS `share`;
CREATE TABLE `share` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `openid` varchar(127) NOT NULL,
  `share_link` varchar(256) DEFAULT 'http://mmbiz.qpic.cn/mmbiz_png/fRGvPLUn66BcibezkicsQd1V25hSDob12ASxstzG0j6aIiac2fNEzudvtTUF5CKL6dXicwHI9Ribx21wxSiagz86YT2w/0?wx_fmt=png',
  `share_total` int(11) NOT NULL DEFAULT '0',
  `media_id` varchar(128) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `openid` (`openid`),
  UNIQUE KEY `share_media_id_uindex` (`media_id`),
  UNIQUE KEY `shareLink` (`share_link`),
  UNIQUE KEY `share_shareLink_uindex` (`share_link`),
  CONSTRAINT `share_ibfk_1` FOREIGN KEY (`openid`) REFERENCES `basic_user_info` (`openid`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=288 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for user_feedback
-- ----------------------------
DROP TABLE IF EXISTS `user_feedback`;
CREATE TABLE `user_feedback` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `title` varchar(32) NOT NULL,
  `message` varchar(512) NOT NULL,
  `way` varchar(6) DEFAULT NULL,
  `details` varchar(128) NOT NULL,
  `openid` varchar(128) NOT NULL,
  `ctime` datetime NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `openid` (`openid`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for user_use_total
-- ----------------------------
DROP TABLE IF EXISTS `user_use_total`;
CREATE TABLE `user_use_total` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `openid` varchar(128) NOT NULL,
  `can_use` int(11) NOT NULL DEFAULT '5',
  `free_user` char(1) NOT NULL DEFAULT 'F',
  `all_use` int(11) NOT NULL DEFAULT '0',
  `free` char(1) NOT NULL DEFAULT 'T',
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_use_total_openid_uindex` (`openid`)
) ENGINE=InnoDB AUTO_INCREMENT=2392 DEFAULT CHARSET=utf8mb4;

SET FOREIGN_KEY_CHECKS = 1;
