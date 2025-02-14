// Licensed to the Apache Software Foundation (ASF) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The ASF licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

suite("test_date_key") {
    sql "DROP TABLE IF EXISTS `test_date_key`"
    sql """
        create table `test_date_key` (
            `k1` datev1, `k2` int,
            INDEX idx_k1 (`k1`) USING BITMAP,
        ) duplicate key(`k1`)
        distributed by hash(k2) buckets 3
        properties("replication_num" = "1");
    """
    sql """ insert into `test_date_key` values("2016-11-04", 1); """
    sql """ insert into `test_date_key` values("2016-11-05", 2); """
    sql """ insert into `test_date_key` values("2016-12-06", 3); """
    sql """ insert into `test_date_key` values("2016-12-07", 4); """
    sql """ insert into `test_date_key` values("2017-02-05", 5); """
    sql """ insert into `test_date_key` values("2017-08-22", 6); """
    sql """ insert into `test_date_key` values("2016-11-04", 1); """
    sql """ insert into `test_date_key` values("2016-11-05", 22); """
    sql """ insert into `test_date_key` values("2016-12-06", 3); """
    sql """ insert into `test_date_key` values("2016-12-07", 44); """
    sql """ insert into `test_date_key` values("2017-02-05", 5); """
    sql """ insert into `test_date_key` values("2017-08-22", 666); """

    qt_sql1 """
        select * from `test_date_key` where `k1` = "2016-11-04" order by `k1`, `k2`;
    """

    qt_sql2 """
        select * from `test_date_key` where `k1` <> "2016-11-05" order by `k1`, `k2`;
    """

    qt_sql3 """
        select * from `test_date_key` where `k1` = "2016-11-05" or `k1` = "2016-11-04" order by `k1`, `k2`;
    """

    qt_sql_in """
        select * from `test_date_key` where `k1` in ("2016-11-05", "2016-11-04", "2017-08-22", "2017-02-05")  order by `k1`, `k2`;
    """

    qt_sql_not_in """
        select * from `test_date_key` where `k1` not in ("2016-11-05", "2016-11-04", "2017-08-22", "2017-02-05")  order by `k1`, `k2`;
    """

    sql "DROP TABLE IF EXISTS `test_date_distributed`"
    sql """
        create table `test_date_distributed` (
            `k1` int, `k2` datev1
        ) duplicate key(`k1`)
        distributed by hash(k2) buckets 3
        properties("replication_num" = "1");
    """
    sql """ insert into `test_date_distributed` values(1, "2016-11-04"); """
    sql """ insert into `test_date_distributed` values(2, "2016-11-05"); """
    sql """ insert into `test_date_distributed` values(3, "2016-12-06"); """
    sql """ insert into `test_date_distributed` values(4, "2016-12-07"); """
    sql """ insert into `test_date_distributed` values(5, "2017-02-05"); """
    sql """ insert into `test_date_distributed` values(6, "2017-08-22"); """
    sql """ insert into `test_date_distributed` values(1, "2016-11-04"); """
    sql """ insert into `test_date_distributed` values(22, "2016-11-05");"""
    sql """ insert into `test_date_distributed` values(3, "2016-12-06"); """
    sql """ insert into `test_date_distributed` values(44, "2016-12-07");"""
    sql """ insert into `test_date_distributed` values(5, "2017-02-05"); """
    sql """ insert into `test_date_distributed` values(6, "2017-08-22"); """

    qt_sql_distribute_1 """
        select * from `test_date_distributed` where `k2` = "2016-11-04" order by `k1`, `k2`;
    """

    qt_sql_distribute_2 """
        select * from `test_date_distributed` where `k2` <> "2016-11-05" order by `k1`, `k2`;
    """

    qt_sql_distribute_3 """
        select * from `test_date_distributed` where `k2` = "2016-11-05" or `k2` = "2016-11-04" order by `k1`, `k2`;
    """

    qt_sql_distribute_in """
        select * from `test_date_distributed` where `k2` in ("2016-11-05", "2016-11-04", "2017-08-22", "2017-02-05")  order by `k1`, `k2`;
    """

    qt_sql_distribute_not_in """
        select * from `test_date_distributed` where `k2` not in ("2016-11-05", "2016-11-04", "2017-08-22", "2017-02-05")  order by `k1`, `k2`;
    """

    sql "DROP TABLE IF EXISTS `test_date_partition`"
    sql """
        create table `test_date_partition` (
            `k1` int, `k2` datev1,
            INDEX idx_k2 (`k2`) USING BITMAP
        ) duplicate key(`k1`)
        PARTITION BY range(`k2`)(
            PARTITION p_1610 VALUES [('2016-10-01'), ('2016-10-31')),
            PARTITION p_1611 VALUES [('2016-11-01'), ('2016-11-30')),
            PARTITION p_1612 VALUES [('2016-12-01'), ('2016-12-31')),
            PARTITION p_1702 VALUES [('2017-02-01'), ('2017-02-28')),
            PARTITION p_1708 VALUES [('2017-08-01'), ('2017-08-31'))
        )
        distributed by hash(`k1`) buckets 3
        properties(
            "replication_num" = "1"
        );
    """

    sql """ insert into `test_date_partition` values(1, "2016-11-04"); """
    sql """ insert into `test_date_partition` values(2, "2016-11-05"); """
    sql """ insert into `test_date_partition` values(3, "2016-12-06"); """
    sql """ insert into `test_date_partition` values(4, "2016-12-07"); """
    sql """ insert into `test_date_partition` values(5, "2017-02-05"); """
    sql """ insert into `test_date_partition` values(6, "2017-08-22"); """
    sql """ insert into `test_date_partition` values(1, "2016-11-04"); """
    sql """ insert into `test_date_partition` values(22, "2016-11-05");"""
    sql """ insert into `test_date_partition` values(3, "2016-12-06"); """
    sql """ insert into `test_date_partition` values(44, "2016-12-07");"""
    sql """ insert into `test_date_partition` values(5, "2017-02-05"); """
    sql """ insert into `test_date_partition` values(6, "2017-08-22"); """

    qt_sql_partition_1 """
        select * from `test_date_partition` where `k2` = "2016-11-04" order by `k1`, `k2`;
    """

    qt_sql_partition_2 """
        select * from `test_date_partition` where `k2` <> "2016-11-05" order by `k1`, `k2`;
    """

    qt_sql_partition_3 """
        select * from `test_date_partition` where `k2` = "2016-11-05" or `k2` = "2016-11-04" order by `k1`, `k2`;
    """

    qt_sql_partition_in """
        select * from `test_date_partition` where `k2` in ("2016-11-05", "2016-11-04", "2017-08-22", "2017-02-05")  order by `k1`, `k2`;
    """

    qt_sql_partition_not_in """
        select * from `test_date_partition` where `k2` not in ("2016-11-05", "2016-11-04", "2017-08-22", "2017-02-05")  order by `k1`, `k2`;
    """

    qt_join_rf """
        select *
        from `test_date_key` `t1`, `test_date_distributed` `t2`
        where `t1`.`k1` = `t2`.`k2` and `t1`.`k2` % 2 = 0 order by `t1`.`k1`, `t1`.`k2`, `t2`.`k1`, `t2`.`k2`;
    """

    qt_join_rf2 """
        select *
        from `test_date_key` `t1`, `test_date_distributed` `t2`
        where `t1`.`k1` = `t2`.`k2` and `t2`.`k1` % 2 = 0 order by `t1`.`k1`, `t1`.`k2`, `t2`.`k1`, `t2`.`k2`;
    """

    qt_join_rf3 """
        select *
        from `test_date_key` `t1`, `test_date_partition` `t2`
        where `t1`.`k1` = `t2`.`k2` and `t2`.`k1` % 2 = 0 order by `t1`.`k1`, `t1`.`k2`, `t2`.`k1`, `t2`.`k2`;
    """

    sql """
        delete from `test_date_key` where `k1` = '2016-12-06';
    """

    sql """
        delete from `test_date_distributed` where `k2` = '2016-12-06';
    """

    sql """
        delete from `test_date_partition` where `k2` = '2016-12-06';
    """

    qt_key_after_del """
        select * from `test_date_key` order by `k1`, `k2`;
    """

    qt_distributed_after_del """
        select * from `test_date_distributed` order by `k1`, `k2`;
    """

    qt_partition_after_del """
        select * from `test_date_partition` order by `k1`, `k2`;
    """
}
