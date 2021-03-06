/*
 * Copyright 1999-2015 dangdang.com.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * </p>
 */

package com.dangdang.ddframe.rdb.integrate.db;

import com.dangdang.ddframe.rdb.integrate.AbstractDBUnitTest;
import com.dangdang.ddframe.rdb.integrate.fixture.MultipleKeysModuloDatabaseShardingAlgorithm;
import com.dangdang.ddframe.rdb.sharding.api.rule.BindingTableRule;
import com.dangdang.ddframe.rdb.sharding.api.rule.DataSourceRule;
import com.dangdang.ddframe.rdb.sharding.api.rule.ShardingRule;
import com.dangdang.ddframe.rdb.sharding.api.rule.TableRule;
import com.dangdang.ddframe.rdb.sharding.api.strategy.database.DatabaseShardingStrategy;
import com.dangdang.ddframe.rdb.sharding.api.strategy.table.NoneTableShardingAlgorithm;
import com.dangdang.ddframe.rdb.sharding.api.strategy.table.TableShardingStrategy;
import com.dangdang.ddframe.rdb.sharding.id.generator.fixture.IncrementIdGenerator;
import com.dangdang.ddframe.rdb.sharding.jdbc.ShardingDataSource;
import org.junit.AfterClass;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public abstract class AbstractShardingDataBasesOnlyDBUnitTest extends AbstractDBUnitTest {
    
    private static boolean isShutdown;
    
    private static ShardingDataSource shardingDataSource;
    
    @Override
    protected List<String> getSchemaFiles() {
        return Arrays.asList(
                "integrate/schema/db/db_0.sql", 
                "integrate/schema/db/db_1.sql", 
                "integrate/schema/db/db_2.sql", 
                "integrate/schema/db/db_3.sql", 
                "integrate/schema/db/db_4.sql", 
                "integrate/schema/db/db_5.sql", 
                "integrate/schema/db/db_6.sql", 
                "integrate/schema/db/db_7.sql", 
                "integrate/schema/db/db_8.sql", 
                "integrate/schema/db/db_9.sql");
    }
    
    @Override
    protected List<String> getDataSetFiles() {
        return Arrays.asList(
                "integrate/dataset/db/init/db_0.xml", 
                "integrate/dataset/db/init/db_1.xml", 
                "integrate/dataset/db/init/db_2.xml", 
                "integrate/dataset/db/init/db_3.xml", 
                "integrate/dataset/db/init/db_4.xml", 
                "integrate/dataset/db/init/db_5.xml", 
                "integrate/dataset/db/init/db_6.xml", 
                "integrate/dataset/db/init/db_7.xml", 
                "integrate/dataset/db/init/db_8.xml", 
                "integrate/dataset/db/init/db_9.xml");
    }
    
    protected final ShardingDataSource getShardingDataSource() {
        if (null != shardingDataSource && !isShutdown) {
            return shardingDataSource;
        }
        isShutdown = false;
        DataSourceRule dataSourceRule = new DataSourceRule(createDataSourceMap("dataSource_%s"));
        TableRule orderTableRule = TableRule.builder("t_order").dataSourceRule(dataSourceRule).autoIncrementColumns("user_id").tableIdGenerator(IncrementIdGenerator.class).build();
        TableRule orderItemTableRule = TableRule.builder("t_order_item").dataSourceRule(dataSourceRule).build();
        ShardingRule shardingRule = ShardingRule.builder().dataSourceRule(dataSourceRule).tableRules(Arrays.asList(orderTableRule, orderItemTableRule))
                .bindingTableRules(Collections.singletonList(new BindingTableRule(Arrays.asList(orderTableRule, orderItemTableRule))))
                .databaseShardingStrategy(new DatabaseShardingStrategy(Collections.singletonList("user_id"), new MultipleKeysModuloDatabaseShardingAlgorithm()))
                .tableShardingStrategy(new TableShardingStrategy(Collections.singletonList("order_id"), new NoneTableShardingAlgorithm())).build();
        shardingDataSource = new ShardingDataSource(shardingRule);
        return shardingDataSource;
    }
    
    @AfterClass
    public static void clear() {
        isShutdown = true;
        shardingDataSource.shutdown();
    }
}
